package com.darkfactory.education.identityaccess.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/identity/auth")
public class AuthenticationController {

    private final IdentityAuthenticationService identityAuthenticationService;

    public AuthenticationController(IdentityAuthenticationService identityAuthenticationService) {
        this.identityAuthenticationService = identityAuthenticationService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        try {
            return LoginResponse.fromIssuedToken(
                    identityAuthenticationService.login(request.username(), request.password())
            );
        } catch (InvalidCredentialsException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, exception.getMessage(), exception);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }
}

