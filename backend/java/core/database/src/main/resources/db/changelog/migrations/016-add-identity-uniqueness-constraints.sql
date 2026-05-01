--liquibase formatted sql

--changeset ninggiangboy:016-01-add-identity-uniqueness-constraints
CREATE UNIQUE INDEX uq_iam_account_credentials_provider_subject
    ON iam_account_credentials (provider, provider_account_id)
    WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX uq_iam_account_sessions_token_hash
    ON iam_account_sessions (token_hash);

--rollback DROP INDEX IF EXISTS uq_iam_account_sessions_token_hash; DROP INDEX IF EXISTS uq_iam_account_credentials_provider_subject;
