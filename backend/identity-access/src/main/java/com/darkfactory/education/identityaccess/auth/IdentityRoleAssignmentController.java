package com.darkfactory.education.identityaccess.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/identity")
public class IdentityRoleAssignmentController {

    private final IdentityRoleAssignmentService identityRoleAssignmentService;

    public IdentityRoleAssignmentController(IdentityRoleAssignmentService identityRoleAssignmentService) {
        this.identityRoleAssignmentService = identityRoleAssignmentService;
    }

    @PostMapping("/role-assignments")
    @ResponseStatus(HttpStatus.CREATED)
    public IdentityRoleAssignmentResponse createRoleAssignment(
            @RequestBody CreateRoleAssignmentRequest request,
            Authentication authentication
    ) {
        try {
            return IdentityRoleAssignmentResponse.fromRecord(
                    identityRoleAssignmentService.assignRole(request, authenticatedPrincipal(authentication))
            );
        } catch (DuplicateRoleAssignmentException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
        } catch (AuthorizationDeniedException exception) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, exception.getMessage(), exception);
        } catch (NoSuchElementException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    @GetMapping("/users/{userId}/role-assignments")
    public List<IdentityRoleAssignmentResponse> listRoleAssignments(
            @PathVariable("userId") UUID userId,
            Authentication authentication
    ) {
        try {
            return identityRoleAssignmentService.listRoleAssignments(userId, authenticatedPrincipal(authentication)).stream()
                    .map(IdentityRoleAssignmentResponse::fromRecord)
                    .toList();
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

