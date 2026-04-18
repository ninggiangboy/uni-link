package dev.ngb.app.identity.usecase;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.service.AccountSessionTokenService;
import dev.ngb.app.identity.application.usecase.registration.verify_email.VerifyEmailUseCase;
import dev.ngb.app.identity.application.usecase.registration.verify_email.dto.VerifyEmailRequest;
import dev.ngb.app.identity.usecase.support.IdentityUseCaseTestFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.AccountDevice;
import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.domain.identity.model.otp.OtpChannel;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.repository.AccountDeviceRepository;
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
@DisplayName("VerifyEmailUseCase")
class VerifyEmailUseCaseTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountDeviceRepository accountDeviceRepository;
    @Mock
    private AccountOtpRepository accountOtpRepository;
    @Mock
    private AccountSessionTokenService accountSessionTokenService;

    @InjectMocks
    private VerifyEmailUseCase useCase;

    @Test
    @DisplayName("Account not found → ACCOUNT_NOT_FOUND")
    void executeWhenAccountNotFoundThrows() {
        var req = new VerifyEmailRequest(IdentityUseCaseTestFixtures.EMAIL, "123456", IdentityUseCaseTestFixtures.device());
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(req, IdentityUseCaseTestFixtures.IP));

        assertThat(ex.getError()).isEqualTo(AccountError.ACCOUNT_NOT_FOUND);
    }

    @Test
    @DisplayName("Not pending (already verified) → EMAIL_ALREADY_VERIFIED")
    void executeWhenNotPendingThrowsEmailAlreadyVerified() {
        var req = new VerifyEmailRequest(IdentityUseCaseTestFixtures.EMAIL, "123456", IdentityUseCaseTestFixtures.device());
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL))
                .thenReturn(Optional.of(IdentityUseCaseTestFixtures.activeAccount(1L)));

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(req, IdentityUseCaseTestFixtures.IP));

        assertThat(ex.getError()).isEqualTo(AccountError.EMAIL_ALREADY_VERIFIED);
    }

    @Test
    @DisplayName("No active registration OTP → INVALID_OTP")
    void executeWhenNoActiveOtpThrowsInvalidOtp() {
        var req = new VerifyEmailRequest(IdentityUseCaseTestFixtures.EMAIL, "123456", IdentityUseCaseTestFixtures.device());
        var pending = IdentityUseCaseTestFixtures.pendingAccount(10L);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(pending));
        when(accountOtpRepository.findLatestActiveByAccountIdAndPurpose(10L, OtpPurpose.REGISTRATION)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(req, IdentityUseCaseTestFixtures.IP));

        assertThat(ex.getError()).isEqualTo(AccountError.INVALID_OTP);
    }

    @Test
    @DisplayName("Valid OTP → open session + tokens")
    void executeWhenOtpValidOpensSessionAndReturnsTokens() {
        var req = new VerifyEmailRequest(IdentityUseCaseTestFixtures.EMAIL, "123456", IdentityUseCaseTestFixtures.device());
        var pending = IdentityUseCaseTestFixtures.pendingAccount(10L);
        AccountOtp otp = AccountOtp.create(10L, "123456", OtpPurpose.REGISTRATION, OtpChannel.EMAIL);

        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(pending));
        when(accountOtpRepository.findLatestActiveByAccountIdAndPurpose(10L, OtpPurpose.REGISTRATION)).thenReturn(Optional.of(otp));
        when(accountDeviceRepository.save(any(AccountDevice.class))).thenAnswer(inv -> {
            AccountDevice d = inv.getArgument(0);
            return AccountDevice.reconstruct(
                    77L,
                    d.getUuid(),
                    null,
                    d.getCreatedAt(),
                    null,
                    null,
                    d.getAccountId(),
                    d.getDeviceType(),
                    d.getDeviceName(),
                    d.getFingerprint(),
                    d.getUserAgent(),
                    d.getPushToken(),
                    d.getLastActiveAt(),
                    d.getIsTrusted()
            );
        });
        var tokenResponse = new AuthTokenResponse("access", "refresh", 3600, pending.getUuid());
        when(accountSessionTokenService.openSessionAndIssueTokens(any(), eq(77L), eq(IdentityUseCaseTestFixtures.IP)))
                .thenReturn(tokenResponse);

        AuthTokenResponse result = useCase.execute(req, IdentityUseCaseTestFixtures.IP);

        assertThat(result).isSameAs(tokenResponse);
        verify(accountRepository).save(pending);
        verify(accountOtpRepository).save(otp);
        verify(accountSessionTokenService).openSessionAndIssueTokens(pending, 77L, IdentityUseCaseTestFixtures.IP);
    }
}
