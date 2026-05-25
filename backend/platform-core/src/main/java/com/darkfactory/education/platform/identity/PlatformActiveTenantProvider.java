package com.darkfactory.education.platform.identity;

import com.darkfactory.education.identityaccess.auth.ActiveTenantProvider;
import com.darkfactory.education.platform.tenant.TenantContextService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PlatformActiveTenantProvider implements ActiveTenantProvider {

    private final TenantContextService tenantContextService;

    public PlatformActiveTenantProvider(TenantContextService tenantContextService) {
        this.tenantContextService = tenantContextService;
    }

    @Override
    public UUID getActiveTenantId() {
        return tenantContextService.getActiveTenant().tenantId();
    }
}

