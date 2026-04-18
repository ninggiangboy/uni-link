package dev.ngb.infrastructure.jdbc.chat.entity;

import dev.ngb.domain.chat.model.conversation.ConversationRole;
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
@Table("chat_conversation_participants")
public class ConversationParticipantJdbcEntity extends JdbcEntity<Long> {

    private Long conversationId;
    private Long profileId;
    private ConversationRole role;
    private Instant joinedAt;
    private Instant leftAt;
}
