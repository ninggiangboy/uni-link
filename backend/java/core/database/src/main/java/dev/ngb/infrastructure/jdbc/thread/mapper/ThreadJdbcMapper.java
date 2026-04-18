package dev.ngb.infrastructure.jdbc.thread.mapper;

import dev.ngb.domain.thread.model.thread.Thread;
import dev.ngb.infrastructure.jdbc.base.mapper.JdbcMapper;
import dev.ngb.infrastructure.jdbc.thread.entity.ThreadJdbcEntity;

public final class ThreadJdbcMapper implements JdbcMapper<Thread, ThreadJdbcEntity> {

    public static final ThreadJdbcMapper INSTANCE = new ThreadJdbcMapper();

    private ThreadJdbcMapper() {}

    @Override
    public Thread toDomain(ThreadJdbcEntity entity) {
        return Thread.reconstruct(
                entity.getId(),
                entity.getUuid(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedBy(),
                entity.getUpdatedAt(),
                entity.getAuthorProfileId(),
                entity.getRawContent(),
                entity.getParentThreadId(),
                entity.getRootThreadId(),
                entity.getQuoteThreadId(),
                entity.getLikeCount(),
                entity.getReplyCount(),
                entity.getRepostCount(),
                entity.getQuoteCount(),
                entity.getBookmarkCount(),
                entity.getViewCount(),
                entity.getStatus(),
                entity.getVisibility(),
                entity.getReplyRestriction(),
                entity.getIsHideLikeCount(),
                entity.getIsEdited(),
                entity.getEditedAt(),
                entity.getScheduledPublishAt(),
                entity.getHasMentions(),
                entity.getHasHashtags(),
                entity.getHasMedias(),
                entity.getHasPolls()
        );
    }

    @Override
    public ThreadJdbcEntity toJdbc(Thread domain) {
        return ThreadJdbcEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedBy(domain.getUpdatedBy())
                .updatedAt(domain.getUpdatedAt())
                .authorProfileId(domain.getAuthorProfileId())
                .rawContent(domain.getRawContent())
                .parentThreadId(domain.getParentThreadId())
                .rootThreadId(domain.getRootThreadId())
                .quoteThreadId(domain.getQuoteThreadId())
                .likeCount(domain.getLikeCount())
                .replyCount(domain.getReplyCount())
                .repostCount(domain.getRepostCount())
                .quoteCount(domain.getQuoteCount())
                .bookmarkCount(domain.getBookmarkCount())
                .viewCount(domain.getViewCount())
                .status(domain.getStatus())
                .visibility(domain.getVisibility())
                .replyRestriction(domain.getReplyRestriction())
                .isHideLikeCount(domain.getIsHideLikeCount())
                .isEdited(domain.getIsEdited())
                .editedAt(domain.getEditedAt())
                .scheduledPublishAt(domain.getScheduledPublishAt())
                .hasMentions(domain.getHasMentions())
                .hasHashtags(domain.getHasHashtags())
                .hasMedias(domain.getHasMedias())
                .hasPolls(domain.getHasPolls())
                .build();
    }
}

