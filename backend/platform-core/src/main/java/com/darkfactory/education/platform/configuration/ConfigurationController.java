package com.darkfactory.education.platform.configuration;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/validate")
    @Operation(
            summary = "Validate a scoped configuration change",
            description = "Checks whether a proposed configuration write is valid without mutating state and returns conflict details for blocked overrides."
    )
    public ResponseEntity<ConfigurationValidationResponse> validate(@RequestBody ConfigurationValidationRequest request) {
        try {
            ConfigurationValidationResponse response = configurationService.validateValue(
                    request.fieldKey(),
                    ScopePath.fromRequests(request.scopePath()),
                    request.value(),
                    request.locked()
            );
            return response.valid()
                    ? ResponseEntity.ok(response)
                    : ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (NoSuchElementException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    @PostMapping("/inheritance-break-requests")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Submit an inheritance-break request",
            description = "Records a justification-backed request for an exceptional inheritance break without automatically bypassing ancestor locks."
    )
    public ConfigurationInheritanceBreakRequestResponse submitInheritanceBreakRequest(
            @RequestBody ConfigurationInheritanceBreakRequestCreateRequest request
    ) {
        try {
            return ConfigurationInheritanceBreakRequestResponse.from(configurationService.submitInheritanceBreakRequest(
                    request.fieldKey(),
                    ScopePath.fromRequests(request.targetScopePath()),
                    request.proposedValue(),
                    request.justification(),
                    request.requestedBy()
            ));
        } catch (NoSuchElementException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    @PostMapping("/compatibility-report")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Generate a configuration compatibility report",
            description = "Evaluates how a proposed ancestor configuration change would affect institution-scope overrides and returns a structured impact report."
    )
    public ConfigurationCompatibilityReportResponse compatibilityReport(
            @RequestBody ConfigurationCompatibilityReportRequest request
    ) {
        try {
            return configurationService.generateCompatibilityReport(
                    request.fieldKey(),
                    ScopePath.fromRequests(request.scopePath()),
                    request.proposedValue()
            );
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

