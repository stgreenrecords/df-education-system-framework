package com.darkfactory.education.platform.tenant;

import java.util.UUID;

public record TenantRecord(
        UUID tenantId,
        String countryCode,
        String displayName,
        String defaultTimezone,
        String defaultLocale
) {
}

