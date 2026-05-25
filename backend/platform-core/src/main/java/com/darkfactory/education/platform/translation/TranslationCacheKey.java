package com.darkfactory.education.platform.translation;

public record TranslationCacheKey(String translationKey, String namespace, String languageCode) {

    public static TranslationCacheKey from(TranslationEntry translationEntry) {
        return new TranslationCacheKey(
                translationEntry.translationKey(),
                translationEntry.namespace(),
                translationEntry.languageCode()
        );
    }
}

