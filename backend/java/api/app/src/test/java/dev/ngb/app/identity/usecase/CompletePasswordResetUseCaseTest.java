package dev.ngb.app.identity.usecase;

import dev.ngb.app.identity.application.port.PasswordEncoder;
import dev.ngb.app.identity.application.usecase.password.reset_password.CompletePasswordResetUseCase;
import dev.ngb.app.identity.application.usecase.password.reset_password.dto.CompletePasswordResetRequest;
import dev.ngb.app.identity.usecase.support.IdentityUseCaseTestFixtures;
import dev.ngb.domain.DomainException;
import dev.ngb.domain.identity.error.AccountError;
import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.domain.identity.model.otp.OtpChannel;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.repository.AccountOtpRepository;
import dev.ngb.domain.identity.repository.AccountRepository;
import dev.ngb.domain.identity.repository.AccountSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompletePasswordResetUseCase")
class CompletePasswordResetUseCaseTest {
    private static final String RESET_ID = "otp-uuid";

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountOtpRepository accountOtpRepository;
    @Mock
    private AccountSessionRepository accountSessionRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CompletePasswordResetUseCase useCase;

    private final CompletePasswordResetRequest request =
            new CompletePasswordResetRequest("123456", "new-secret");

    @Test
    @DisplayName("Account missing → INVALID_OTP")
    void executeWhenAccountMissingThrows() {
        when(accountOtpRepository.findByUuid(RESET_ID)).thenReturn(Optional.empty());

        var ex = assertThrows(DomainException.class, () -> useCase.execute(RESET_ID, request));

        assertThat(ex.getError()).isEqualTo(AccountError.INVALID_OTP);
    }

    @Test
    @DisplayName("No active reset OTP → INVALID_OTP")
    void executeWhenNoOtpThrowsInvalidOtp() {
        var account = IdentityUseCaseTestFixtures.activeAccount(3L);
        var otp = AccountOtp.create(3L, "123456", OtpPurpose.LOGIN, OtpChannel.EMAIL);
        when(accountOtpRepository.findByUuid(RESET_ID)).thenReturn(Optional.of(otp));

        var ex = assertThrows(DomainException.class, () -> useCase.execute(RESET_ID, request));

        assertThat(ex.getError()).isEqualTo(AccountError.INVALID_OTP);
    }

    @Test
    @DisplayName("Concurrent OTP save → ConcurrentModificationException propagated")
    void executeWhenOtpSaveThrowsConcurrentModificationException() {
        var account = IdentityUseCaseTestFixtures.activeAccount(3L);
        var otp = AccountOtp.create(3L, "123456", OtpPurpose.PASSWORD_RESET, OtpChannel.EMAIL);

        when(accountOtpRepository.findByUuid(RESET_ID)).thenReturn(Optional.of(otp));
        when(accountRepository.findById(3L)).thenReturn(Optional.of(account));
        when(accountOtpRepository.save(otp)).thenThrow(new ConcurrentModificationException("version conflict"));

        assertThrows(ConcurrentModificationException.class,
                () -> useCase.execute(RESET_ID, request));
    }

    @Test
    @DisplayName("Valid OTP → new password + revoke sessions")
    void executeWhenOtpValidUpdatesPasswordAndRevokesSessions() {
        var account = IdentityUseCaseTestFixtures.activeAccount(3L);
        var otp = AccountOtp.create(3L, "123456", OtpPurpose.PASSWORD_RESET, OtpChannel.EMAIL);
        var s1 = IdentityUseCaseTestFixtures.validSession(1L, 3L, 10L, "h1");
        var s2 = IdentityUseCaseTestFixtures.validSession(2L, 3L, 11L, "h2");

        when(accountOtpRepository.findByUuid(RESET_ID)).thenReturn(Optional.of(otp));
        when(accountRepository.findById(3L)).thenReturn(Optional.of(account));
        when(passwordEncoder.encode("new-secret")).thenReturn("new-hash");
        when(accountSessionRepository.findActiveByAccountId(3L)).thenReturn(List.of(s1, s2));

        useCase.execute(RESET_ID, request);

        assertThat(account.getPasswordHash()).isEqualTo("new-hash");
        verify(accountRepository).save(account);
        verify(accountOtpRepository).save(otp);
        assertThat(s1.isValid()).isFalse();
        assertThat(s2.isValid()).isFalse();
        verify(accountSessionRepository).saveAll(anyList());
    }
}
