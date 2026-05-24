package com.darkfactory.education.platform.tenant;

import org.springframework.stereotype.Service;

@Service
public class TenantContextService {

    private final TenantRepository tenantRepository;

    private volatile TenantContext cachedTenantContext;

    public TenantContextService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public TenantContext getActiveTenant() {
        TenantContext current = cachedTenantContext;
        if (current != null) {
            return current;
        }

        synchronized (this) {
            if (cachedTenantContext == null) {
                cachedTenantContext = loadActiveTenant();
            }
            return cachedTenantContext;
        }
    }

    public synchronized TenantContext refreshContext() {
        cachedTenantContext = loadActiveTenant();
        return cachedTenantContext;
    }

    private TenantContext loadActiveTenant() {
        TenantRecord tenant = tenantRepository.findSingleTenant()
                .orElseThrow(() -> new IllegalStateException("No active deployment tenant record found."));

        return new TenantContext(
                tenant.tenantId(),
                tenant.countryCode(),
                tenant.displayName(),
                tenant.defaultTimezone(),
                tenant.defaultLocale()
        );
    }
}

