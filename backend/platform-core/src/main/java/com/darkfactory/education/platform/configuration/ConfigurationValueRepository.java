package com.darkfactory.education.platform.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class ConfigurationValueRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public ConfigurationValueRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public ConfigurationValueEntry upsert(
            UUID tenantId,
            String fieldKey,
            ConfigurationScope scope,
            JsonNode value,
            boolean locked
    ) {
        return jdbcTemplate.query(
                """
                INSERT INTO configuration_value (
                    id,
                    tenant_id,
                    field_key,
                    scope_type,
                    scope_key,
                    value_json,
                    locked,
                    created_at,
                    updated_at
                )
                VALUES (?, ?, ?, ?, ?, CAST(? AS jsonb), ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                ON CONFLICT (tenant_id, field_key, scope_type, scope_key)
                DO UPDATE SET value_json = EXCLUDED.value_json,
                              locked = EXCLUDED.locked,
                              updated_at = CURRENT_TIMESTAMP
                RETURNING id, tenant_id, field_key, scope_type, scope_key, value_json, locked, created_at, updated_at
                """,
                rowMapper(),
                UUID.randomUUID(),
                tenantId,
                fieldKey,
                scope.scopeType().name(),
                scope.scopeKey(),
                toJson(value),
                locked
        ).get(0);
    }

    public List<ConfigurationValueEntry> findByTenantAndFieldKey(UUID tenantId, String fieldKey) {
        return jdbcTemplate.query(
                """
                SELECT id, tenant_id, field_key, scope_type, scope_key, value_json, locked, created_at, updated_at
                FROM configuration_value
                WHERE tenant_id = ?
                  AND field_key = ?
                """,
                rowMapper(),
                tenantId,
                fieldKey
        );
    }

    private RowMapper<ConfigurationValueEntry> rowMapper() {
        return new ConfigurationValueRowMapper(objectMapper);
    }

    private String toJson(JsonNode value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize configuration value", exception);
        }
    }

    private static final class ConfigurationValueRowMapper implements RowMapper<ConfigurationValueEntry> {

        private final ObjectMapper objectMapper;

        private ConfigurationValueRowMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public ConfigurationValueEntry mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return new ConfigurationValueEntry(
                    resultSet.getObject("id", UUID.class),
                    resultSet.getObject("tenant_id", UUID.class),
                    resultSet.getString("field_key"),
                    ConfigurationScopeType.from(resultSet.getString("scope_type")),
                    resultSet.getString("scope_key"),
                    parseValue(resultSet.getString("value_json")),
                    resultSet.getBoolean("locked"),
                    resultSet.getObject("created_at", OffsetDateTime.class),
                    resultSet.getObject("updated_at", OffsetDateTime.class)
            );
        }

        private JsonNode parseValue(String json) {
            try {
                return objectMapper.readTree(json);
            } catch (JsonProcessingException exception) {
                throw new IllegalStateException("Failed to parse configuration JSON value", exception);
            }
        }
    }
}

