package dev.ngb.infrastructure.jdbc.chat.mapper;

import dev.ngb.domain.chat.model.moderation.MessageReport;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.chat.entity.MessageReportJdbcEntity;

public final class MessageReportJdbcMapper implements JdbcMapper<MessageReport, MessageReportJdbcEntity> {

    public static final MessageReportJdbcMapper INSTANCE = new MessageReportJdbcMapper();

    private MessageReportJdbcMapper() {}

    @Override
    public MessageReport toDomain(MessageReportJdbcEntity entity) {
        return MessageReport.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getMessageId(),
                entity.getReporterProfileId(),
                entity.getReason()
        );
    }

    @Override
    public MessageReportJdbcEntity toJdbc(MessageReport domain) {
        return MessageReportJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .messageId(domain.getMessageId())
                .reporterProfileId(domain.getReporterProfileId())
                .reason(domain.getReason())
                .build();
    }
}

