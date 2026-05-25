package com.darkfactory.education.platform.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Generic platform status endpoint.
 * Provides a proof point for OpenAPI contract generation.
 * Contains no country-specific, language-specific, or product-domain behavior.
 */
@RestController
@RequestMapping("/platform")
@Tag(name = "Platform", description = "Generic platform endpoints")
public class PlatformStatusController {

    @GetMapping("/status")
    @Operation(summary = "Get platform status", description = "Returns the current service name and status.")
    public PlatformStatusResponse status() {
        return new PlatformStatusResponse("education-system-framework", "UP");
    }
}

