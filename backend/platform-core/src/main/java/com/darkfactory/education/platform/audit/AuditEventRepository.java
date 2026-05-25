package com.darkfactory.education.platform.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class AuditEventRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public AuditEventRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public AuditEventEntry append(
            UUID tenantId,
            String entityType,
            String entityId,
            String actionType,
            String actor,
            JsonNode oldValue,
            JsonNode newValue,
            JsonNode metadata
    ) {
        return jdbcTemplate.query(
                """
                INSERT INTO audit_event (
                    id,
                    tenant_id,
                    entity_type,
                    entity_id,
                    action_type,
                    actor,
                    old_value_json,
                    new_value_json,
                    metadata_json,
                    occurred_at
                )
                VALUES (?, ?, ?, ?, ?, ?, CAST(? AS jsonb), CAST(? AS jsonb), CAST(? AS jsonb), ?)
                RETURNING id, tenant_id, entity_type, entity_id, action_type, actor, old_value_json, new_value_json, metadata_json, occurred_at
                """,
                rowMapper(),
                UUID.randomUUID(),
                tenantId,
                entityType,
                entityId,
                actionType,
                actor,
                toNullableJson(oldValue),
                toNullableJson(newValue),
                toNullableJson(metadata),
                OffsetDateTime.now(ZoneOffset.UTC)
        ).get(0);
    }

    public List<AuditEventEntry> findFiltered(
            UUID tenantId,
            String entityType,
            String actor,
            OffsetDateTime from,
            OffsetDateTime to
    ) {
        StringBuilder sql = new StringBuilder(
                """
                SELECT id, tenant_id, entity_type, entity_id, action_type, actor, old_value_json, new_value_json, metadata_json, occurred_at
                FROM audit_event
                WHERE tenant_id = ?
                """
        );
        List<Object> params = new ArrayList<>();
        params.add(tenantId);

        if (entityType != null) {
            sql.append(" AND entity_type = ?");
            params.add(entityType);
        }
        if (actor != null) {
            sql.append(" AND actor = ?");
            params.add(actor);
        }
        if (from != null) {
            sql.append(" AND occurred_at >= ?");
            params.add(from);
        }
        if (to != null) {
            sql.append(" AND occurred_at <= ?");
            params.add(to);
        }

        sql.append(" ORDER BY occurred_at DESC, id DESC");

        return jdbcTemplate.query(sql.toString(), rowMapper(), params.toArray());
    }

    private RowMapper<AuditEventEntry> rowMapper() {
        return new AuditEventRowMapper(objectMapper);
    }

    private String toNullableJson(JsonNode value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize audit JSON payload", exception);
        }
    }

    private static final class AuditEventRowMapper implements RowMapper<AuditEventEntry> {

        private final ObjectMapper objectMapper;

        private AuditEventRowMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public AuditEventEntry mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return new AuditEventEntry(
                    resultSet.getObject("id", UUID.class),
                    resultSet.getObject("tenant_id", UUID.class),
                    resultSet.getString("entity_type"),
                    resultSet.getString("entity_id"),
                    resultSet.getString("action_type"),
                    resultSet.getString("actor"),
                    parseNullableJson(resultSet.getString("old_value_json")),
                    parseNullableJson(resultSet.getString("new_value_json")),
                    parseNullableJson(resultSet.getString("metadata_json")),
                    resultSet.getObject("occurred_at", OffsetDateTime.class)
            );
        }

        private JsonNode parseNullableJson(String json) {
            if (json == null) {
                return null;
            }
            try {
                return objectMapper.readTree(json);
            } catch (JsonProcessingException exception) {
                throw new IllegalStateException("Failed to parse audit JSON payload", exception);
            }
        }
    }
}

