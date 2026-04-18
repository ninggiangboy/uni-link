package dev.ngb.infrastructure.jdbc.identity.entity;

import dev.ngb.domain.identity.model.auth.DeviceType;
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
@Table("iam_account_devices")
public class AccountDeviceJdbcEntity extends SoftDeletableJdbcEntity<Long> {

    private Long accountId;
    private DeviceType deviceType;
    private String deviceName;
    private String fingerprint;
    private String userAgent;
    private String pushToken;
    private Instant lastActiveAt;
    private Boolean isTrusted;
}
