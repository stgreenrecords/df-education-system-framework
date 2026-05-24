package com.darkfactory.education.platform.tenant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/platform/tenant")
@Tag(name = "Platform Tenant", description = "Sovereign deployment tenant metadata")
public class TenantController {

    private final TenantContextService tenantContextService;

    public TenantController(TenantContextService tenantContextService) {
        this.tenantContextService = tenantContextService;
    }

    @GetMapping
    @Operation(
            summary = "Get active deployment tenant",
            description = "Returns the active sovereign deployment tenant metadata for the current runtime."
    )
    public TenantResponse activeTenant() {
        return TenantResponse.fromContext(tenantContextService.getActiveTenant());
    }
}

