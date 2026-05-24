package com.darkfactory.education.platform.tenant;

import java.util.UUID;

public record TenantResponse(
        UUID tenantId,
        String countryCode,
        String displayName,
        String timezone,
        String locale
) {
    public static TenantResponse fromContext(TenantContext context) {
        return new TenantResponse(
                context.tenantId(),
                context.countryCode(),
                context.displayName(),
                context.defaultTimezone(),
                context.defaultLocale()
        );
    }
}

