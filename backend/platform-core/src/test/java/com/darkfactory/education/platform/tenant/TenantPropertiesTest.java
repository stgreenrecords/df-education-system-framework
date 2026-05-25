package com.darkfactory.education.platform.tenant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TenantPropertiesTest {

    @Test
    void bootstrapConfigurationNormalizesCountryCodeAndLocale() {
        TenantProperties properties = new TenantProperties();
        properties.setCountryCode("pl");
        properties.setDisplayName(" Poland Reference Deployment ");
        properties.setTimezone("Europe/Warsaw");
        properties.setLocale("pl-pl");

        TenantBootstrapConfiguration configuration = properties.toBootstrapConfiguration();

        assertThat(configuration.countryCode()).isEqualTo("PL");
        assertThat(configuration.displayName()).isEqualTo("Poland Reference Deployment");
        assertThat(configuration.defaultTimezone()).isEqualTo("Europe/Warsaw");
        assertThat(configuration.defaultLocale()).isEqualTo("pl-PL");
    }

    @Test
    void bootstrapConfigurationRejectsInvalidTimezone() {
        TenantProperties properties = new TenantProperties();
        properties.setCountryCode("PL");
        properties.setDisplayName("Poland Reference Deployment");
        properties.setTimezone("Not/A-Timezone");
        properties.setLocale("pl-PL");

        assertThatThrownBy(properties::toBootstrapConfiguration)
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Not/A-Timezone");
    }

    @Test
    void bootstrapConfigurationRejectsInvalidLocale() {
        TenantProperties properties = new TenantProperties();
        properties.setCountryCode("PL");
        properties.setDisplayName("Poland Reference Deployment");
        properties.setTimezone("Europe/Warsaw");
        properties.setLocale("---");

        assertThatThrownBy(properties::toBootstrapConfiguration)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("edu.tenant.locale");
    }
}

