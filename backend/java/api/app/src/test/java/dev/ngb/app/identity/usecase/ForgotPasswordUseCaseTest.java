package dev.ngb.app.identity.usecase;

import dev.ngb.app.identity.application.service.AccountOtpDeliveryService;
import dev.ngb.app.identity.application.usecase.password.forgot_password.ForgotPasswordUseCase;
import dev.ngb.app.identity.application.usecase.password.forgot_password.dto.ForgotPasswordRequest;
import dev.ngb.app.identity.usecase.support.IdentityUseCaseTestFixtures;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ForgotPasswordUseCase")
class ForgotPasswordUseCaseTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountOtpDeliveryService accountOtpDeliveryService;

    @InjectMocks
    private ForgotPasswordUseCase useCase;

    private final ForgotPasswordRequest request = new ForgotPasswordRequest(IdentityUseCaseTestFixtures.EMAIL);

    @Test
    @DisplayName("No account for email → does not send OTP")
    void executeWhenNoAccountDoesNothing() {
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.empty());

        useCase.execute(request);

        verifyNoInteractions(accountOtpDeliveryService);
    }

    @Test
    @DisplayName("Account not active → does not send OTP")
    void executeWhenAccountNotActiveDoesNothing() {
        var pending = IdentityUseCaseTestFixtures.pendingAccount(1L);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(pending));

        useCase.execute(request);

        verifyNoInteractions(accountOtpDeliveryService);
    }

    @Test
    @DisplayName("Active account → sends PASSWORD_RESET OTP")
    void executeWhenActiveSendsPasswordResetOtp() {
        var active = IdentityUseCaseTestFixtures.activeAccount(2L);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(active));

        useCase.execute(request);

        verify(accountOtpDeliveryService).sendEmailOtp(eq(2L), eq(IdentityUseCaseTestFixtures.EMAIL), eq(OtpPurpose.PASSWORD_RESET));
    }
}
