package com.darkfactory.education.identityaccess.auth;

import org.springframework.util.StringUtils;

import java.util.Locale;

final class IdentityUserNormalizer {

    private IdentityUserNormalizer() {
    }

    static String normalizeUsername(String username) {
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        return username.trim().toLowerCase(Locale.ROOT);
    }

    static String normalizePassword(String password, String fieldName) {
        if (!StringUtils.hasText(password)) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return password;
    }

    static String normalizeDisplayName(String displayName) {
        if (!StringUtils.hasText(displayName)) {
            return null;
        }
        return displayName.trim();
    }

    static IdentityUserStatus parseStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return IdentityUserStatus.ACTIVE;
        }
        try {
            return IdentityUserStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Status must be ACTIVE or DISABLED", exception);
        }
    }
}

