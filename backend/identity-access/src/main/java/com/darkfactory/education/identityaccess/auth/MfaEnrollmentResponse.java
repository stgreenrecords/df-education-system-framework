package com.darkfactory.education.identityaccess.auth;

import java.util.UUID;

public record MfaEnrollmentResponse(
        UUID factorId,
        String factorType,
        String secret,
        String issuer,
        String accountLabel,
        String provisioningUri
) {
    public static MfaEnrollmentResponse forTotp(
            UUID factorId,
            String secret,
            String issuer,
            String accountLabel,
            String provisioningUri
    ) {
        return new MfaEnrollmentResponse(
                factorId,
                IdentityMfaFactorType.TOTP.name(),
                secret,
                issuer,
                accountLabel,
                provisioningUri
        );
    }
}

