package dev.ngb.app.identity.application.port;

public interface TokenProvider {

    String generateAccessToken(Long accountId, String accountUuid, String email);

    String generateRefreshToken();

    String generateVerificationToken(Long accountId, Long deviceId);

    VerificationClaims parseVerificationToken(String token);

    String hashToken(String rawToken);

    long getAccessTokenExpiresInSeconds();

    record VerificationClaims(Long accountId, Long deviceId) {}
}
