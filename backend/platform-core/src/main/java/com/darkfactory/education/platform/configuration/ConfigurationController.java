package com.darkfactory.education.platform.configuration;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/platform/configuration")
@Tag(name = "Platform Configuration", description = "Generic configuration inheritance endpoints")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @PutMapping("/fields/{fieldKey}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Upsert a configuration field definition",
            description = "Creates or updates the generic field behavior metadata used by the configuration inheritance engine."
    )
    public ConfigurationFieldDefinitionResponse upsertFieldDefinition(
            @PathVariable("fieldKey") String fieldKey,
            @RequestBody ConfigurationFieldDefinitionUpsertRequest request
    ) {
        try {
            return ConfigurationFieldDefinitionResponse.from(configurationService.upsertFieldDefinition(
                    fieldKey,
                    request.valueType(),
                    request.mergeStrategy(),
                    request.overridesAllowed()
            ));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    @PutMapping("/values")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Upsert a scoped configuration value",
            description = "Stores or updates a configuration value at the target scope in the supplied generic scope path and rejects writes blocked by ancestor locks."
    )
    public ConfigurationValueResponse upsertValue(@RequestBody ConfigurationValueUpsertRequest request) {
        try {
            return ConfigurationValueResponse.from(configurationService.upsertValue(
                    request.fieldKey(),
                    ScopePath.fromRequests(request.scopePath()),
                    request.value(),
                    request.locked()
            ));
        } catch (LockedConfigurationOverrideException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
        } catch (NoSuchElementException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    @PostMapping("/resolve")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Resolve an effective configuration value",
            description = "Returns the effective inherited configuration value for a field and generic scope path using tenant-rooted scope resolution."
    )
    public ConfigurationResolveResponse resolve(@RequestBody ConfigurationResolveRequest request) {
        try {
            return ConfigurationResolveResponse.from(configurationService.resolve(
                    request.fieldKey(),
                    ScopePath.fromRequests(request.scopePath())
            ));
        } catch (NoSuchElementException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }
}

