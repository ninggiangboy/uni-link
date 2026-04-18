package dev.ngb.app.identity.web;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.usecase.authentication.login_account.LoginAccountUseCase;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.LoginAccountRequest;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.LoginAccountResponse;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.OAuthLoginUseCase;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.OAuthLoginRequest;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.OAuthLoginResponse;
import dev.ngb.app.identity.application.usecase.authentication.verify_login.VerifyLoginUseCase;
import dev.ngb.app.identity.application.usecase.authentication.verify_login.dto.VerifyLoginRequest;
import dev.ngb.app.identity.application.usecase.password.forgot_password.ForgotPasswordUseCase;
import dev.ngb.app.identity.application.usecase.password.forgot_password.dto.ForgotPasswordRequest;
import dev.ngb.app.identity.application.usecase.password.reset_password.ResetPasswordUseCase;
import dev.ngb.app.identity.application.usecase.password.reset_password.dto.ResetPasswordRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.RegisterAccountUseCase;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.RegisterAccountRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.RegisterAccountResponse;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.ResendVerificationUseCase;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.dto.ResendVerificationRequest;
import dev.ngb.app.identity.application.usecase.registration.verify_email.VerifyEmailUseCase;
import dev.ngb.app.identity.application.usecase.registration.verify_email.dto.VerifyEmailRequest;
import dev.ngb.app.identity.application.usecase.session.logout_account.LogoutAccountUseCase;
import dev.ngb.app.identity.application.usecase.session.logout_account.dto.LogoutAccountRequest;
import dev.ngb.app.identity.application.usecase.session.refresh_token.RefreshTokenUseCase;
import dev.ngb.app.identity.application.usecase.session.refresh_token.dto.RefreshTokenRequest;
import dev.ngb.infrastructure.web.RequestUtils;
import dev.ngb.infrastructure.web.ResourceResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthResource implements AuthApi {

    private final RegisterAccountUseCase registerAccountUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;
    private final ResendVerificationUseCase resendVerificationUseCase;
    private final LoginAccountUseCase loginAccountUseCase;
    private final VerifyLoginUseCase verifyLoginUseCase;
    private final OAuthLoginUseCase oAuthLoginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutAccountUseCase logoutAccountUseCase;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;

    @Override
    public ResponseEntity<RegisterAccountResponse> register(RegisterAccountRequest request) {
        RegisterAccountResponse response = registerAccountUseCase.execute(request);
        return ResourceResponse.created(response);
    }

    @Override
    public ResponseEntity<AuthTokenResponse> verifyEmail(VerifyEmailRequest request,
                                                         HttpServletRequest httpRequest) {
        String ipAddress = RequestUtils.extractIpAddress(httpRequest);
        AuthTokenResponse response = verifyEmailUseCase.execute(request, ipAddress);
        return ResourceResponse.ok(response);
    }

    @Override
    public ResponseEntity<Void> resendVerification(ResendVerificationRequest request) {
        resendVerificationUseCase.execute(request);
        return ResourceResponse.noContent();
    }

    @Override
    public ResponseEntity<LoginAccountResponse> login(LoginAccountRequest request,
                                                      HttpServletRequest httpRequest) {
        String ipAddress = RequestUtils.extractIpAddress(httpRequest);
        LoginAccountResponse response = loginAccountUseCase.execute(request, ipAddress);
        return ResourceResponse.ok(response);
    }

    @Override
    public ResponseEntity<AuthTokenResponse> verifyLogin(VerifyLoginRequest request,
                                                         HttpServletRequest httpRequest) {
        String ipAddress = RequestUtils.extractIpAddress(httpRequest);
        AuthTokenResponse response = verifyLoginUseCase.execute(request, ipAddress);
        return ResourceResponse.ok(response);
    }

    @Override
    public ResponseEntity<OAuthLoginResponse> oauthLogin(OAuthLoginRequest request,
                                                         HttpServletRequest httpRequest) {
        String ipAddress = RequestUtils.extractIpAddress(httpRequest);
        OAuthLoginResponse response = oAuthLoginUseCase.execute(request, ipAddress);
        return ResourceResponse.ok(response);
    }

    @Override
    public ResponseEntity<AuthTokenResponse> refreshToken(RefreshTokenRequest request) {
        AuthTokenResponse response = refreshTokenUseCase.execute(request);
        return ResourceResponse.ok(response);
    }

    @Override
    public ResponseEntity<Void> logout(LogoutAccountRequest request) {
        logoutAccountUseCase.execute(request);
        return ResourceResponse.noContent();
    }

    @Override
    public ResponseEntity<Void> forgotPassword(ForgotPasswordRequest request) {
        forgotPasswordUseCase.execute(request);
        return ResourceResponse.noContent();
    }

    @Override
    public ResponseEntity<Void> resetPassword(ResetPasswordRequest request) {
        resetPasswordUseCase.execute(request);
        return ResourceResponse.noContent();
    }
}
