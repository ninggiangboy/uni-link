package dev.ngb.infrastructure.jdbc.attachment.repository;

import dev.ngb.domain.attachment.model.AttachmentUploadStatus;
import dev.ngb.domain.attachment.model.attachment.Attachment;
import dev.ngb.domain.attachment.repository.AttachmentRepository;
import dev.ngb.infrastructure.jdbc.attachment.entity.AttachmentJdbcEntity;
import dev.ngb.infrastructure.jdbc.attachment.mapper.AttachmentJdbcMapper;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@Repository
public class AttachmentJdbcRepository extends JdbcRepository<Attachment, AttachmentJdbcEntity, Long>
        implements AttachmentRepository {

    public AttachmentJdbcRepository(
            JdbcClient jdbcClient,
            JdbcTemplate jdbcTemplate,
            JdbcAggregateTemplate jdbcAggregate
    ) {
        super(AttachmentJdbcEntity.class, jdbcClient, jdbcTemplate, jdbcAggregate, AttachmentJdbcMapper.INSTANCE);
    }

    @Override
    public Optional<Attachment> findByUuidAndAccountId(String uuid, Long accountId) {
        return findFirst(Criteria.where("uuid").is(uuid).and("accountId").is(accountId));
    }

    @Override
    public List<Attachment> findByUploadStatusAndCreatedAtBefore(AttachmentUploadStatus status, Instant createdBefore) {
        return findAll(Criteria.where("uploadStatus")
                .is(status.name())
                .and("createdAt").lessThan(createdBefore));
    }

    @Override
    public List<Attachment> findAvailableUnprocessedImages(int limit) {
        return findAllBySql(
                """
                SELECT *
                FROM att_attachments
                WHERE upload_status = :uploadStatus
                  AND processed_at IS NULL
                  AND processing_requested_at IS NULL
                  AND content_type LIKE 'image/%'
                ORDER BY created_at ASC
                LIMIT :limit
                """,
                Map.of(
                        "uploadStatus", AttachmentUploadStatus.AVAILABLE.name(),
                        "limit", limit
                )
        );
    }
}
