--liquibase formatted sql

--changeset ninggiangboy:018-01-add-account-provider-unique-constraint
CREATE UNIQUE INDEX uq_iam_account_credentials_account_provider
    ON iam_account_credentials (account_id, provider)
    WHERE deleted_at IS NULL;

--rollback DROP INDEX IF EXISTS uq_iam_account_credentials_account_provider;
