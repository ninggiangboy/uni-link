package dev.ngb.app.identity.support;

import dev.ngb.app.identity.application.port.OtpSender;
import dev.ngb.domain.identity.model.otp.OtpPurpose;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Captures the last OTP sent so integration tests can read the code (e.g. for verify-email, reset-password).
 */
public class TestOtpSender implements OtpSender {

    private final AtomicReference<SentOtp> lastSent = new AtomicReference<>();

    @Override
    public void send(String email, String code, OtpPurpose purpose) {
        lastSent.set(new SentOtp(email, code, purpose));
    }

    public Optional<SentOtp> getLastSent() {
        return Optional.ofNullable(lastSent.get());
    }

    public Optional<String> getLastOtpCode() {
        return getLastSent().map(SentOtp::code);
    }

    public void clear() {
        lastSent.set(null);
    }

    public record SentOtp(String email, String code, OtpPurpose purpose) {}
}
