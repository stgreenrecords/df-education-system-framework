package com.darkfactory.education.identityaccess.auth;

import java.time.OffsetDateTime;

public record ParsedMfaChallengeToken(
        AuthenticatedUserPrincipal principal,
        MfaChallengePurpose purpose,
        OffsetDateTime expiresAt
) {
}

