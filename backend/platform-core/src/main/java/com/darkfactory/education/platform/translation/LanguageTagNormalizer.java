package com.darkfactory.education.platform.translation;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.IllformedLocaleException;
import java.util.Locale;

@Component
public class LanguageTagNormalizer {

    public String normalize(String languageTag) {
        if (!StringUtils.hasText(languageTag)) {
            throw new IllegalArgumentException("Language tag must not be blank");
        }

        String candidate = languageTag.trim();

        try {
            Locale locale = new Locale.Builder()
                    .setLanguageTag(candidate)
                    .build();
            String normalized = locale.toLanguageTag();

            if (!StringUtils.hasText(normalized) || "und".equalsIgnoreCase(normalized)) {
                throw new IllegalArgumentException("Invalid BCP 47 language tag: " + languageTag);
            }

            return normalized;
        } catch (IllformedLocaleException exception) {
            throw new IllegalArgumentException("Invalid BCP 47 language tag: " + languageTag, exception);
        }
    }
}

