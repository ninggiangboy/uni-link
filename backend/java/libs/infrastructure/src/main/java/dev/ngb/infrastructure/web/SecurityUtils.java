package dev.ngb.infrastructure.web;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@UtilityClass
public final class SecurityUtils {

    private static final String ACCOUNT_ID_CLAIM = "account_id";

    public static Long getCurrentAccountId() {
        Jwt jwt = getCurrentJwt();
        return jwt.getClaim(ACCOUNT_ID_CLAIM);
    }

    public static Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found in security context");
        }
        return (Jwt) authentication.getPrincipal();
    }
}
