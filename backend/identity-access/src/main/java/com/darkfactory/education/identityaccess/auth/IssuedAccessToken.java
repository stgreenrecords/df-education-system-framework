package com.darkfactory.education.identityaccess.auth;

import java.time.OffsetDateTime;

public record IssuedAccessToken(
        String accessToken,
        OffsetDateTime expiresAt
) {
}

