package com.darkfactory.education.identityaccess.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/identity/auth/mfa")
public class IdentityMfaController {

    private final IdentityMfaService identityMfaService;

    public IdentityMfaController(IdentityMfaService identityMfaService) {
        this.identityMfaService = identityMfaService;
    }

    @PostMapping("/enroll")
    public MfaEnrollmentResponse enroll(@RequestBody MfaChallengeRequest request) {
        try {
            return identityMfaService.enroll(request.challengeToken());
        } catch (InvalidMfaChallengeException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, exception.getMessage(), exception);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    @PostMapping("/activate")
    public LoginResponse activate(@RequestBody MfaCodeChallengeRequest request) {
        try {
            return identityMfaService.activate(request.challengeToken(), request.totpCode());
        } catch (InvalidMfaChallengeException | InvalidMfaCodeException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, exception.getMessage(), exception);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    @PostMapping("/verify")
    public LoginResponse verify(@RequestBody MfaCodeChallengeRequest request) {
        try {
            return identityMfaService.verify(request.challengeToken(), request.totpCode());
        } catch (InvalidMfaChallengeException | InvalidMfaCodeException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, exception.getMessage(), exception);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }
}

