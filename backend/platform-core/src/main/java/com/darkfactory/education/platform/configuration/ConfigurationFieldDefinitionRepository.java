package com.darkfactory.education.platform.configuration;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ConfigurationFieldDefinitionRepository {

    private static final RowMapper<ConfigurationFieldDefinition> ROW_MAPPER = new ConfigurationFieldDefinitionRowMapper();

    private final JdbcTemplate jdbcTemplate;

    public ConfigurationFieldDefinitionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ConfigurationFieldDefinition upsert(String fieldKey, ConfigurationValueType valueType, ConfigurationMergeStrategy mergeStrategy, boolean overridesAllowed) {
        return jdbcTemplate.query(
                """
                INSERT INTO configuration_field_definition (
                    field_key,
                    value_type,
                    merge_strategy,
                    overrides_allowed,
                    created_at,
                    updated_at
                )
                VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                ON CONFLICT (field_key)
                DO UPDATE SET value_type = EXCLUDED.value_type,
                              merge_strategy = EXCLUDED.merge_strategy,
                              overrides_allowed = EXCLUDED.overrides_allowed,
                              updated_at = CURRENT_TIMESTAMP
                RETURNING field_key, value_type, merge_strategy, overrides_allowed, created_at, updated_at
                """,
                ROW_MAPPER,
                fieldKey,
                valueType.name(),
                mergeStrategy.name(),
                overridesAllowed
        ).get(0);
    }

    public Optional<ConfigurationFieldDefinition> findByFieldKey(String fieldKey) {
        List<ConfigurationFieldDefinition> results = jdbcTemplate.query(
                """
                SELECT field_key, value_type, merge_strategy, overrides_allowed, created_at, updated_at
                FROM configuration_field_definition
                WHERE field_key = ?
                """,
                ROW_MAPPER,
                fieldKey
        );
        return results.stream().findFirst();
    }

    private static final class ConfigurationFieldDefinitionRowMapper implements RowMapper<ConfigurationFieldDefinition> {
        @Override
        public ConfigurationFieldDefinition mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return new ConfigurationFieldDefinition(
                    resultSet.getString("field_key"),
                    ConfigurationValueType.from(resultSet.getString("value_type")),
                    ConfigurationMergeStrategy.from(resultSet.getString("merge_strategy")),
                    resultSet.getBoolean("overrides_allowed"),
                    resultSet.getObject("created_at", OffsetDateTime.class),
                    resultSet.getObject("updated_at", OffsetDateTime.class)
            );
        }
    }
}

