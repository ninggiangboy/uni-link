package dev.ngb.app.identity.usecase;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.service.AccountSessionTokenService;
import dev.ngb.app.identity.application.usecase.registration.verify_email.CompleteEmailVerificationUseCase;
import dev.ngb.app.identity.application.usecase.registration.verify_email.dto.CompleteEmailVerificationRequest;
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
@DisplayName("CompleteEmailVerificationUseCase")
class CompleteEmailVerificationUseCaseTest {
    private static final String VERIFICATION_ID = "otp-uuid";

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountDeviceRepository accountDeviceRepository;
    @Mock
    private AccountOtpRepository accountOtpRepository;
    @Mock
    private AccountSessionTokenService accountSessionTokenService;

    @InjectMocks
    private CompleteEmailVerificationUseCase useCase;

    @Test
    @DisplayName("Account not found → ACCOUNT_NOT_FOUND")
    void executeWhenAccountNotFoundThrows() {
        var req = new CompleteEmailVerificationRequest("123456", IdentityUseCaseTestFixtures.device());
        when(accountOtpRepository.findByUuid(VERIFICATION_ID)).thenReturn(Optional.empty());

        var ex = assertThrows(DomainException.class, () -> useCase.execute(VERIFICATION_ID, req, IdentityUseCaseTestFixtures.IP));

        assertThat(ex.getError()).isEqualTo(AccountError.INVALID_OTP);
    }

    @Test
    @DisplayName("Not pending (already verified) → EMAIL_ALREADY_VERIFIED")
    void executeWhenNotPendingThrowsEmailAlreadyVerified() {
        var req = new CompleteEmailVerificationRequest("123456", IdentityUseCaseTestFixtures.device());
        var account = IdentityUseCaseTestFixtures.activeAccount(1L);
        var otp = AccountOtp.create(1L, "123456", OtpPurpose.REGISTRATION, OtpChannel.EMAIL);
        when(accountOtpRepository.findByUuid(VERIFICATION_ID)).thenReturn(Optional.of(otp));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        var ex = assertThrows(DomainException.class, () -> useCase.execute(VERIFICATION_ID, req, IdentityUseCaseTestFixtures.IP));

        assertThat(ex.getError()).isEqualTo(AccountError.EMAIL_ALREADY_VERIFIED);
    }

    @Test
    @DisplayName("No active registration OTP → INVALID_OTP")
    void executeWhenNoActiveOtpThrowsInvalidOtp() {
        var req = new CompleteEmailVerificationRequest("123456", IdentityUseCaseTestFixtures.device());
        var pending = IdentityUseCaseTestFixtures.pendingAccount(10L);
        var otp = AccountOtp.create(10L, "654321", OtpPurpose.REGISTRATION, OtpChannel.EMAIL);
        when(accountOtpRepository.findByUuid(VERIFICATION_ID)).thenReturn(Optional.of(otp));
        when(accountRepository.findById(10L)).thenReturn(Optional.of(pending));

        var ex = assertThrows(DomainException.class, () -> useCase.execute(VERIFICATION_ID, req, IdentityUseCaseTestFixtures.IP));

        assertThat(ex.getError()).isEqualTo(AccountError.INVALID_OTP);
    }

    @Test
    @DisplayName("Valid OTP → open session + tokens")
    void executeWhenOtpValidOpensSessionAndReturnsTokens() {
        var req = new CompleteEmailVerificationRequest("123456", IdentityUseCaseTestFixtures.device());
        var pending = IdentityUseCaseTestFixtures.pendingAccount(10L);
        var otp = AccountOtp.create(10L, "123456", OtpPurpose.REGISTRATION, OtpChannel.EMAIL);

        when(accountOtpRepository.findByUuid(VERIFICATION_ID)).thenReturn(Optional.of(otp));
        when(accountRepository.findById(10L)).thenReturn(Optional.of(pending));
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
        when(accountSessionTokenService.createSessionAndIssueTokens(any(), eq(77L), eq(IdentityUseCaseTestFixtures.IP)))
                .thenReturn(tokenResponse);

        var result = useCase.execute(VERIFICATION_ID, req, IdentityUseCaseTestFixtures.IP);

        assertThat(result).isSameAs(tokenResponse);
        verify(accountRepository).save(pending);
        verify(accountOtpRepository).save(otp);
        verify(accountSessionTokenService).createSessionAndIssueTokens(pending, 77L, IdentityUseCaseTestFixtures.IP);
    }
}
