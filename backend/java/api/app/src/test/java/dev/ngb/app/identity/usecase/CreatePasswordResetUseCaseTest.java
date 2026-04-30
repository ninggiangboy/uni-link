package dev.ngb.app.identity.usecase;

import dev.ngb.app.identity.application.service.AccountOtpDeliveryService;
import dev.ngb.app.identity.application.usecase.password.forgot_password.CreatePasswordResetUseCase;
import dev.ngb.app.identity.application.usecase.password.forgot_password.dto.CreatePasswordResetRequest;
import dev.ngb.app.identity.usecase.support.IdentityUseCaseTestFixtures;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreatePasswordResetUseCase")
class CreatePasswordResetUseCaseTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountOtpDeliveryService accountOtpDeliveryService;

    @InjectMocks
    private CreatePasswordResetUseCase useCase;

    private final CreatePasswordResetRequest request = new CreatePasswordResetRequest(IdentityUseCaseTestFixtures.EMAIL);

    @Test
    @DisplayName("No account for email → does not send OTP")
    void executeWhenNoAccountDoesNothing() {
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.empty());

        var response = useCase.execute(request);

        verifyNoInteractions(accountOtpDeliveryService);
        assertThat(response.resetId()).isNotBlank();
    }

    @Test
    @DisplayName("Account not active → does not send OTP")
    void executeWhenAccountNotActiveDoesNothing() {
        var pending = IdentityUseCaseTestFixtures.pendingAccount(1L);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(pending));

        var response = useCase.execute(request);

        verifyNoInteractions(accountOtpDeliveryService);
        assertThat(response.resetId()).isNotBlank();
    }

    @Test
    @DisplayName("Active account → sends PASSWORD_RESET OTP")
    void executeWhenActiveSendsPasswordResetOtp() {
        var active = IdentityUseCaseTestFixtures.activeAccount(2L);
        var otp = AccountOtp.create(2L, "654321", OtpPurpose.PASSWORD_RESET, OtpChannel.EMAIL);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(active));
        when(accountOtpDeliveryService.sendEmailOtp(eq(2L), eq(IdentityUseCaseTestFixtures.EMAIL), eq(OtpPurpose.PASSWORD_RESET)))
                .thenReturn(otp);

        var response = useCase.execute(request);

        verify(accountOtpDeliveryService).sendEmailOtp(eq(2L), eq(IdentityUseCaseTestFixtures.EMAIL), eq(OtpPurpose.PASSWORD_RESET));
        assertThat(response.resetId()).isEqualTo(otp.getUuid());
    }
}
