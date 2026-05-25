package com.darkfactory.education.platform.translation;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class TranslationCache {

    public static final String CACHE_NAME = "translationEntries";

    private final Cache cache;

    public TranslationCache(CacheManager cacheManager) {
        this.cache = Objects.requireNonNull(
                cacheManager.getCache(CACHE_NAME),
                "Translation cache 'translationEntries' must be configured"
        );
    }

    public Optional<TranslationEntry> get(TranslationCacheKey cacheKey) {
        return Optional.ofNullable(cache.get(cacheKey, TranslationEntry.class));
    }

    public void put(TranslationEntry translationEntry) {
        cache.put(TranslationCacheKey.from(translationEntry), translationEntry);
    }

    public void evict(TranslationCacheKey cacheKey) {
        cache.evict(cacheKey);
    }

    public void clear() {
        cache.clear();
    }

    public boolean contains(TranslationCacheKey cacheKey) {
        return get(cacheKey).isPresent();
    }
}

