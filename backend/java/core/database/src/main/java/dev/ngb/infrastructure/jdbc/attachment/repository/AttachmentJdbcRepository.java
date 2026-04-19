package dev.ngb.infrastructure.jdbc.attachment.repository;

import dev.ngb.domain.attachment.model.AttachmentUploadStatus;
import dev.ngb.domain.attachment.model.attachment.Attachment;
import dev.ngb.domain.attachment.repository.AttachmentRepository;
import dev.ngb.infrastructure.jdbc.attachment.entity.AttachmentJdbcEntity;
import dev.ngb.infrastructure.jdbc.attachment.mapper.AttachmentJdbcMapper;
import dev.ngb.infrastructure.jdbc.base.repository.JdbcRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
        return findFirst(Criteria.where("uuid").is(uuid).and("account_id").is(accountId));
    }

    @Override
    public List<Attachment> findByUploadStatusAndCreatedAtBefore(AttachmentUploadStatus status, Instant createdBefore) {
        return findAll(Criteria.where("upload_status")
                .is(status.name())
                .and("created_at").lessThan(createdBefore));
    }

    @Override
    public List<Attachment> findByUploadStatusAndCreatedAtBefore(
            AttachmentUploadStatus status,
            Instant createdBefore,
            int limit
    ) {
        Criteria criteria = Criteria.where("upload_status")
                .is(status.name())
                .and("created_at").lessThan(createdBefore);
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "created_at"));
        return findAll(criteria, pageable);
    }

    @Override
    public List<Attachment> findAvailableUnprocessedImages(int limit) {
        Criteria criteria = Criteria.where("upload_status").is(AttachmentUploadStatus.AVAILABLE.name())
                .and("processed_at").isNull()
                .and("processing_requested_at").isNull()
                .and("content_type").like("image/%");
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "created_at"));
        return findAll(criteria, pageable);
    }
}
