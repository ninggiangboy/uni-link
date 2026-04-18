package dev.ngb.app.identity.infrastructure;

import dev.ngb.app.identity.application.port.OtpCodeGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomOtpCodeGenerator implements OtpCodeGenerator {

    private static final int OTP_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate() {
        int code = random.nextInt((int) Math.pow(10, OTP_LENGTH));
        return String.format("%0" + OTP_LENGTH + "d", code);
    }
}
