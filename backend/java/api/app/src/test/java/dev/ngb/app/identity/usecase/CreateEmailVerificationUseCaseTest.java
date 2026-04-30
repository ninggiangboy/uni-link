package dev.ngb.app.identity.usecase;

import dev.ngb.app.identity.application.service.AccountOtpDeliveryService;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.CreateEmailVerificationUseCase;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.dto.CreateEmailVerificationRequest;
import dev.ngb.app.identity.usecase.support.IdentityUseCaseTestFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.domain.identity.model.otp.OtpChannel;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateEmailVerificationUseCase")
class CreateEmailVerificationUseCaseTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountOtpDeliveryService accountOtpDeliveryService;

    @InjectMocks
    private CreateEmailVerificationUseCase useCase;

    private final CreateEmailVerificationRequest request = new CreateEmailVerificationRequest(IdentityUseCaseTestFixtures.EMAIL);

    @Test
    @DisplayName("Account missing → ACCOUNT_NOT_FOUND")
    void executeWhenAccountMissingThrowsNotFound() {
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.empty());

        var ex = assertThrows(DomainException.class, () -> useCase.execute(request));

        assertThat(ex.getError()).isEqualTo(AccountError.ACCOUNT_NOT_FOUND);
        verifyNoInteractions(accountOtpDeliveryService);
    }

    @Test
    @DisplayName("Already verified → EMAIL_ALREADY_VERIFIED")
    void executeWhenAlreadyVerifiedThrowsConflict() {
        var active = IdentityUseCaseTestFixtures.activeAccount(1L);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(active));

        var ex = assertThrows(DomainException.class, () -> useCase.execute(request));

        assertThat(ex.getError()).isEqualTo(AccountError.EMAIL_ALREADY_VERIFIED);
        verifyNoInteractions(accountOtpDeliveryService);
    }

    @Test
    @DisplayName("Pending → sends REGISTRATION OTP")
    void executeWhenPendingSendsRegistrationOtp() {
        var pending = IdentityUseCaseTestFixtures.pendingAccount(5L);
        var otp = AccountOtp.create(5L, "123456", OtpPurpose.REGISTRATION, OtpChannel.EMAIL);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(pending));
        when(accountOtpDeliveryService.sendEmailOtp(eq(5L), eq(IdentityUseCaseTestFixtures.EMAIL), eq(OtpPurpose.REGISTRATION)))
                .thenReturn(otp);

        var response = useCase.execute(request);

        verify(accountOtpDeliveryService).sendEmailOtp(eq(5L), eq(IdentityUseCaseTestFixtures.EMAIL), eq(OtpPurpose.REGISTRATION));
        assertThat(response.verificationId()).isEqualTo(otp.getUuid());
    }
}
