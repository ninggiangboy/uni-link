--liquibase formatted sql

--changeset ninggiangboy:013-01-create-msg-processed-events
CREATE TABLE msg_processed_events
(
    event_id     VARCHAR(120) PRIMARY KEY,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

--rollback DROP TABLE IF EXISTS msg_processed_events;
