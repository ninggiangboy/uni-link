--liquibase formatted sql

--changeset ninggiangboy:017-01-drop-oauth-provider-token-columns
ALTER TABLE iam_account_credentials
    DROP COLUMN access_token,
    DROP COLUMN refresh_token;

--rollback ALTER TABLE iam_account_credentials ADD COLUMN access_token TEXT; ALTER TABLE iam_account_credentials ADD COLUMN refresh_token TEXT;
