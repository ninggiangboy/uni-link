package dev.ngb.app.identity.usecase;

import dev.ngb.app.identity.application.port.PasswordEncoder;
import dev.ngb.app.identity.application.usecase.password.reset_password.ResetPasswordUseCase;
import dev.ngb.app.identity.application.usecase.password.reset_password.dto.ResetPasswordRequest;
import dev.ngb.app.identity.usecase.support.IdentityUseCaseTestFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.domain.identity.model.otp.OtpChannel;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.model.session.AccountSession;
import dev.ngb.domain.identity.repository.AccountOtpRepository;
import dev.ngb.domain.identity.repository.AccountRepository;
import dev.ngb.domain.identity.repository.AccountSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResetPasswordUseCase")
class ResetPasswordUseCaseTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountOtpRepository accountOtpRepository;
    @Mock
    private AccountSessionRepository accountSessionRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ResetPasswordUseCase useCase;

    private final ResetPasswordRequest request =
            new ResetPasswordRequest(IdentityUseCaseTestFixtures.EMAIL, "123456", "new-secret");

    @Test
    @DisplayName("Account missing → INVALID_OTP")
    void executeWhenAccountMissingThrows() {
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(request));

        assertThat(ex.getError()).isEqualTo(AccountError.INVALID_OTP);
    }

    @Test
    @DisplayName("No active reset OTP → INVALID_OTP")
    void executeWhenNoOtpThrowsInvalidOtp() {
        var account = IdentityUseCaseTestFixtures.activeAccount(3L);
        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(account));
        when(accountOtpRepository.findLatestActiveByAccountIdAndPurpose(3L, OtpPurpose.PASSWORD_RESET)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(request));

        assertThat(ex.getError()).isEqualTo(AccountError.INVALID_OTP);
    }

    @Test
    @DisplayName("Valid OTP → new password + revoke sessions")
    void executeWhenOtpValidUpdatesPasswordAndRevokesSessions() {
        var account = IdentityUseCaseTestFixtures.activeAccount(3L);
        AccountOtp otp = AccountOtp.create(3L, "123456", OtpPurpose.PASSWORD_RESET, OtpChannel.EMAIL);
        AccountSession s1 = IdentityUseCaseTestFixtures.validSession(1L, 3L, 10L, "h1");
        AccountSession s2 = IdentityUseCaseTestFixtures.validSession(2L, 3L, 11L, "h2");

        when(accountRepository.findByEmail(IdentityUseCaseTestFixtures.EMAIL)).thenReturn(Optional.of(account));
        when(accountOtpRepository.findLatestActiveByAccountIdAndPurpose(3L, OtpPurpose.PASSWORD_RESET)).thenReturn(Optional.of(otp));
        when(passwordEncoder.encode("new-secret")).thenReturn("new-hash");
        when(accountSessionRepository.findActiveByAccountId(3L)).thenReturn(List.of(s1, s2));

        useCase.execute(request);

        assertThat(account.getPasswordHash()).isEqualTo("new-hash");
        verify(accountRepository).save(account);
        verify(accountOtpRepository).save(otp);
        assertThat(s1.isValid()).isFalse();
        assertThat(s2.isValid()).isFalse();
        verify(accountSessionRepository).saveAll(anyList());
    }
}
