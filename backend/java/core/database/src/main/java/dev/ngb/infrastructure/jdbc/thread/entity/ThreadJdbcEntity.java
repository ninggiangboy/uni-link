package dev.ngb.infrastructure.jdbc.thread.entity;

import dev.ngb.domain.thread.model.thread.ReplyRestriction;
import dev.ngb.domain.thread.model.thread.ThreadStatus;
import dev.ngb.domain.thread.model.thread.ThreadVisibility;
import dev.ngb.infrastructure.jdbc.base.entity.SoftDeletableJdbcEntity;
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
@Table("thr_threads")
public class ThreadJdbcEntity extends SoftDeletableJdbcEntity<Long> {

    private Long authorProfileId;
    private String rawContent;
    private Long parentThreadId;
    private Long rootThreadId;
    private Long quoteThreadId;
    private Long likeCount;
    private Long replyCount;
    private Long repostCount;
    private Long quoteCount;
    private Long bookmarkCount;
    private Long viewCount;
    private ThreadStatus status;
    private ThreadVisibility visibility;
    private ReplyRestriction replyRestriction;
    private Boolean isHideLikeCount;
    private Boolean isEdited;
    private Instant editedAt;
    private Instant scheduledPublishAt;
    private Boolean hasMentions;
    private Boolean hasHashtags;
    private Boolean hasMedias;
    private Boolean hasPolls;
}
