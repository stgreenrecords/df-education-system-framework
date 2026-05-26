package com.darkfactory.education.identityaccess.auth;

import java.time.OffsetDateTime;

public record LoginResponse(
        String accessToken,
        String tokenType,
        OffsetDateTime expiresAt,
        boolean mfaRequired,
        boolean mfaEnrollmentRequired,
        String challengeToken,
        String challengePurpose
) {
    public static LoginResponse fromIssuedToken(IssuedAccessToken issuedAccessToken) {
        return new LoginResponse(
                issuedAccessToken.accessToken(),
                "Bearer",
                issuedAccessToken.expiresAt(),
                false,
                false,
                null,
                null
        );
    }

    public static LoginResponse mfaRequired(IssuedMfaChallengeToken challengeToken) {
        return new LoginResponse(
                null,
                null,
                challengeToken.expiresAt(),
                true,
                false,
                challengeToken.challengeToken(),
                challengeToken.purpose().name()
        );
    }

    public static LoginResponse mfaEnrollmentRequired(IssuedMfaChallengeToken challengeToken) {
        return new LoginResponse(
                null,
                null,
                challengeToken.expiresAt(),
                false,
                true,
                challengeToken.challengeToken(),
                challengeToken.purpose().name()
        );
    }
}

