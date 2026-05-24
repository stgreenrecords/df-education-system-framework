package com.darkfactory.education.platform.config;

import com.darkfactory.education.platform.translation.TranslationCache;
import com.darkfactory.education.platform.translation.TranslationProperties;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@EnableConfigurationProperties(TranslationProperties.class)
public class TranslationCacheConfiguration {

    @Bean
    public CacheManager cacheManager(TranslationProperties translationProperties) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(TranslationCache.CACHE_NAME);
        cacheManager.setAllowNullValues(false);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(translationProperties.getCacheTtl())
                .maximumSize(10_000));
        return cacheManager;
    }
}

