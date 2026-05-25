package com.darkfactory.education.identityaccess.auth;

public record LoginRequest(
        String username,
        String password
) {
}

