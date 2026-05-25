package com.darkfactory.education.identityaccess.auth;

public record CreateUserRequest(
        String username,
        String initialPassword,
        String displayName,
        String status
) {
}

