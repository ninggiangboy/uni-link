--liquibase formatted sql

--changeset ninggiangboy:012-01-add-att-attachments-processing-request-fields
ALTER TABLE att_attachments
    ADD COLUMN processing_request_id VARCHAR(120),
    ADD COLUMN processing_requested_at TIMESTAMPTZ;

CREATE INDEX idx_att_attachments_processing_requested_at ON att_attachments (processing_requested_at);
--rollback ALTER TABLE att_attachments DROP COLUMN processing_request_id, DROP COLUMN processing_requested_at;
