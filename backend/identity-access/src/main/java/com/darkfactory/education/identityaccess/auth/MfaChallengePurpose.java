package com.darkfactory.education.identityaccess.auth;

import java.util.Locale;

public enum MfaChallengePurpose {
    ENROLL,
    VERIFY;

    public static MfaChallengePurpose parse(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("MFA challenge purpose is required.");
        }

        try {
            return MfaChallengePurpose.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unsupported MFA challenge purpose: " + value, exception);
        }
    }
}

