package com.darkfactory.education.identityaccess.auth;

import java.time.OffsetDateTime;

public record IssuedMfaChallengeToken(
        String challengeToken,
        OffsetDateTime expiresAt,
        MfaChallengePurpose purpose
) {
}

