package com.darkfactory.education.identityaccess.auth;

import java.util.UUID;

public record CurrentUserResponse(
        UUID userId,
        UUID tenantId,
        String username,
        String displayName,
        String authority
) {
    public static CurrentUserResponse fromPrincipal(AuthenticatedUserPrincipal principal) {
        return new CurrentUserResponse(
                principal.userId(),
                principal.tenantId(),
                principal.username(),
                principal.displayName(),
                principal.authority().name()
        );
    }
}

