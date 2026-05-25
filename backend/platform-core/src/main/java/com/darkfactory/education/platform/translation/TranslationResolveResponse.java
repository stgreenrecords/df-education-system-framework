package com.darkfactory.education.platform.translation;

import java.util.UUID;

public record TranslationResolveResponse(
        UUID translationId,
        String translationKey,
        String namespace,
        String requestedLanguage,
        String resolvedLanguage,
        String value,
        boolean fallbackApplied,
        boolean cacheHit
) {

    public static TranslationResolveResponse fromResolvedTranslation(ResolvedTranslation resolvedTranslation) {
        return new TranslationResolveResponse(
                resolvedTranslation.translationEntry().id(),
                resolvedTranslation.translationEntry().translationKey(),
                resolvedTranslation.translationEntry().namespace(),
                resolvedTranslation.requestedLanguage(),
                resolvedTranslation.resolvedLanguage(),
                resolvedTranslation.translationEntry().value(),
                resolvedTranslation.fallbackApplied(),
                resolvedTranslation.cacheHit()
        );
    }
}

