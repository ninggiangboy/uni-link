package dev.ngb.app.identity.web;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.LoginAccountRequest;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.LoginAccountResponse;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.OAuthLoginRequest;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.OAuthLoginResponse;
import dev.ngb.app.identity.application.usecase.authentication.verify_login.dto.VerifyLoginRequest;
import dev.ngb.app.identity.application.usecase.password.forgot_password.dto.ForgotPasswordRequest;
import dev.ngb.app.identity.application.usecase.password.reset_password.dto.ResetPasswordRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.RegisterAccountRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.RegisterAccountResponse;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.dto.ResendVerificationRequest;
import dev.ngb.app.identity.application.usecase.registration.verify_email.dto.VerifyEmailRequest;
import dev.ngb.app.identity.application.usecase.session.logout_account.dto.LogoutAccountRequest;
import dev.ngb.app.identity.application.usecase.session.refresh_token.dto.RefreshTokenRequest;
import dev.ngb.web.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Authentication", description = "Account registration, login, verification, and password management")
@RequestMapping("/api/auth")
public interface AuthApi {

    @Operation(summary = "Register a new account", description = "Creates a new account with email and password. An OTP is sent to the email for verification.")
    @PostMapping("/register")
    ResponseEntity<RegisterAccountResponse> register(@RequestBody RegisterAccountRequest request);

    @Operation(summary = "Verify email with OTP", description = "Verifies the account email using the OTP code. Returns auth tokens on success.")
    @PostMapping("/verify-email")
    ResponseEntity<AuthTokenResponse> verifyEmail(@RequestBody VerifyEmailRequest request,
                                                  HttpServletRequest httpRequest);

    @Operation(summary = "Resend verification email", description = "Resends the OTP verification email to the specified address.")
    @PostMapping("/verify-email/resend")
    ResponseEntity<Void> resendVerification(@RequestBody ResendVerificationRequest request);

    @Operation(summary = "Login with email and password",
            description = "Authenticates using credentials. Returns tokens directly if 2FA is not required, or a verification token if OTP verification is needed.")
    @PostMapping("/login")
    ResponseEntity<LoginAccountResponse> login(@RequestBody LoginAccountRequest request,
                                               HttpServletRequest httpRequest);

    @Operation(summary = "Verify login OTP", description = "Completes two-factor login by verifying the OTP code sent after the initial login request.")
    @PostMapping("/login/verify")
    ResponseEntity<AuthTokenResponse> verifyLogin(@RequestBody VerifyLoginRequest request,
                                                  HttpServletRequest httpRequest);

    @Operation(summary = "Login via OAuth provider", description = "Authenticates using a third-party OAuth provider token (e.g. Google, GitHub). Creates a new account if one does not exist.")
    @PostMapping("/oauth")
    ResponseEntity<OAuthLoginResponse> oauthLogin(@RequestBody OAuthLoginRequest request,
                                                  HttpServletRequest httpRequest);

    @Operation(summary = "Refresh access token", description = "Issues a new access token using a valid refresh token.")
    @PostMapping("/token/refresh")
    ResponseEntity<AuthTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request);

    @Operation(summary = "Logout", description = "Revokes the current session. If a refresh token is provided, only that session is revoked.")
    @PostMapping("/logout")
    ResponseEntity<Void> logout(@RequestBody LogoutAccountRequest request);

    @Operation(summary = "Request password reset", description = "Sends a password reset OTP to the specified email address.")
    @PostMapping("/forgot-password")
    ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest request);

    @Operation(summary = "Reset password", description = "Resets the account password using email, OTP code, and the new password.")
    @PostMapping("/reset-password")
    ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request);
}
