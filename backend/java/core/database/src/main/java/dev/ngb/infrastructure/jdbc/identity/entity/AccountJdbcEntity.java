package dev.ngb.infrastructure.jdbc.identity.entity;

import dev.ngb.domain.identity.model.auth.AccountStatus;
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
@Table("iam_accounts")
public class AccountJdbcEntity extends SoftDeletableJdbcEntity<Long> {

    private String email;
    private String phoneNumber;
    private String passwordHash;
    private AccountStatus status;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Boolean twoFactorEnabled;
    private Instant lastLoginAt;
    private String lastLoginIp;
}
