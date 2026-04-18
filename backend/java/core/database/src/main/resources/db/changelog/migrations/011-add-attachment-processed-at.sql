--liquibase formatted sql

--changeset ninggiangboy:011-01-add-att-attachments-processed-at
ALTER TABLE att_attachments
    ADD COLUMN processed_at TIMESTAMPTZ;

CREATE INDEX idx_att_attachments_processed_at ON att_attachments (processed_at);
CREATE INDEX idx_att_attachments_upload_status_content_type ON att_attachments (upload_status, content_type);
--rollback ALTER TABLE att_attachments DROP COLUMN processed_at;
