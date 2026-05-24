package com.darkfactory.education.identityaccess.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/identity")
public class IdentityUserController {

    private final IdentityUserService identityUserService;

    public IdentityUserController(IdentityUserService identityUserService) {
        this.identityUserService = identityUserService;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public IdentityUserResponse createUser(
            @RequestBody CreateUserRequest request,
            Authentication authentication
    ) {
        try {
            return IdentityUserResponse.fromRecord(
                    identityUserService.registerUser(request, authenticatedPrincipal(authentication))
            );
        } catch (DuplicateUsernameException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    @GetMapping("/me")
    public CurrentUserResponse currentUser(Authentication authentication) {
        return CurrentUserResponse.fromPrincipal(authenticatedPrincipal(authentication));
    }

    private AuthenticatedUserPrincipal authenticatedPrincipal(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUserPrincipal principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return principal;
    }
}

