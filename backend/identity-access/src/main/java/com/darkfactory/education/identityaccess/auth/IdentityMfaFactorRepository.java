package com.darkfactory.education.identityaccess.auth;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class IdentityMfaFactorRepository {

    private static final RowMapper<IdentityMfaFactorRecord> ROW_MAPPER = (resultSet, rowNum) -> new IdentityMfaFactorRecord(
            resultSet.getObject("factor_id", UUID.class),
            resultSet.getObject("tenant_id", UUID.class),
            resultSet.getObject("user_id", UUID.class),
            IdentityMfaFactorType.valueOf(resultSet.getString("factor_type")),
            IdentityMfaFactorStatus.valueOf(resultSet.getString("status")),
            resultSet.getString("secret_ciphertext"),
            resultSet.getObject("created_at", OffsetDateTime.class),
            resultSet.getObject("updated_at", OffsetDateTime.class),
            resultSet.getObject("activated_at", OffsetDateTime.class)
    );

    private final JdbcTemplate jdbcTemplate;

    public IdentityMfaFactorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<IdentityMfaFactorRecord> findActiveTotpFactor(UUID tenantId, UUID userId) {
        return findByUserAndStatus(tenantId, userId, IdentityMfaFactorStatus.ACTIVE);
    }

    public Optional<IdentityMfaFactorRecord> findPendingTotpFactor(UUID tenantId, UUID userId) {
        return findByUserAndStatus(tenantId, userId, IdentityMfaFactorStatus.PENDING);
    }

    public IdentityMfaFactorRecord savePendingTotpFactor(UUID tenantId, UUID userId, String secretCiphertext) {
        Optional<IdentityMfaFactorRecord> existingPending = findPendingTotpFactor(tenantId, userId);
        if (existingPending.isPresent()) {
            return jdbcTemplate.query(
                    """
                    update identity_mfa_factor
                    set secret_ciphertext = ?,
                        updated_at = current_timestamp
                    where factor_id = ?
                    returning factor_id, tenant_id, user_id, factor_type, status, secret_ciphertext, created_at, updated_at, activated_at
                    """,
                    ROW_MAPPER,
                    secretCiphertext,
                    existingPending.get().factorId()
            ).getFirst();
        }

        return jdbcTemplate.query(
                """
                insert into identity_mfa_factor (
                    factor_id,
                    tenant_id,
                    user_id,
                    factor_type,
                    status,
                    secret_ciphertext,
                    created_at,
                    updated_at
                )
                values (?, ?, ?, ?, ?, ?, current_timestamp, current_timestamp)
                returning factor_id, tenant_id, user_id, factor_type, status, secret_ciphertext, created_at, updated_at, activated_at
                """,
                ROW_MAPPER,
                UUID.randomUUID(),
                tenantId,
                userId,
                IdentityMfaFactorType.TOTP.name(),
                IdentityMfaFactorStatus.PENDING.name(),
                secretCiphertext
        ).getFirst();
    }

    public IdentityMfaFactorRecord activateFactor(UUID factorId) {
        return jdbcTemplate.query(
                """
                update identity_mfa_factor
                set status = ?,
                    activated_at = current_timestamp,
                    updated_at = current_timestamp
                where factor_id = ?
                returning factor_id, tenant_id, user_id, factor_type, status, secret_ciphertext, created_at, updated_at, activated_at
                """,
                ROW_MAPPER,
                IdentityMfaFactorStatus.ACTIVE.name(),
                factorId
        ).getFirst();
    }

    private Optional<IdentityMfaFactorRecord> findByUserAndStatus(UUID tenantId, UUID userId, IdentityMfaFactorStatus status) {
        List<IdentityMfaFactorRecord> records = jdbcTemplate.query(
                """
                select factor_id, tenant_id, user_id, factor_type, status, secret_ciphertext, created_at, updated_at, activated_at
                from identity_mfa_factor
                where tenant_id = ?
                  and user_id = ?
                  and factor_type = ?
                  and status = ?
                order by created_at desc
                """,
                ROW_MAPPER,
                tenantId,
                userId,
                IdentityMfaFactorType.TOTP.name(),
                status.name()
        );
        return records.stream().findFirst();
    }
}

