package dev.ngb.app.identity.web;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.CreateSessionRequest;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.CreateSessionResponse;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.CreateOAuthSessionRequest;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.CreateOAuthSessionResponse;
import dev.ngb.app.identity.application.usecase.password.forgot_password.dto.CreatePasswordResetResponse;
import dev.ngb.app.identity.application.usecase.authentication.verify_login.dto.CompleteSessionVerificationRequest;
import dev.ngb.app.identity.application.usecase.password.forgot_password.dto.CreatePasswordResetRequest;
import dev.ngb.app.identity.application.usecase.password.reset_password.dto.CompletePasswordResetRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountResponse;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.dto.CreateEmailVerificationResponse;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.dto.CreateEmailVerificationRequest;
import dev.ngb.app.identity.application.usecase.registration.verify_email.dto.CompleteEmailVerificationRequest;
import dev.ngb.app.identity.application.usecase.session.logout_account.dto.DeleteCurrentSessionRequest;
import dev.ngb.app.identity.application.usecase.session.refresh_token.dto.CreateTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Authentication", description = "Account registration, login, verification, and password management")
@RequestMapping("/identity")
public interface IdentityAuthEndpoint {

    @Operation(summary = "Create account", description = "Creates an account resource with email and password, then issues an email verification challenge.")
    @PostMapping("/accounts")
    ResponseEntity<CreateAccountResponse> createAccount(@RequestBody CreateAccountRequest request);

    @Operation(summary = "Complete email verification", description = "Completes the email verification challenge identified by verification ID using an OTP code, then returns auth tokens.")
    @PatchMapping("/email-verifications/{verificationId}")
    ResponseEntity<AuthTokenResponse> completeEmailVerification(@PathVariable @NotBlank String verificationId,
                                                  @RequestBody CompleteEmailVerificationRequest request,
                                                  HttpServletRequest httpRequest);

    @Operation(summary = "Create email verification", description = "Creates a new email verification challenge for the given email and returns its verification ID.")
    @PostMapping("/email-verifications")
    ResponseEntity<CreateEmailVerificationResponse> createEmailVerification(@RequestBody CreateEmailVerificationRequest request);

    @Operation(summary = "Create session",
            description = "Creates an authenticated session using email/password. Returns tokens immediately or a verification token when step-up verification is required.")
    @PostMapping("/sessions")
    ResponseEntity<CreateSessionResponse> createSession(@RequestBody CreateSessionRequest request,
                                               HttpServletRequest httpRequest);

    @Operation(summary = "Complete session verification", description = "Completes session verification using the verification token and OTP code from the create-session step.")
    @PostMapping("/sessions/verification")
    ResponseEntity<AuthTokenResponse> completeSessionVerification(@RequestBody CompleteSessionVerificationRequest request,
                                                  HttpServletRequest httpRequest);

    @Operation(summary = "Create OAuth session", description = "Creates an authenticated session using a third-party OAuth provider token. Creates an account if one does not already exist.")
    @PostMapping("/sessions/oauth")
    ResponseEntity<CreateOAuthSessionResponse> createOAuthSession(@RequestBody CreateOAuthSessionRequest request,
                                                  HttpServletRequest httpRequest);

    @Operation(summary = "Create token", description = "Creates a new token pair using a valid refresh token.")
    @PostMapping("/tokens")
    ResponseEntity<AuthTokenResponse> createToken(@RequestBody CreateTokenRequest request);

    @Operation(summary = "Delete current session", description = "Deletes the current session. If a refresh token is provided, only that specific session is revoked.")
    @DeleteMapping("/sessions/current")
    ResponseEntity<Void> deleteCurrentSession(@RequestBody(required = false) DeleteCurrentSessionRequest request);

    @Operation(summary = "Create password reset", description = "Creates a password reset challenge by sending an OTP to the specified email and returns reset ID.")
    @PostMapping("/password-resets")
    ResponseEntity<CreatePasswordResetResponse> createPasswordReset(@RequestBody CreatePasswordResetRequest request);

    @Operation(summary = "Complete password reset", description = "Completes password reset using reset ID, OTP code, and a new password.")
    @PatchMapping("/password-resets/{resetId}")
    ResponseEntity<Void> completePasswordReset(@PathVariable @NotBlank String resetId,
                                       @RequestBody CompletePasswordResetRequest request);
}
