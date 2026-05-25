package com.darkfactory.education.platform.tenant;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class TenantBootstrapRunner implements ApplicationRunner {

    private final TenantProperties tenantProperties;
    private final TenantRepository tenantRepository;
    private final TenantContextService tenantContextService;

    public TenantBootstrapRunner(
            TenantProperties tenantProperties,
            TenantRepository tenantRepository,
            TenantContextService tenantContextService
    ) {
        this.tenantProperties = tenantProperties;
        this.tenantRepository = tenantRepository;
        this.tenantContextService = tenantContextService;
    }

    @Override
    public void run(ApplicationArguments args) {
        TenantBootstrapConfiguration configuredTenant = tenantProperties.toBootstrapConfiguration();
        TenantRecord persistedTenant = tenantRepository.findSingleTenant()
                .map(existing -> reconcileTenant(existing, configuredTenant))
                .orElseGet(() -> tenantRepository.insert(configuredTenant));

        tenantContextService.refreshContext();

        if (!persistedTenant.countryCode().equals(configuredTenant.countryCode())) {
            throw new IllegalStateException("Active deployment tenant country code mismatch after bootstrap.");
        }
    }

    private TenantRecord reconcileTenant(TenantRecord existing, TenantBootstrapConfiguration configuredTenant) {
        if (!existing.countryCode().equals(configuredTenant.countryCode())) {
            throw new IllegalStateException(
                    "Configured deployment tenant country code '" + configuredTenant.countryCode()
                            + "' does not match persisted tenant '" + existing.countryCode() + "'."
            );
        }

        if (matches(existing, configuredTenant)) {
            return existing;
        }

        return tenantRepository.update(existing, configuredTenant);
    }

    private boolean matches(TenantRecord existing, TenantBootstrapConfiguration configuredTenant) {
        return existing.displayName().equals(configuredTenant.displayName())
                && existing.defaultTimezone().equals(configuredTenant.defaultTimezone())
                && existing.defaultLocale().equals(configuredTenant.defaultLocale());
    }
}

