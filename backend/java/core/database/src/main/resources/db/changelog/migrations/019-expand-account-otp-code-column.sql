--liquibase formatted sql

--changeset ninggiangboy:019-01-expand-account-otp-code-column
ALTER TABLE iam_account_otps
    ALTER COLUMN code TYPE VARCHAR(64);

--rollback ALTER TABLE iam_account_otps ALTER COLUMN code TYPE VARCHAR(20);
