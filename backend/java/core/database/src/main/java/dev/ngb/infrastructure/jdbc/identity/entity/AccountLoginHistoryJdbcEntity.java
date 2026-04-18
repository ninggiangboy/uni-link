package dev.ngb.infrastructure.jdbc.identity.entity;

import dev.ngb.domain.identity.model.session.LoginResult;
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
@Table("iam_account_login_histories")
public class AccountLoginHistoryJdbcEntity extends JdbcEntity<Long> {

    private Long accountId;
    private Long deviceId;
    private String ipAddress;
    private String userAgent;
    private LoginResult result;
    private String failureReason;
}
