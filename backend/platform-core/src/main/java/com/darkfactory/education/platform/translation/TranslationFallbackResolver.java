package com.darkfactory.education.platform.translation;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Component
public class TranslationFallbackResolver {

    private final TranslationProperties translationProperties;
    private final LanguageTagNormalizer languageTagNormalizer;

    public TranslationFallbackResolver(
            TranslationProperties translationProperties,
            LanguageTagNormalizer languageTagNormalizer
    ) {
        this.translationProperties = translationProperties;
        this.languageTagNormalizer = languageTagNormalizer;
    }

    public List<String> candidateLanguages(String requestedLanguage) {
        LinkedHashSet<String> candidates = new LinkedHashSet<>();
        candidates.add(languageTagNormalizer.normalize(requestedLanguage));
        candidates.add(languageTagNormalizer.normalize(translationProperties.getDefaultLanguage()));
        candidates.add(languageTagNormalizer.normalize(translationProperties.getGlobalFallbackLanguage()));
        return new ArrayList<>(candidates);
    }
}

