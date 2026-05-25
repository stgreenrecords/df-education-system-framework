package com.darkfactory.education.platform.translation;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "edu.translation")
public class TranslationProperties {

    private String defaultLanguage;
    private String globalFallbackLanguage;
    private String defaultNamespace;
    private Duration cacheTtl;

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getGlobalFallbackLanguage() {
        return globalFallbackLanguage;
    }

    public void setGlobalFallbackLanguage(String globalFallbackLanguage) {
        this.globalFallbackLanguage = globalFallbackLanguage;
    }

    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    public void setDefaultNamespace(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
    }

    public Duration getCacheTtl() {
        return cacheTtl;
    }

    public void setCacheTtl(Duration cacheTtl) {
        this.cacheTtl = cacheTtl;
    }
}

