package com.darkfactory.education.identityaccess.auth;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class TotpService {

    private static final int SECRET_BYTES = 20;
    private static final int TIME_STEP_SECONDS = 30;
    private static final int CODE_DIGITS = 6;
    private static final int MODULUS = 1_000_000;
    private static final String HMAC_ALGORITHM = "HmacSHA1";

    private final AuthProperties authProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public TotpService(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public String generateSecret() {
        byte[] secret = new byte[SECRET_BYTES];
        secureRandom.nextBytes(secret);
        return Base32Codec.encode(secret);
    }

    public boolean verifyCode(String base32Secret, String code) {
        String normalizedCode = normalizeCode(code);
        long currentCounter = counterFor(OffsetDateTime.now(ZoneOffset.UTC));
        int window = authProperties.requireNonNegativeMfaTotpCodeWindow();

        for (long counter = currentCounter - window; counter <= currentCounter + window; counter++) {
            if (counter < 0) {
                continue;
            }
            if (generateCode(base32Secret, counter).equals(normalizedCode)) {
                return true;
            }
        }

        return false;
    }

    public String currentCode(String base32Secret) {
        return generateCode(base32Secret, counterFor(OffsetDateTime.now(ZoneOffset.UTC)));
    }

    public String provisioningUri(String issuer, String accountLabel, String base32Secret) {
        String encodedIssuer = URLEncoder.encode(issuer, StandardCharsets.UTF_8);
        String encodedAccountLabel = URLEncoder.encode(accountLabel, StandardCharsets.UTF_8);
        return "otpauth://totp/" + encodedIssuer + ":" + encodedAccountLabel
                + "?secret=" + base32Secret
                + "&issuer=" + encodedIssuer
                + "&algorithm=SHA1&digits=" + CODE_DIGITS
                + "&period=" + TIME_STEP_SECONDS;
    }

    private String generateCode(String base32Secret, long counter) {
        try {
            byte[] secret = Base32Codec.decode(base32Secret);
            byte[] counterBytes = ByteBuffer.allocate(8).putLong(counter).array();

            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            byte[] hash = mac.doFinal(counterBytes);

            int offset = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);

            int otp = binary % MODULUS;
            return String.format("%0" + CODE_DIGITS + "d", otp);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to generate a TOTP code.", exception);
        }
    }

    private long counterFor(OffsetDateTime timestamp) {
        return Instant.from(timestamp).getEpochSecond() / TIME_STEP_SECONDS;
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("TOTP code is required.");
        }

        String normalized = code.trim();
        if (!normalized.matches("\\d{6}")) {
            throw new IllegalArgumentException("TOTP code must be a six-digit numeric value.");
        }
        return normalized;
    }
}


