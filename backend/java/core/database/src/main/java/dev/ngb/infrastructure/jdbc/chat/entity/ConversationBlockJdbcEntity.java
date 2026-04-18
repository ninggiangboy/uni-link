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
@Table("chat_conversation_blocks")
public class ConversationBlockJdbcEntity extends JdbcEntity<Long> {

    private Long blockerProfileId;
    private Long blockedProfileId;
    private Long conversationId;
}
