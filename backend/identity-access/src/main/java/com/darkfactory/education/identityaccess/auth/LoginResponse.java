package com.darkfactory.education.identityaccess.auth;

import java.time.OffsetDateTime;

public record LoginResponse(
        String accessToken,
        String tokenType,
        OffsetDateTime expiresAt
) {
    public static LoginResponse fromIssuedToken(IssuedAccessToken issuedAccessToken) {
        return new LoginResponse(
                issuedAccessToken.accessToken(),
                "Bearer",
                issuedAccessToken.expiresAt()
        );
    }
}

