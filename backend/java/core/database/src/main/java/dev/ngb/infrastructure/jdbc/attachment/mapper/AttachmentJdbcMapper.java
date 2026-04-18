package dev.ngb.infrastructure.jdbc.attachment.mapper;

import dev.ngb.domain.attachment.model.AttachmentUploadStatus;
import dev.ngb.domain.attachment.model.attachment.Attachment;
import dev.ngb.domain.attachment.model.attachment.AttachmentType;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.attachment.entity.AttachmentJdbcEntity;

public final class AttachmentJdbcMapper implements JdbcMapper<Attachment, AttachmentJdbcEntity> {

    public static final AttachmentJdbcMapper INSTANCE = new AttachmentJdbcMapper();

    private AttachmentJdbcMapper() {
    }

    @Override
    public Attachment toDomain(AttachmentJdbcEntity entity) {
        return Attachment.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getAccountId(),
                AttachmentType.valueOf(entity.getType()),
                entity.getObjectKey(),
                entity.getFileName(),
                entity.getContentType(),
                entity.getSizeBytes(),
                entity.getFileUrl(),
                AttachmentUploadStatus.valueOf(entity.getUploadStatus()),
                entity.getProcessingRequestId(),
                entity.getProcessingRequestedAt(),
                entity.getProcessedAt()
        );
    }

    @Override
    public AttachmentJdbcEntity toJdbc(Attachment domain) {
        return AttachmentJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .accountId(domain.getAccountId())
                .type(domain.getType().name())
                .objectKey(domain.getObjectKey())
                .fileName(domain.getFileName())
                .contentType(domain.getContentType())
                .sizeBytes(domain.getSizeBytes())
                .fileUrl(domain.getFileUrl())
                .uploadStatus(domain.getUploadStatus().name())
                .processingRequestId(domain.getProcessingRequestId())
                .processingRequestedAt(domain.getProcessingRequestedAt())
                .processedAt(domain.getProcessedAt())
                .build();
    }
}
