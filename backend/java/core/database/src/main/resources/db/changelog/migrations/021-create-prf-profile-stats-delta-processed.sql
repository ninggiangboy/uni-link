--liquibase formatted sql

--changeset ninggiangboy:021-01-create-prf-profile-stats-delta-processed
CREATE TABLE prf_profile_stats_delta_processed (
    event_uuid   VARCHAR(36) PRIMARY KEY,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
--rollback DROP TABLE prf_profile_stats_delta_processed;
