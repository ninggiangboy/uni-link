package dev.ngb.infrastructure.jdbc.identity.entity;

import dev.ngb.domain.identity.model.otp.OtpChannel;
import dev.ngb.domain.identity.model.otp.OtpPurpose;
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
@Table("iam_account_otps")
public class AccountOtpJdbcEntity extends JdbcEntity<Long> {

    private Long accountId;
    private String code;
    private OtpPurpose purpose;
    private OtpChannel channel;
    private Instant expiresAt;
    private Boolean isUsed;
    private Integer attempts;
}
