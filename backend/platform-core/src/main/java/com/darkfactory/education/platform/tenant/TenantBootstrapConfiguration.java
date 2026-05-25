package com.darkfactory.education.platform.tenant;

public record TenantBootstrapConfiguration(
        String countryCode,
        String displayName,
        String defaultTimezone,
        String defaultLocale
) {
}

