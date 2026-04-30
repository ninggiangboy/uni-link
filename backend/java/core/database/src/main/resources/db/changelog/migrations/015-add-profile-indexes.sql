--liquibase formatted sql

--changeset ninggiangboy:015-01-add-prf-profiles-indexes
CREATE UNIQUE INDEX uq_prf_profiles_username ON prf_profiles (username);
CREATE INDEX idx_prf_profiles_account_id ON prf_profiles (account_id);
--rollback DROP INDEX IF EXISTS idx_prf_profiles_account_id;
--rollback DROP INDEX IF EXISTS uq_prf_profiles_username;
