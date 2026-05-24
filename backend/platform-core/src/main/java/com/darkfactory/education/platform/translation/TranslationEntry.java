package com.darkfactory.education.platform.translation;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TranslationEntry(
        UUID id,
        String translationKey,
        String languageCode,
        String namespace,
        String value,
        int version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

