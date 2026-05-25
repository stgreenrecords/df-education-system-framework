package com.darkfactory.education.platform.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
public class ConfigurationInheritanceBreakRequestRepository {

    private static final TypeReference<List<ConfigurationScopeRequest>> SCOPE_PATH_TYPE = new TypeReference<>() { };

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public ConfigurationInheritanceBreakRequestRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public ConfigurationInheritanceBreakRequestRecord append(
            UUID tenantId,
            String fieldKey,
            List<ConfigurationScope> targetScopePath,
            ConfigurationScope blockingAncestorScope,
            JsonNode proposedValue,
            String justification,
            String requestedBy,
            ConfigurationInheritanceBreakRequestStatus status
    ) {
        return jdbcTemplate.query(
                """
                INSERT INTO configuration_inheritance_break_request (
                    request_id,
                    tenant_id,
                    field_key,
                    target_scope_type,
                    target_scope_key,
                    target_scope_path_json,
                    blocking_ancestor_scope_type,
                    blocking_ancestor_scope_key,
                    proposed_value_json,
                    justification,
                    requested_by,
                    status,
                    created_at,
                    updated_at
                )
                VALUES (?, ?, ?, ?, ?, CAST(? AS jsonb), ?, ?, CAST(? AS jsonb), ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                RETURNING request_id,
                          tenant_id,
                          field_key,
                          target_scope_path_json,
                          blocking_ancestor_scope_type,
                          blocking_ancestor_scope_key,
                          proposed_value_json,
                          justification,
                          requested_by,
                          status,
                          created_at,
                          updated_at
                """,
                rowMapper(),
                UUID.randomUUID(),
                tenantId,
                fieldKey,
                targetScopePath.get(targetScopePath.size() - 1).scopeType().name(),
                targetScopePath.get(targetScopePath.size() - 1).scopeKey(),
                toJson(targetScopePath.stream().map(scope -> new ConfigurationScopeResponse(scope.scopeType().name(), scope.scopeKey())).toList()),
                blockingAncestorScope.scopeType().name(),
                blockingAncestorScope.scopeKey(),
                toJson(proposedValue),
                justification,
                requestedBy,
                status.name()
        ).get(0);
    }

    private RowMapper<ConfigurationInheritanceBreakRequestRecord> rowMapper() {
        return new ConfigurationInheritanceBreakRequestRowMapper(objectMapper);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize inheritance-break request data", exception);
        }
    }

    private static final class ConfigurationInheritanceBreakRequestRowMapper
            implements RowMapper<ConfigurationInheritanceBreakRequestRecord> {

        private final ObjectMapper objectMapper;

        private ConfigurationInheritanceBreakRequestRowMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public ConfigurationInheritanceBreakRequestRecord mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return new ConfigurationInheritanceBreakRequestRecord(
                    resultSet.getObject("request_id", UUID.class),
                    resultSet.getObject("tenant_id", UUID.class),
                    resultSet.getString("field_key"),
                    parseScopePath(resultSet.getString("target_scope_path_json")),
                    new ConfigurationScope(
                            ConfigurationScopeType.from(resultSet.getString("blocking_ancestor_scope_type")),
                            resultSet.getString("blocking_ancestor_scope_key")
                    ),
                    parseValue(resultSet.getString("proposed_value_json")),
                    resultSet.getString("justification"),
                    resultSet.getString("requested_by"),
                    ConfigurationInheritanceBreakRequestStatus.from(resultSet.getString("status")),
                    resultSet.getObject("created_at", OffsetDateTime.class),
                    resultSet.getObject("updated_at", OffsetDateTime.class)
            );
        }

        private List<ConfigurationScope> parseScopePath(String json) {
            try {
                return objectMapper.readValue(json, SCOPE_PATH_TYPE).stream()
                        .map(ConfigurationScopeRequest::toDomain)
                        .toList();
            } catch (JsonProcessingException exception) {
                throw new IllegalStateException("Failed to parse inheritance-break target scope path", exception);
            }
        }

        private JsonNode parseValue(String json) {
            try {
                return objectMapper.readTree(json);
            } catch (JsonProcessingException exception) {
                throw new IllegalStateException("Failed to parse inheritance-break proposed value JSON", exception);
            }
        }
    }
}

