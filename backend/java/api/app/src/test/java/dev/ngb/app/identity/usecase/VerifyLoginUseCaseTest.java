package dev.ngb.app.identity.usecase;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.port.TokenProvider;
import dev.ngb.app.identity.application.service.AccountSessionTokenService;
import dev.ngb.app.identity.application.usecase.authentication.verify_login.VerifyLoginUseCase;
import dev.ngb.app.identity.application.usecase.authentication.verify_login.dto.VerifyLoginRequest;
import dev.ngb.app.identity.usecase.support.IdentityUseCaseTestFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.domain.identity.model.otp.OtpChannel;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.repository.AccountDeviceRepository;
import dev.ngb.domain.identity.repository.AccountLoginHistoryRepository;
import dev.ngb.domain.identity.repository.AccountOtpRepository;
import dev.ngb.domain.identity.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerifyLoginUseCase")
class VerifyLoginUseCaseTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountDeviceRepository accountDeviceRepository;
    @Mock
    private AccountOtpRepository accountOtpRepository;
    @Mock
    private AccountLoginHistoryRepository accountLoginHistoryRepository;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private AccountSessionTokenService accountSessionTokenService;

    @InjectMocks
    private VerifyLoginUseCase useCase;

    @Test
    @DisplayName("Invalid verification token → INVALID_VERIFICATION_TOKEN")
    void executeWhenTokenInvalidThrowsInvalidVerificationToken() {
        var req = new VerifyLoginRequest("bad", "123456");
        when(tokenProvider.parseVerificationToken("bad")).thenThrow(new IllegalArgumentException("bad sig"));

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(req, IdentityUseCaseTestFixtures.IP));

        assertThat(ex.getError()).isEqualTo(AccountError.INVALID_VERIFICATION_TOKEN);
    }

    @Test
    @DisplayName("Account missing → ACCOUNT_NOT_FOUND")
    void executeWhenAccountMissingThrowsNotFound() {
        var req = new VerifyLoginRequest("tok", "123456");
        when(tokenProvider.parseVerificationToken("tok")).thenReturn(new TokenProvider.VerificationClaims(1L, 50L));
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(req, IdentityUseCaseTestFixtures.IP));

        assertThat(ex.getError()).isEqualTo(AccountError.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("No active login OTP → INVALID_OTP")
    void executeWhenNoLoginOtpThrowsInvalidOtp() {
        var account = IdentityUseCaseTestFixtures.activeAccount(1L);
        var req = new VerifyLoginRequest("tok", "123456");
        when(tokenProvider.parseVerificationToken("tok")).thenReturn(new TokenProvider.VerificationClaims(1L, 50L));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountOtpRepository.findLatestActiveByAccountIdAndPurpose(1L, OtpPurpose.LOGIN)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(req, IdentityUseCaseTestFixtures.IP));

        assertThat(ex.getError()).isEqualTo(AccountError.INVALID_OTP);
    }

    @Test
    @DisplayName("Device row not for account → INVALID_VERIFICATION_TOKEN")
    void executeWhenDeviceWrongAccountThrowsInvalidVerificationToken() {
        var account = IdentityUseCaseTestFixtures.activeAccount(1L);
        AccountOtp otp = AccountOtp.create(1L, "123456", OtpPurpose.LOGIN, OtpChannel.EMAIL);
        var device = IdentityUseCaseTestFixtures.deviceRow(50L, 999L, "fp", false);
        var req = new VerifyLoginRequest("tok", "123456");

        when(tokenProvider.parseVerificationToken("tok")).thenReturn(new TokenProvider.VerificationClaims(1L, 50L));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountOtpRepository.findLatestActiveByAccountIdAndPurpose(1L, OtpPurpose.LOGIN)).thenReturn(Optional.of(otp));
        when(accountDeviceRepository.findById(50L)).thenReturn(Optional.of(device));

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(req, IdentityUseCaseTestFixtures.IP));

        assertThat(ex.getError()).isEqualTo(AccountError.INVALID_VERIFICATION_TOKEN);
    }

    @Test
    @DisplayName("Valid OTP → tokens + device marked trusted")
    void executeWhenValidReturnsTokensAndMarksTrustedDevice() {
        var account = IdentityUseCaseTestFixtures.activeAccount(1L);
        AccountOtp otp = AccountOtp.create(1L, "123456", OtpPurpose.LOGIN, OtpChannel.EMAIL);
        var device = IdentityUseCaseTestFixtures.deviceRow(50L, 1L, "fp", false);
        var req = new VerifyLoginRequest("tok", "123456");
        var tokens = new AuthTokenResponse("a", "r", 3600, account.getUuid());

        when(tokenProvider.parseVerificationToken("tok")).thenReturn(new TokenProvider.VerificationClaims(1L, 50L));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountOtpRepository.findLatestActiveByAccountIdAndPurpose(1L, OtpPurpose.LOGIN)).thenReturn(Optional.of(otp));
        when(accountDeviceRepository.findById(50L)).thenReturn(Optional.of(device));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        when(accountSessionTokenService.openSessionAndIssueTokens(eq(account), eq(50L), eq(IdentityUseCaseTestFixtures.IP)))
                .thenReturn(tokens);

        AuthTokenResponse result = useCase.execute(req, IdentityUseCaseTestFixtures.IP);

        assertThat(result).isSameAs(tokens);
        assertThat(device.getIsTrusted()).isTrue();
        verify(accountDeviceRepository).save(device);
        verify(accountOtpRepository).save(otp);
        verify(accountSessionTokenService).openSessionAndIssueTokens(account, 50L, IdentityUseCaseTestFixtures.IP);
    }
}
