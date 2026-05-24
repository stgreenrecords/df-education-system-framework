package com.darkfactory.education.platform.tenant;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.ZoneId;
import java.util.Locale;

@ConfigurationProperties(prefix = "edu.tenant")
public class TenantProperties {

    private String countryCode;
    private String displayName;
    private String timezone;
    private String locale;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public TenantBootstrapConfiguration toBootstrapConfiguration() {
        String normalizedCountryCode = requireNonBlank(countryCode, "edu.tenant.country-code")
                .toUpperCase(Locale.ROOT);
        String normalizedDisplayName = requireNonBlank(displayName, "edu.tenant.display-name");
        String normalizedTimezone = ZoneId.of(requireNonBlank(timezone, "edu.tenant.timezone")).getId();

        java.util.Locale parsedLocale = java.util.Locale.forLanguageTag(
                requireNonBlank(locale, "edu.tenant.locale")
        );
        if (parsedLocale.getLanguage().isBlank()) {
            throw new IllegalStateException("Property 'edu.tenant.locale' must be a valid BCP 47 language tag.");
        }

        return new TenantBootstrapConfiguration(
                normalizedCountryCode,
                normalizedDisplayName,
                normalizedTimezone,
                parsedLocale.toLanguageTag()
        );
    }

    private String requireNonBlank(String value, String propertyName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required property '" + propertyName + "' must not be blank.");
        }
        return value.trim();
    }
}

