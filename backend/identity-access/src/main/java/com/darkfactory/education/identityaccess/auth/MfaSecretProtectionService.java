package com.darkfactory.education.identityaccess.auth;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Service
public class MfaSecretProtectionService {

    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private final AuthProperties authProperties;
    private final SecureRandom secureRandom = new SecureRandom();

    public MfaSecretProtectionService(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public String encrypt(String plaintextSecret) {
        if (plaintextSecret == null || plaintextSecret.isBlank()) {
            throw new IllegalArgumentException("MFA secret must not be blank.");
        }

        try {
            byte[] iv = new byte[IV_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey(), new GCMParameterSpec(TAG_BITS, iv));
            byte[] ciphertext = cipher.doFinal(plaintextSecret.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to encrypt the MFA secret.", exception);
        }
    }

    public String decrypt(String ciphertext) {
        if (ciphertext == null || ciphertext.isBlank()) {
            throw new IllegalArgumentException("Encrypted MFA secret must not be blank.");
        }

        try {
            byte[] combined = Base64.getDecoder().decode(ciphertext);
            if (combined.length <= IV_BYTES) {
                throw new IllegalArgumentException("Encrypted MFA secret is malformed.");
            }

            byte[] iv = Arrays.copyOfRange(combined, 0, IV_BYTES);
            byte[] encrypted = Arrays.copyOfRange(combined, IV_BYTES, combined.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey(), new GCMParameterSpec(TAG_BITS, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException exception) {
            throw new IllegalStateException("Unable to decrypt the MFA secret.", exception);
        }
    }

    private SecretKeySpec encryptionKey() {
        try {
            String configuredKey = authProperties.requireMfaSecretEncryptionKey();
            byte[] hashedKey = MessageDigest.getInstance("SHA-256")
                    .digest(configuredKey.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(hashedKey, "AES");
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to derive the MFA encryption key.", exception);
        }
    }
}

