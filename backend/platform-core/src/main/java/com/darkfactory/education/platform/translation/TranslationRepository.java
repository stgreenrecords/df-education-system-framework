package com.darkfactory.education.platform.translation;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TranslationRepository {

    private static final RowMapper<TranslationEntry> TRANSLATION_ROW_MAPPER = new TranslationRowMapper();

    private final JdbcTemplate jdbcTemplate;

    public TranslationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TranslationEntry> findAll() {
        return jdbcTemplate.query(
                """
                SELECT id, translation_key, language_code, namespace, value, version, created_at, updated_at
                FROM translation
                ORDER BY translation_key, namespace, language_code
                """,
                TRANSLATION_ROW_MAPPER
        );
    }

    public Optional<TranslationEntry> findById(UUID id) {
        List<TranslationEntry> results = jdbcTemplate.query(
                """
                SELECT id, translation_key, language_code, namespace, value, version, created_at, updated_at
                FROM translation
                WHERE id = ?
                """,
                TRANSLATION_ROW_MAPPER,
                id
        );
        return results.stream().findFirst();
    }

    public Optional<TranslationEntry> findByNaturalKey(String translationKey, String namespace, String languageCode) {
        List<TranslationEntry> results = jdbcTemplate.query(
                """
                SELECT id, translation_key, language_code, namespace, value, version, created_at, updated_at
                FROM translation
                WHERE translation_key = ?
                  AND namespace = ?
                  AND language_code = ?
                """,
                TRANSLATION_ROW_MAPPER,
                translationKey,
                namespace,
                languageCode
        );
        return results.stream().findFirst();
    }

    public TranslationEntry updateValue(UUID id, String newValue) {
        List<TranslationEntry> results = jdbcTemplate.query(
                """
                UPDATE translation
                   SET value = ?,
                       version = version + 1,
                       updated_at = CURRENT_TIMESTAMP
                 WHERE id = ?
             RETURNING id, translation_key, language_code, namespace, value, version, created_at, updated_at
                """,
                TRANSLATION_ROW_MAPPER,
                newValue,
                id
        );

        if (results.isEmpty()) {
            throw new EmptyResultDataAccessException("No translation found for id " + id, 1);
        }

        return results.get(0);
    }

    private static final class TranslationRowMapper implements RowMapper<TranslationEntry> {

        @Override
        public TranslationEntry mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return new TranslationEntry(
                    resultSet.getObject("id", UUID.class),
                    resultSet.getString("translation_key"),
                    resultSet.getString("language_code"),
                    resultSet.getString("namespace"),
                    resultSet.getString("value"),
                    resultSet.getInt("version"),
                    resultSet.getObject("created_at", OffsetDateTime.class),
                    resultSet.getObject("updated_at", OffsetDateTime.class)
            );
        }
    }
}

