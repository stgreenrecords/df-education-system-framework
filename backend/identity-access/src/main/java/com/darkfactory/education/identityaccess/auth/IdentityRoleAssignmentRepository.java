package com.darkfactory.education.identityaccess.auth;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class IdentityRoleAssignmentRepository {

    private static final RowMapper<IdentityRoleAssignmentRecord> ROW_MAPPER = (resultSet, rowNum) -> new IdentityRoleAssignmentRecord(
            resultSet.getObject("assignment_id", UUID.class),
            resultSet.getObject("tenant_id", UUID.class),
            resultSet.getObject("user_id", UUID.class),
            IdentityRoleCode.valueOf(resultSet.getString("role_code")),
            IdentityScopePath.fromStorageString(resultSet.getString("scope_path")),
            resultSet.getObject("created_at", OffsetDateTime.class),
            resultSet.getObject("updated_at", OffsetDateTime.class)
    );

    private final JdbcTemplate jdbcTemplate;

    public IdentityRoleAssignmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<IdentityRoleAssignmentRecord> findByUserId(UUID tenantId, UUID userId) {
        return jdbcTemplate.query(
                """
                select assignment_id, tenant_id, user_id, role_code, scope_path, created_at, updated_at
                from identity_role_assignment
                where tenant_id = ? and user_id = ?
                order by created_at asc
                """,
                ROW_MAPPER,
                tenantId,
                userId
        );
    }

    public IdentityRoleAssignmentRecord insert(UUID tenantId, UUID userId, IdentityRoleCode roleCode, IdentityScopePath scopePath) {
        try {
            return jdbcTemplate.query(
                    """
                    insert into identity_role_assignment (
                        assignment_id,
                        tenant_id,
                        user_id,
                        role_code,
                        scope_path,
                        created_at,
                        updated_at
                    )
                    values (?, ?, ?, ?, ?, current_timestamp, current_timestamp)
                    returning assignment_id, tenant_id, user_id, role_code, scope_path, created_at, updated_at
                    """,
                    ROW_MAPPER,
                    UUID.randomUUID(),
                    tenantId,
                    userId,
                    roleCode.name(),
                    scopePath.toStorageString()
            ).getFirst();
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateRoleAssignmentException(
                    "Duplicate role assignment for role '%s' at scope '%s'.".formatted(roleCode.apiValue(), scopePath.toStorageString())
            );
        }
    }
}

