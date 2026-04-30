package dev.ngb.app.identity.application.usecase.registration.register_account.dto;

public record CreateAccountResponse(
        String accountUuid,
        String verificationId
) {}
