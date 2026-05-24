package com.darkfactory.education.identityaccess.auth;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class IdentityUserRepository {

    private static final RowMapper<IdentityUserRecord> ROW_MAPPER = (resultSet, rowNum) -> new IdentityUserRecord(
            resultSet.getObject("user_id", UUID.class),
            resultSet.getObject("tenant_id", UUID.class),
            resultSet.getString("username"),
            resultSet.getString("password_hash"),
            IdentityUserStatus.valueOf(resultSet.getString("status")),
            resultSet.getString("display_name"),
            IdentityUserAuthority.valueOf(resultSet.getString("authority")),
            resultSet.getObject("created_at", OffsetDateTime.class),
            resultSet.getObject("updated_at", OffsetDateTime.class)
    );

    private final JdbcTemplate jdbcTemplate;

    public IdentityUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<IdentityUserRecord> findByTenantAndUsername(UUID tenantId, String username) {
        List<IdentityUserRecord> records = jdbcTemplate.query(
                """
                select user_id, tenant_id, username, password_hash, status, display_name, authority, created_at, updated_at
                from identity_user
                where tenant_id = ? and username = ?
                """,
                ROW_MAPPER,
                tenantId,
                username
        );
        return records.stream().findFirst();
    }

    public IdentityUserRecord insert(
            UUID tenantId,
            String username,
            String passwordHash,
            IdentityUserStatus status,
            String displayName,
            IdentityUserAuthority authority
    ) {
        return jdbcTemplate.query(
                """
                insert into identity_user (
                    user_id,
                    tenant_id,
                    username,
                    password_hash,
                    status,
                    display_name,
                    authority,
                    created_at,
                    updated_at
                )
                values (?, ?, ?, ?, ?, ?, ?, current_timestamp, current_timestamp)
                returning user_id, tenant_id, username, password_hash, status, display_name, authority, created_at, updated_at
                """,
                ROW_MAPPER,
                UUID.randomUUID(),
                tenantId,
                username,
                passwordHash,
                status.name(),
                displayName,
                authority.name()
        ).getFirst();
    }

    public IdentityUserRecord updateBootstrapAdmin(
            UUID userId,
            String passwordHash,
            String displayName,
            IdentityUserStatus status
    ) {
        return jdbcTemplate.query(
                """
                update identity_user
                set password_hash = ?,
                    display_name = ?,
                    authority = ?,
                    status = ?,
                    updated_at = current_timestamp
                where user_id = ?
                returning user_id, tenant_id, username, password_hash, status, display_name, authority, created_at, updated_at
                """,
                ROW_MAPPER,
                passwordHash,
                displayName,
                IdentityUserAuthority.ADMIN.name(),
                status.name(),
                userId
        ).getFirst();
    }
}

