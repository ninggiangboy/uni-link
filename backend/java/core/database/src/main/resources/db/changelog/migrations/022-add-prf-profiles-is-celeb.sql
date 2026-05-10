--liquibase formatted sql

--changeset ninggiangboy:022-01-add-prf-profiles-is-celeb
ALTER TABLE prf_profiles
    ADD COLUMN is_celeb BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_prf_profiles_is_celeb ON prf_profiles (is_celeb) WHERE is_celeb = TRUE;
--rollback DROP INDEX IF EXISTS idx_prf_profiles_is_celeb;
--rollback ALTER TABLE prf_profiles DROP COLUMN IF EXISTS is_celeb;
