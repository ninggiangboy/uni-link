package dev.ngb.app.identity.infrastructure;

import dev.ngb.app.identity.application.port.OtpSender;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Development stub that logs OTP codes instead of sending actual emails.
 * Replace with a real email sender in production.
 */
@Slf4j
@Component
public class LoggingOtpSender implements OtpSender {

    @Override
    public void send(String email, String code, OtpPurpose purpose) {
        log.info("OTP [{}] for {} sent to {}", purpose, code, email);
    }
}
