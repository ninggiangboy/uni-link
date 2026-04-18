package dev.ngb.infrastructure.jdbc.chat.entity;

import dev.ngb.infrastructure.jdbc.base.entity.JdbcEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table("chat_actor_inboxes")
public class ActorInboxJdbcEntity extends JdbcEntity<Long> {

    private Long profileId;
    private Long conversationId;
    private Long lastMessageId;
    private Long lastReadMessageId;
    private Long unreadCount;
    private Boolean pinned;
    private Boolean muted;
}
