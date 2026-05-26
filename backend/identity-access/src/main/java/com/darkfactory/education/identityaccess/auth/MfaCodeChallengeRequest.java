package com.darkfactory.education.identityaccess.auth;

public record MfaCodeChallengeRequest(
        String challengeToken,
        String totpCode
) {
}

