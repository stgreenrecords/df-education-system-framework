package com.darkfactory.education.platform.audit;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/platform/audit")
@Tag(name = "Platform Audit", description = "Generic append-only audit query and export endpoints")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Query audit events",
            description = "Returns generic append-only audit events for the active deployment tenant with optional entity, actor, and time-range filters."
    )
    public AuditEventListResponse listEvents(
            @RequestParam(value = "entityType", required = false) String entityType,
            @RequestParam(value = "actor", required = false) String actor,
            @RequestParam(value = "from", required = false) OffsetDateTime from,
            @RequestParam(value = "to", required = false) OffsetDateTime to
    ) {
        try {
            return AuditEventListResponse.from(auditService.findEvents(entityType, actor, from, to));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    @GetMapping("/events/export")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Export audit events",
            description = "Exports generic append-only audit events for the active deployment tenant with optional entity, actor, and time-range filters."
    )
    public AuditEventExportResponse exportEvents(
            @RequestParam(value = "entityType", required = false) String entityType,
            @RequestParam(value = "actor", required = false) String actor,
            @RequestParam(value = "from", required = false) OffsetDateTime from,
            @RequestParam(value = "to", required = false) OffsetDateTime to
    ) {
        try {
            return AuditEventExportResponse.from(auditService.findEvents(entityType, actor, from, to));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }
}

