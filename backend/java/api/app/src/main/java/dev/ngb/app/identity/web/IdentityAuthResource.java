package dev.ngb.app.identity.web;

import dev.ngb.app.identity.application.dto.AuthTokenResponse;
import dev.ngb.app.identity.application.usecase.authentication.login_account.CreateSessionUseCase;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.CreateSessionRequest;
import dev.ngb.app.identity.application.usecase.authentication.login_account.dto.CreateSessionResponse;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.CreateOAuthSessionUseCase;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.CreateOAuthSessionRequest;
import dev.ngb.app.identity.application.usecase.authentication.oauth_login.dto.CreateOAuthSessionResponse;
import dev.ngb.app.identity.application.usecase.authentication.verify_login.CompleteSessionVerificationUseCase;
import dev.ngb.app.identity.application.usecase.authentication.verify_login.dto.CompleteSessionVerificationRequest;
import dev.ngb.app.identity.application.usecase.password.forgot_password.CreatePasswordResetUseCase;
import dev.ngb.app.identity.application.usecase.password.forgot_password.dto.CreatePasswordResetRequest;
import dev.ngb.app.identity.application.usecase.password.forgot_password.dto.CreatePasswordResetResponse;
import dev.ngb.app.identity.application.usecase.password.reset_password.CompletePasswordResetUseCase;
import dev.ngb.app.identity.application.usecase.password.reset_password.dto.CompletePasswordResetRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.CreateAccountUseCase;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountRequest;
import dev.ngb.app.identity.application.usecase.registration.register_account.dto.CreateAccountResponse;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.CreateEmailVerificationUseCase;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.dto.CreateEmailVerificationRequest;
import dev.ngb.app.identity.application.usecase.registration.resend_verification.dto.CreateEmailVerificationResponse;
import dev.ngb.app.identity.application.usecase.registration.verify_email.CompleteEmailVerificationUseCase;
import dev.ngb.app.identity.application.usecase.registration.verify_email.dto.CompleteEmailVerificationRequest;
import dev.ngb.app.identity.application.usecase.session.logout_account.DeleteCurrentSessionUseCase;
import dev.ngb.app.identity.application.usecase.session.logout_account.dto.DeleteCurrentSessionRequest;
import dev.ngb.app.identity.application.usecase.session.refresh_token.CreateTokenUseCase;
import dev.ngb.app.identity.application.usecase.session.refresh_token.dto.CreateTokenRequest;
import dev.ngb.infrastructure.web.RequestUtils;
import dev.ngb.infrastructure.web.ResourceResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IdentityAuthResource implements IdentityAuthEndpoint {

    private final CreateAccountUseCase createAccountUseCase;
    private final CompleteEmailVerificationUseCase completeEmailVerificationUseCase;
    private final CreateEmailVerificationUseCase createEmailVerificationUseCase;
    private final CreateSessionUseCase createSessionUseCase;
    private final CompleteSessionVerificationUseCase completeSessionVerificationUseCase;
    private final CreateOAuthSessionUseCase createOAuthSessionUseCase;
    private final CreateTokenUseCase createTokenUseCase;
    private final DeleteCurrentSessionUseCase deleteCurrentSessionUseCase;
    private final CreatePasswordResetUseCase createPasswordResetUseCase;
    private final CompletePasswordResetUseCase completePasswordResetUseCase;

    @Override
    public ResponseEntity<CreateAccountResponse> createAccount(CreateAccountRequest request) {
        CreateAccountResponse response = createAccountUseCase.execute(request);
        return ResourceResponse.created(response);
    }

    @Override
    public ResponseEntity<AuthTokenResponse> completeEmailVerification(String verificationId,
                                                         CompleteEmailVerificationRequest request,
                                                         HttpServletRequest httpRequest) {
        String ipAddress = RequestUtils.extractIpAddress(httpRequest);
        AuthTokenResponse response = completeEmailVerificationUseCase.execute(verificationId, request, ipAddress);
        return ResourceResponse.ok(response);
    }

    @Override
    public ResponseEntity<CreateEmailVerificationResponse> createEmailVerification(CreateEmailVerificationRequest request) {
        CreateEmailVerificationResponse response = createEmailVerificationUseCase.execute(request);
        return ResourceResponse.created(response);
    }

    @Override
    public ResponseEntity<CreateSessionResponse> createSession(CreateSessionRequest request,
                                                      HttpServletRequest httpRequest) {
        String ipAddress = RequestUtils.extractIpAddress(httpRequest);
        CreateSessionResponse response = createSessionUseCase.execute(request, ipAddress);
        return ResourceResponse.ok(response);
    }

    @Override
    public ResponseEntity<AuthTokenResponse> completeSessionVerification(CompleteSessionVerificationRequest request,
                                                         HttpServletRequest httpRequest) {
        String ipAddress = RequestUtils.extractIpAddress(httpRequest);
        AuthTokenResponse response = completeSessionVerificationUseCase.execute(request, ipAddress);
        return ResourceResponse.ok(response);
    }

    @Override
    public ResponseEntity<CreateOAuthSessionResponse> createOAuthSession(CreateOAuthSessionRequest request,
                                                         HttpServletRequest httpRequest) {
        String ipAddress = RequestUtils.extractIpAddress(httpRequest);
        CreateOAuthSessionResponse response = createOAuthSessionUseCase.execute(request, ipAddress);
        return ResourceResponse.ok(response);
    }

    @Override
    public ResponseEntity<AuthTokenResponse> createToken(CreateTokenRequest request) {
        AuthTokenResponse response = createTokenUseCase.execute(request);
        return ResourceResponse.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteCurrentSession(DeleteCurrentSessionRequest request) {
        deleteCurrentSessionUseCase.execute(request);
        return ResourceResponse.noContent();
    }

    @Override
    public ResponseEntity<CreatePasswordResetResponse> createPasswordReset(CreatePasswordResetRequest request) {
        CreatePasswordResetResponse response = createPasswordResetUseCase.execute(request);
        return ResourceResponse.created(response);
    }

    @Override
    public ResponseEntity<Void> completePasswordReset(String resetId, CompletePasswordResetRequest request) {
        completePasswordResetUseCase.execute(resetId, request);
        return ResourceResponse.noContent();
    }
}
