package dev.ngb.app.identity.usecase;

import dev.ngb.app.identity.application.service.AccountOtpDeliveryService;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.ResendVerificationUseCase;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.dto.ResendVerificationRequest;
import dev.ngb.app.identity.usecase.support.IdentityUseCaseTestFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.identity.error.AccountError;
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
@DisplayName("ResendVerificationUseCase")
class ResendVerificationUseCaseTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountOtpDeliveryService accountOtpDeliveryService;

    @InjectMocks
    private ResendVerificationUseCase useCase;

    private final ResendVerificationRequest request = new ResendVerificationRequest(IdentityUseCaseTestFixtures.EMAIL);

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
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(pending));

        useCase.execute(request);

        verify(accountOtpDeliveryService).sendEmailOtp(eq(5L), eq(IdentityUseCaseTestFixtures.EMAIL), eq(OtpPurpose.REGISTRATION));
    }
}
