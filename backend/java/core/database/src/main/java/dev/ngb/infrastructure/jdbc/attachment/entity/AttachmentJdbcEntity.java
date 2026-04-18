package dev.ngb.infrastructure.jdbc.attachment.entity;

import dev.ngb.infrastructure.jdbc.base.entity.JdbcEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table("att_attachments")
public class AttachmentJdbcEntity extends JdbcEntity<Long> {

    private Long accountId;
    private String type;
    private String objectKey;
    private String fileName;
    private String contentType;
    private Long sizeBytes;
    private String fileUrl;
    private String uploadStatus;
    private String processingRequestId;
    private Instant processingRequestedAt;
    private Instant processedAt;
}
