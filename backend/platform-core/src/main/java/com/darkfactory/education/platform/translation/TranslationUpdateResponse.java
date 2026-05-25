package com.darkfactory.education.platform.translation;

import java.util.UUID;

public record TranslationUpdateResponse(
        UUID translationId,
        String translationKey,
        String namespace,
        String languageCode,
        String value,
        int version
) {

    public static TranslationUpdateResponse fromTranslationEntry(TranslationEntry translationEntry) {
        return new TranslationUpdateResponse(
                translationEntry.id(),
                translationEntry.translationKey(),
                translationEntry.namespace(),
                translationEntry.languageCode(),
                translationEntry.value(),
                translationEntry.version()
        );
    }
}

