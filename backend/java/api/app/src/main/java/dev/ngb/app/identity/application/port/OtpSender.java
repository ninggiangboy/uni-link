package dev.ngb.app.identity.application.port;

import dev.ngb.domain.identity.model.otp.OtpPurpose;

public interface OtpSender {

    void send(String email, String code, OtpPurpose purpose);
}
