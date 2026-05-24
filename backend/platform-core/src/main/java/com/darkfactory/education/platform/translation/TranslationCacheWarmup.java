package com.darkfactory.education.platform.translation;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TranslationCacheWarmup {

    private final TranslationRepository translationRepository;
    private final TranslationCache translationCache;

    public TranslationCacheWarmup(
            TranslationRepository translationRepository,
            TranslationCache translationCache
    ) {
        this.translationRepository = translationRepository;
        this.translationCache = translationCache;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warmAllTranslationsOnStartup() {
        warmAllTranslations();
    }

    public void warmAllTranslations() {
        translationCache.clear();
        translationRepository.findAll().forEach(translationCache::put);
    }
}

