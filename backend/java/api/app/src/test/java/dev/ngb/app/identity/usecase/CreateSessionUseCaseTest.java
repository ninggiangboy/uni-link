package dev.ngb.app.identity.usecase;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.port.PasswordEncoder;
import dev.ngb.app.identity.application.port.TokenProvider;
import dev.ngb.app.identity.application.service.AccountOtpDeliveryService;
import dev.ngb.app.identity.application.service.AccountSessionTokenService;
import dev.ngb.app.identity.application.usecase.authentication.login_account.CreateSessionUseCase;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.CreateSessionRequest;
import dev.ngb.app.identity.usecase.support.IdentityUseCaseTestFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.auth.AccountDevice;
import dev.ngb.domain.identity.model.auth.AccountStatus;
import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.domain.identity.model.otp.OtpChannel;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.model.session.AccountLoginHistory;
import dev.ngb.domain.identity.model.session.LoginResult;
import dev.ngb.domain.identity.repository.AccountDeviceRepository;
import dev.ngb.domain.identity.repository.AccountLoginHistoryRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateSessionUseCase")
class CreateSessionUseCaseTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountDeviceRepository accountDeviceRepository;
    @Mock
    private AccountLoginHistoryRepository accountLoginHistoryRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private AccountOtpDeliveryService accountOtpDeliveryService;
    @Mock
    private AccountSessionTokenService accountSessionTokenService;

    @InjectMocks
    private CreateSessionUseCase useCase;

    private CreateSessionRequest request() {
        return new CreateSessionRequest(IdentityUseCaseTestFixtures.EMAIL, "password", IdentityUseCaseTestFixtures.device());
    }

    @Test
    @DisplayName("Unknown email → INVALID_CREDENTIALS")
    void executeWhenAccountMissingThrowsInvalidCredentials() {
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.empty());

        var ex = assertThrows(DomainException.class, () -> useCase.execute(request(), IdentityUseCaseTestFixtures.IP));

        assertThat(ex.getError()).isEqualTo(AccountError.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("Wrong password → login history + INVALID_CREDENTIALS")
    void executeWhenPasswordWrongRecordsFailureAndThrowsInvalidCredentials() {
        var account = IdentityUseCaseTestFixtures.activeAccount(1L);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("password", "stored-hash")).thenReturn(false);

        var ex = assertThrows(DomainException.class, () -> useCase.execute(request(), IdentityUseCaseTestFixtures.IP));

        assertThat(ex.getError()).isEqualTo(AccountError.INVALID_CREDENTIALS);
        verify(accountLoginHistoryRepository).save(any(AccountLoginHistory.class));
    }

    @Test
    @DisplayName("Pending account → ACCOUNT_PENDING")
    void executeWhenPendingThrowsAccountPending() {
        var account = IdentityUseCaseTestFixtures.pendingAccount(1L);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("password", "hash")).thenReturn(true);

        var ex = assertThrows(DomainException.class, () -> useCase.execute(request(), IdentityUseCaseTestFixtures.IP));

        assertThat(ex.getError()).isEqualTo(AccountError.ACCOUNT_PENDING);
    }

    @Test
    @DisplayName("Suspended account → ACCOUNT_SUSPENDED")
    void executeWhenSuspendedThrows() {
        var account = IdentityUseCaseTestFixtures.accountWithStatus(1L, AccountStatus.SUSPENDED);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("password", "stored-hash")).thenReturn(true);

        var ex = assertThrows(DomainException.class, () -> useCase.execute(request(), IdentityUseCaseTestFixtures.IP));

        assertThat(ex.getError()).isEqualTo(AccountError.ACCOUNT_SUSPENDED);
        var historyCaptor = org.mockito.ArgumentCaptor.forClass(AccountLoginHistory.class);
        verify(accountLoginHistoryRepository).save(historyCaptor.capture());
        assertThat(historyCaptor.getValue().getResult()).isEqualTo(LoginResult.BLOCKED);
    }

    @Test
    @DisplayName("New device → LOGIN OTP + verification token (no session yet)")
    void executeWhenNewDeviceSendsOtpAndReturnsVerificationToken() {
        var account = IdentityUseCaseTestFixtures.activeAccount(1L);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("password", "stored-hash")).thenReturn(true);
        when(accountDeviceRepository.findByAccountIdAndFingerprint(1L, IdentityUseCaseTestFixtures.FINGERPRINT))
                .thenReturn(Optional.empty());
        when(accountDeviceRepository.save(any(AccountDevice.class))).thenAnswer(inv -> {
            AccountDevice d = inv.getArgument(0);
            return AccountDevice.reconstruct(
                    50L,
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
        var otp = AccountOtp.create(1L, "123456", OtpPurpose.LOGIN, OtpChannel.EMAIL);
        when(accountOtpDeliveryService.sendEmailOtp(1L, IdentityUseCaseTestFixtures.EMAIL, OtpPurpose.LOGIN)).thenReturn(otp);
        when(tokenProvider.generateVerificationToken(1L, 50L, otp.getUuid())).thenReturn("verif-token");

        var response = useCase.execute(request(), IdentityUseCaseTestFixtures.IP);

        assertThat(response.requiresVerification()).isTrue();
        assertThat(response.verificationToken()).isEqualTo("verif-token");
        verify(accountOtpDeliveryService).sendEmailOtp(1L, IdentityUseCaseTestFixtures.EMAIL, OtpPurpose.LOGIN);
        verifyNoInteractions(accountSessionTokenService);
    }

    @Test
    @DisplayName("2FA on known device → LOGIN OTP + verification token")
    void executeWhen2faOnKnownDeviceSendsOtpAndReturnsVerificationToken() {
        var account = IdentityUseCaseTestFixtures.activeAccountWith2Fa(1L);
        var device = IdentityUseCaseTestFixtures.deviceRow(50L, 1L, IdentityUseCaseTestFixtures.FINGERPRINT, false);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("password", "stored-hash")).thenReturn(true);
        when(accountDeviceRepository.findByAccountIdAndFingerprint(1L, IdentityUseCaseTestFixtures.FINGERPRINT))
                .thenReturn(Optional.of(device));
        when(accountDeviceRepository.save(any(AccountDevice.class))).thenAnswer(inv -> inv.getArgument(0));
        var otp = AccountOtp.create(1L, "123456", OtpPurpose.LOGIN, OtpChannel.EMAIL);
        when(accountOtpDeliveryService.sendEmailOtp(1L, IdentityUseCaseTestFixtures.EMAIL, OtpPurpose.LOGIN)).thenReturn(otp);
        when(tokenProvider.generateVerificationToken(1L, 50L, otp.getUuid())).thenReturn("v2");

        var response = useCase.execute(request(), IdentityUseCaseTestFixtures.IP);

        assertThat(response.requiresVerification()).isTrue();
        verify(accountOtpDeliveryService).sendEmailOtp(1L, IdentityUseCaseTestFixtures.EMAIL, OtpPurpose.LOGIN);
        verifyNoInteractions(accountSessionTokenService);
    }

    @Test
    @DisplayName("Trusted device path → access + refresh tokens")
    void executeWhenTrustedPathReturnsAuthenticatedTokens() {
        var account = IdentityUseCaseTestFixtures.activeAccount(1L);
        var device = IdentityUseCaseTestFixtures.deviceRow(50L, 1L, IdentityUseCaseTestFixtures.FINGERPRINT, true);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("password", "stored-hash")).thenReturn(true);
        when(accountDeviceRepository.findByAccountIdAndFingerprint(1L, IdentityUseCaseTestFixtures.FINGERPRINT))
                .thenReturn(Optional.of(device));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));
        when(accountDeviceRepository.save(any(AccountDevice.class))).thenAnswer(inv -> inv.getArgument(0));
        var tokens = new AuthTokenResponse("a", "r", 900, account.getUuid());
        when(accountSessionTokenService.createSessionAndIssueTokens(eq(account), eq(50L), eq(IdentityUseCaseTestFixtures.IP)))
                .thenReturn(tokens);

        var response = useCase.execute(request(), IdentityUseCaseTestFixtures.IP);

        assertThat(response.requiresVerification()).isFalse();
        assertThat(response.accessToken()).isEqualTo("a");
        assertThat(response.refreshToken()).isEqualTo("r");
        verify(accountLoginHistoryRepository).save(any(AccountLoginHistory.class));
        verify(accountSessionTokenService).createSessionAndIssueTokens(account, 50L, IdentityUseCaseTestFixtures.IP);
        verifyNoInteractions(accountOtpDeliveryService);
    }
}
