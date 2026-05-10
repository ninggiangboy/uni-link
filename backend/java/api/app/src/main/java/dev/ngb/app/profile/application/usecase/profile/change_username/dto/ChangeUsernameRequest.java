package dev.ngb.app.profile.application.usecase.profile.change_username.dto;

import dev.ngb.util.validation.FluentValidator;

import java.util.regex.Pattern;

public record ChangeUsernameRequest(String username) {
    private static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_.]+$");

    public ChangeUsernameRequest {
        String normalized = username == null ? null : username.trim();
        username = normalized;
        FluentValidator.of(this)
                .ruleFor("username", ignored -> normalized)
                .notNullOrBlank()
                .minLength(3)
                .maxLength(50)
                .matches(VALID_USERNAME_PATTERN)
                .validateAndThrow();
    }
}
