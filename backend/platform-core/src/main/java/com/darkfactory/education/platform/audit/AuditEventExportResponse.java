package com.darkfactory.education.platform.audit;

import java.time.OffsetDateTime;
import java.util.List;

public record AuditEventExportResponse(
        OffsetDateTime exportedAt,
        int exportedCount,
        List<AuditEventResponse> events
) {

    public static AuditEventExportResponse from(List<AuditEventEntry> entries) {
        List<AuditEventResponse> events = entries.stream().map(AuditEventResponse::from).toList();
        return new AuditEventExportResponse(
                OffsetDateTime.now(),
                events.size(),
                events
        );
    }
}

