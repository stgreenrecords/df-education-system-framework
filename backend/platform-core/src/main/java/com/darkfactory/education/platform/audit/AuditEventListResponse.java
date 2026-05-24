package com.darkfactory.education.platform.audit;

import java.util.List;

public record AuditEventListResponse(List<AuditEventResponse> events) {

    public static AuditEventListResponse from(List<AuditEventEntry> entries) {
        return new AuditEventListResponse(entries.stream().map(AuditEventResponse::from).toList());
    }
}

