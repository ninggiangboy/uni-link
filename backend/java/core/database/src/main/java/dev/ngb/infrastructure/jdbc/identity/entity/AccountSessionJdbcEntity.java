package dev.ngb.infrastructure.jdbc.identity.entity;

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
@Table("iam_account_sessions")
public class AccountSessionJdbcEntity extends JdbcEntity<Long> {

    private Long accountId;
    private Long deviceId;
    private String tokenHash;
    private String ipAddress;
    private Instant expiresAt;
    private Boolean isRevoked;
}
