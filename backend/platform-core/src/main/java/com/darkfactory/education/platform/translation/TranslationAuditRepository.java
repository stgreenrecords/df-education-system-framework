package com.darkfactory.education.platform.translation;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Repository
public class TranslationAuditRepository {

    private final JdbcTemplate jdbcTemplate;

    public TranslationAuditRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void recordChange(TranslationEntry before, TranslationEntry after, String actor) {
        jdbcTemplate.update(
                """
                INSERT INTO translation_audit (
                    id,
                    translation_id,
                    actor,
                    translation_key,
                    language_code,
                    namespace,
                    old_value,
                    new_value,
                    changed_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                UUID.randomUUID(),
                after.id(),
                actor,
                after.translationKey(),
                after.languageCode(),
                after.namespace(),
                before.value(),
                after.value(),
                OffsetDateTime.now(ZoneOffset.UTC)
        );
    }
}

