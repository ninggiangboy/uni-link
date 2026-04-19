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
    public List<Attachment> findPendingPutStaleAfterId(Instant createdBefore, long idAfter, int limit) {
        Criteria criteria = Criteria.where("upload_status")
                .is(AttachmentUploadStatus.PENDING_PUT.name())
                .and("created_at").lessThan(createdBefore)
                .and("id").greaterThan(idAfter);
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "id"));
        return findAll(criteria, pageable);
    }
}
