package dev.ngb.app.identity.usecase;

import dev.ngb.app.identity.application.port.PasswordEncoder;
import dev.ngb.app.identity.application.service.AccountOtpDeliveryService;
import dev.ngb.app.identity.application.usecase.registration.register_account.CreateAccountUseCase;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountRequest;
import dev.ngb.app.identity.usecase.support.IdentityUseCaseTestFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.domain.identity.model.otp.OtpChannel;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateAccountUseCase")
class CreateAccountUseCaseTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AccountOtpDeliveryService accountOtpDeliveryService;

    @InjectMocks
    private CreateAccountUseCase useCase;

    private CreateAccountRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateAccountRequest(IdentityUseCaseTestFixtures.EMAIL, "plain-secret");
    }

    @Test
    @DisplayName("New email → hash password, save account, REGISTRATION OTP")
    void executeWhenNewEmailHashesPasswordSavesAccountAndSendsRegistrationOtp() {
        when(passwordEncoder.encode("plain-secret")).thenReturn("hashed-secret");
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account a = invocation.getArgument(0);
            return Account.reconstruct(
                    99L,
                    a.getUuid(),
                    null,
                    a.getCreatedAt(),
                    null,
                    null,
                    a.getEmail(),
                    null,
                    a.getPasswordHash(),
                    a.getStatus(),
                    a.getEmailVerified(),
                    a.getPhoneVerified(),
                    a.getTwoFactorEnabled(),
                    a.getLastLoginAt(),
                    a.getLastLoginIp()
            );
        });
        var otp = AccountOtp.create(99L, "123456", OtpPurpose.REGISTRATION, OtpChannel.EMAIL);
        when(accountOtpDeliveryService.sendEmailOtp(eq(99L), eq(IdentityUseCaseTestFixtures.EMAIL), eq(OtpPurpose.REGISTRATION)))
                .thenReturn(otp);

        var response = useCase.execute(request);

        assertThat(response.accountUuid()).isNotBlank();
        assertThat(response.verificationId()).isEqualTo(otp.getUuid());
        var saved = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(saved.capture());
        assertThat(saved.getValue().getEmail()).isEqualTo(IdentityUseCaseTestFixtures.EMAIL);
        assertThat(saved.getValue().getPasswordHash()).isEqualTo("hashed-secret");
        verify(accountOtpDeliveryService).sendEmailOtp(eq(99L), eq(IdentityUseCaseTestFixtures.EMAIL), eq(OtpPurpose.REGISTRATION));
    }

    @Test
    @DisplayName("Duplicate email on save race → DataIntegrityViolation → EMAIL_ALREADY_EXISTS")
    void executeWhenConcurrentEmailSaveThrowsConflict() {
        when(passwordEncoder.encode("plain-secret")).thenReturn("hashed-secret");
        when(accountRepository.save(any(Account.class))).thenThrow(new DataIntegrityViolationException("duplicate email"));

        var ex = assertThrows(DomainException.class, () -> useCase.execute(request));

        assertThat(ex.getError()).isEqualTo(AccountError.EMAIL_ALREADY_EXISTS);
        verifyNoInteractions(accountOtpDeliveryService);
    }
}
