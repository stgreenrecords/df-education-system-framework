package com.darkfactory.education.platform.translation;

import com.darkfactory.education.platform.audit.AuditEventWriteCommand;
import com.darkfactory.education.platform.audit.AuditService;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TranslationService {

    private final TranslationRepository translationRepository;
    private final AuditService auditService;
    private final TranslationCache translationCache;
    private final TranslationFallbackResolver translationFallbackResolver;
    private final TranslationProperties translationProperties;
    private final LanguageTagNormalizer languageTagNormalizer;

    public TranslationService(
            TranslationRepository translationRepository,
            AuditService auditService,
            TranslationCache translationCache,
            TranslationFallbackResolver translationFallbackResolver,
            TranslationProperties translationProperties,
            LanguageTagNormalizer languageTagNormalizer
    ) {
        this.translationRepository = translationRepository;
        this.auditService = auditService;
        this.translationCache = translationCache;
        this.translationFallbackResolver = translationFallbackResolver;
        this.translationProperties = translationProperties;
        this.languageTagNormalizer = languageTagNormalizer;
    }

    public ResolvedTranslation resolve(String translationKey, String namespace, String requestedLanguage) {
        String normalizedTranslationKey = normalizeTranslationKey(translationKey);
        String normalizedNamespace = normalizeNamespace(namespace);
        List<String> candidateLanguages = translationFallbackResolver.candidateLanguages(requestedLanguage);
        String normalizedRequestedLanguage = candidateLanguages.get(0);

        for (String candidateLanguage : candidateLanguages) {
            TranslationCacheKey cacheKey = new TranslationCacheKey(
                    normalizedTranslationKey,
                    normalizedNamespace,
                    candidateLanguage
            );

            Optional<TranslationEntry> cachedTranslation = translationCache.get(cacheKey);
            if (cachedTranslation.isPresent()) {
                return new ResolvedTranslation(
                        true,
                        cachedTranslation.get(),
                        normalizedRequestedLanguage,
                        candidateLanguage,
                        !candidateLanguage.equals(normalizedRequestedLanguage),
                        true
                );
            }

            Optional<TranslationEntry> storedTranslation = translationRepository.findByNaturalKey(
                    normalizedTranslationKey,
                    normalizedNamespace,
                    candidateLanguage
            );
            if (storedTranslation.isPresent()) {
                translationCache.put(storedTranslation.get());
                return new ResolvedTranslation(
                        true,
                        storedTranslation.get(),
                        normalizedRequestedLanguage,
                        candidateLanguage,
                        !candidateLanguage.equals(normalizedRequestedLanguage),
                        false
                );
            }
        }

        return ResolvedTranslation.missing(normalizedRequestedLanguage);
    }

    public TranslationEntry updateTranslation(UUID translationId, String newValue, String actor) {
        TranslationEntry existing = translationRepository.findById(translationId)
                .orElseThrow(() -> new EmptyResultDataAccessException("No translation found for id " + translationId, 1));

        String normalizedValue = normalizeTranslatedValue(newValue);
        String normalizedActor = normalizeActor(actor);

        translationCache.evict(TranslationCacheKey.from(existing));

        TranslationEntry updated = translationRepository.updateValue(translationId, normalizedValue);
        auditService.recordEvent(new AuditEventWriteCommand(
                "TRANSLATION",
                translationId.toString(),
                "UPDATE",
                normalizedActor,
                existing,
                updated,
                null
        ));
        translationCache.put(updated);

        return updated;
    }

    public String normalizeNamespace(String namespace) {
        if (!StringUtils.hasText(namespace)) {
            return translationProperties.getDefaultNamespace();
        }
        return namespace.trim();
    }

    public String normalizeLanguage(String language) {
        return languageTagNormalizer.normalize(language);
    }

    private String normalizeTranslationKey(String translationKey) {
        if (!StringUtils.hasText(translationKey)) {
            throw new IllegalArgumentException("Translation key must not be blank");
        }
        return translationKey.trim();
    }

    private String normalizeTranslatedValue(String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("Translation value must not be blank");
        }
        return value.trim();
    }

    private String normalizeActor(String actor) {
        if (!StringUtils.hasText(actor)) {
            throw new IllegalArgumentException("Actor must not be blank");
        }
        return actor.trim();
    }
}

