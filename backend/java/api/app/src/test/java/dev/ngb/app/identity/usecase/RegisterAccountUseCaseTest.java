package dev.ngb.app.identity.usecase;

import dev.ngb.app.identity.application.port.PasswordEncoder;
import dev.ngb.app.identity.application.service.AccountOtpDeliveryService;
import dev.ngb.app.identity.application.usecase.registration.register_account.RegisterAccountUseCase;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.RegisterAccountRequest;
import dev.ngb.app.identity.usecase.support.IdentityUseCaseTestFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.auth.Account;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterAccountUseCase")
class RegisterAccountUseCaseTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AccountOtpDeliveryService accountOtpDeliveryService;

    @InjectMocks
    private RegisterAccountUseCase useCase;

    private RegisterAccountRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterAccountRequest(IdentityUseCaseTestFixtures.EMAIL, "plain-secret");
    }

    @Test
    @DisplayName("Email already exists (pre-check) → EMAIL_ALREADY_EXISTS")
    void executeWhenEmailAlreadyExistsThrowsConflict() {
        when(accountRepository.existsByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(true);

        var ex = assertThrows(DomainException.class, () -> useCase.execute(request));

        assertThat(ex.getError()).isEqualTo(AccountError.EMAIL_ALREADY_EXISTS);
        verifyNoInteractions(passwordEncoder, accountOtpDeliveryService);
    }

    @Test
    @DisplayName("Duplicate email on save race → EMAIL_ALREADY_EXISTS")
    void executeWhenDuplicateOnSaveThrowsConflict() {
        when(accountRepository.existsByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(false);
        when(passwordEncoder.encode("plain-secret")).thenReturn("hashed-secret");
        when(accountRepository.save(any(Account.class))).thenThrow(AccountError.EMAIL_ALREADY_EXISTS.exception());

        var ex = assertThrows(DomainException.class, () -> useCase.execute(request));

        assertThat(ex.getError()).isEqualTo(AccountError.EMAIL_ALREADY_EXISTS);
        verifyNoInteractions(accountOtpDeliveryService);
    }

    @Test
    @DisplayName("New email → hash password, save account, REGISTRATION OTP")
    void executeWhenNewEmailHashesPasswordSavesAccountAndSendsRegistrationOtp() {
        when(accountRepository.existsByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(false);
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

        var response = useCase.execute(request);

        assertThat(response.accountUuid()).isNotBlank();
        var saved = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(saved.capture());
        assertThat(saved.getValue().getEmail()).isEqualTo(IdentityUseCaseTestFixtures.EMAIL);
        assertThat(saved.getValue().getPasswordHash()).isEqualTo("hashed-secret");
        verify(accountOtpDeliveryService).sendEmailOtp(eq(99L), eq(IdentityUseCaseTestFixtures.EMAIL), eq(OtpPurpose.REGISTRATION));
    }
}
