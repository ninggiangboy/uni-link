--liquibase formatted sql

--changeset ninggiangboy:014-01-create-msg-event-publications
CREATE TABLE msg_event_publications
(
    id          UUID PRIMARY KEY,
    type        VARCHAR(160) NOT NULL,
    type_clazz  VARCHAR(255) NOT NULL,
    payload     TEXT         NOT NULL,
    occurred_at TIMESTAMPTZ  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_msg_event_publications_type ON msg_event_publications (type);
CREATE INDEX idx_msg_event_publications_created_at ON msg_event_publications (created_at);

--rollback DROP TABLE IF EXISTS msg_event_publications;
