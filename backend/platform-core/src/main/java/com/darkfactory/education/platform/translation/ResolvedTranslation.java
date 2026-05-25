package com.darkfactory.education.platform.translation;

public record ResolvedTranslation(
        boolean found,
        TranslationEntry translationEntry,
        String requestedLanguage,
        String resolvedLanguage,
        boolean fallbackApplied,
        boolean cacheHit
) {

    public static ResolvedTranslation missing(String requestedLanguage) {
        return new ResolvedTranslation(false, null, requestedLanguage, null, false, false);
    }
}

