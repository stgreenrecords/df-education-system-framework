package com.darkfactory.education.platform.translation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/translations")
@Tag(name = "Translations", description = "Generic translation storage and resolution endpoints")
public class TranslationController {

    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @GetMapping("/resolve")
    @Operation(
            summary = "Resolve a translation value",
            description = "Returns the translation for a key and language using generic fallback to the deployment default language and then the global fallback language."
    )
    public TranslationResolveResponse resolve(
            @RequestParam("key") String translationKey,
            @RequestParam("lang") String requestedLanguage,
            @RequestParam(value = "namespace", required = false) String namespace
    ) {
        try {
            ResolvedTranslation resolvedTranslation = translationService.resolve(
                    translationKey,
                    namespace,
                    requestedLanguage
            );

            if (!resolvedTranslation.found()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Translation not found");
            }

            return TranslationResolveResponse.fromResolvedTranslation(resolvedTranslation);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    @PutMapping("/{translationId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update a translation value",
            description = "Updates an existing translation, increments its version, invalidates the cached entry, and records a generic platform audit event."
    )
    public TranslationUpdateResponse update(
            @PathVariable("translationId") UUID translationId,
            @RequestBody TranslationUpdateRequest request
    ) {
        try {
            TranslationEntry updatedTranslation = translationService.updateTranslation(
                    translationId,
                    request.value(),
                    request.actor()
            );
            return TranslationUpdateResponse.fromTranslationEntry(updatedTranslation);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        } catch (EmptyResultDataAccessException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        }
    }
}

