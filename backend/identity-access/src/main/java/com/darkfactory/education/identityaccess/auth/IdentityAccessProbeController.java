package com.darkfactory.education.identityaccess.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/identity/access")
public class IdentityAccessProbeController {

    private final IdentityAuthorizationService identityAuthorizationService;

    public IdentityAccessProbeController(IdentityAuthorizationService identityAuthorizationService) {
        this.identityAuthorizationService = identityAuthorizationService;
    }

    @GetMapping("/institutions/{institutionKey}/teaching-view")
    public IdentityAccessProbeResponse teachingView(
            @PathVariable("institutionKey") String institutionKey,
            Authentication authentication
    ) {
        AuthenticatedUserPrincipal principal = authenticatedPrincipal(authentication);
        IdentityScopePath resourceScopePath = IdentityScopePath.institution(principal.tenantId(), institutionKey);
        return authorize(principal, IdentityPermission.VIEW_INSTITUTION, resourceScopePath);
    }

    @PostMapping("/institutions/{institutionKey}/management")
    public IdentityAccessProbeResponse management(
            @PathVariable("institutionKey") String institutionKey,
            Authentication authentication
    ) {
        AuthenticatedUserPrincipal principal = authenticatedPrincipal(authentication);
        IdentityScopePath resourceScopePath = IdentityScopePath.institution(principal.tenantId(), institutionKey);
        return authorize(principal, IdentityPermission.MANAGE_INSTITUTION, resourceScopePath);
    }

    @GetMapping("/students/{institutionKey}/{studentKey}/view")
    public IdentityAccessProbeResponse studentView(
            @PathVariable("institutionKey") String institutionKey,
            @PathVariable("studentKey") String studentKey,
            Authentication authentication
    ) {
        AuthenticatedUserPrincipal principal = authenticatedPrincipal(authentication);
        IdentityScopePath resourceScopePath = IdentityScopePath.student(principal.tenantId(), institutionKey, studentKey);
        return authorize(principal, IdentityPermission.VIEW_STUDENT, resourceScopePath);
    }

    private IdentityAccessProbeResponse authorize(
            AuthenticatedUserPrincipal principal,
            IdentityPermission permission,
            IdentityScopePath resourceScopePath
    ) {
        try {
            identityAuthorizationService.requirePermission(
                    principal,
                    permission,
                    resourceScopePath,
                    "The current user is not allowed to access the requested resource scope."
            );
            return IdentityAccessProbeResponse.of(principal, permission, resourceScopePath);
        } catch (AuthorizationDeniedException exception) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, exception.getMessage(), exception);
        }
    }

    private AuthenticatedUserPrincipal authenticatedPrincipal(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUserPrincipal principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return principal;
    }
}

