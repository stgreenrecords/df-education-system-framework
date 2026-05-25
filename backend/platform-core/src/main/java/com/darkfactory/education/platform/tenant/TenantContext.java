package com.darkfactory.education.platform.tenant;

import java.util.UUID;

public record TenantContext(
        UUID tenantId,
        String countryCode,
        String displayName,
        String defaultTimezone,
        String defaultLocale
) {
}

