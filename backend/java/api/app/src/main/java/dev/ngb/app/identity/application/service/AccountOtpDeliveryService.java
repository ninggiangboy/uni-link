package dev.ngb.app.identity.application.service;

import dev.ngb.app.identity.application.port.OtpCodeGenerator;
import dev.ngb.app.identity.application.port.OtpSender;
import dev.ngb.application.ApplicationService;
import dev.ngb.domain.identity.model.otp.AccountOtp;
import dev.ngb.domain.identity.model.otp.OtpChannel;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
import dev.ngb.domain.identity.repository.AccountOtpRepository;
import dev.ngb.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * Centralizes the email OTP pipeline used by several flows (registration, resend, login step-up,
 * password reset). Keeps use cases from repeating generate → persist → send with the same channel.
 */
@Slf4j
@RequiredArgsConstructor
public class AccountOtpDeliveryService implements ApplicationService {

    private final AccountOtpRepository accountOtpRepository;
    private final OtpCodeGenerator otpCodeGenerator;
    private final OtpSender otpSender;

    public void sendEmailOtp(Long accountId, String email, OtpPurpose purpose) {
        log.debug(
                "Email OTP pipeline start accountId={}, purpose={}, channel={}, email={}",
                accountId,
                purpose,
                OtpChannel.EMAIL,
                StringUtils.maskEmail(email)
        );

        String code = otpCodeGenerator.generate();
        AccountOtp otp = AccountOtp.create(accountId, code, purpose, OtpChannel.EMAIL);
        AccountOtp saved = accountOtpRepository.save(otp);
        log.debug(
                "Email OTP persisted accountId={}, purpose={}, otpRecordId={}",
                accountId,
                purpose,
                saved.getId()
        );

        otpSender.send(email, code, purpose);

        log.info(
                "Email OTP dispatched accountId={}, purpose={}, email={} (code not logged)",
                accountId,
                purpose,
                StringUtils.maskEmail(email)
        );
    }
}
