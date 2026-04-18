package dev.ngb.infrastructure.jdbc.identity.entity;

import dev.ngb.domain.identity.model.auth.AuthProvider;
import dev.ngb.infrastructure.jdbc.base.entity.SoftDeletableJdbcEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table("iam_account_credentials")
public class AccountCredentialJdbcEntity extends SoftDeletableJdbcEntity<Long> {

    private Long accountId;
    private AuthProvider provider;
    private String providerAccountId;
    private String accessToken;
    private String refreshToken;
}
