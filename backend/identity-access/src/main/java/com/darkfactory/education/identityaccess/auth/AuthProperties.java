package com.darkfactory.education.identityaccess.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.time.Duration;

@ConfigurationProperties(prefix = "edu.auth")
public class AuthProperties {

    private static final Duration DEFAULT_TOKEN_TTL = Duration.ofMinutes(15);

    private String jwtSecret;
    private Duration tokenTtl = DEFAULT_TOKEN_TTL;
    private String bootstrapAdminUsername;
    private String bootstrapAdminPassword;
    private String bootstrapAdminDisplayName = "Bootstrap Administrator";

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public Duration getTokenTtl() {
        return tokenTtl;
    }

    public void setTokenTtl(Duration tokenTtl) {
        this.tokenTtl = tokenTtl;
    }

    public String getBootstrapAdminUsername() {
        return bootstrapAdminUsername;
    }

    public void setBootstrapAdminUsername(String bootstrapAdminUsername) {
        this.bootstrapAdminUsername = bootstrapAdminUsername;
    }

    public String getBootstrapAdminPassword() {
        return bootstrapAdminPassword;
    }

    public void setBootstrapAdminPassword(String bootstrapAdminPassword) {
        this.bootstrapAdminPassword = bootstrapAdminPassword;
    }

    public String getBootstrapAdminDisplayName() {
        return bootstrapAdminDisplayName;
    }

    public void setBootstrapAdminDisplayName(String bootstrapAdminDisplayName) {
        this.bootstrapAdminDisplayName = bootstrapAdminDisplayName;
    }

    public String requireJwtSecret() {
        if (!StringUtils.hasText(jwtSecret)) {
            throw new IllegalStateException("Required property 'edu.auth.jwt-secret' must not be blank.");
        }
        String normalizedSecret = jwtSecret.trim();
        if (normalizedSecret.length() < 32) {
            throw new IllegalStateException("Property 'edu.auth.jwt-secret' must be at least 32 characters for HMAC signing.");
        }
        return normalizedSecret;
    }

    public Duration requirePositiveTokenTtl() {
        Duration resolved = tokenTtl == null ? DEFAULT_TOKEN_TTL : tokenTtl;
        if (resolved.isZero() || resolved.isNegative()) {
            throw new IllegalStateException("Property 'edu.auth.token-ttl' must be a positive duration.");
        }
        return resolved;
    }

    public BootstrapAdminConfiguration bootstrapAdminConfigurationOrNull() {
        boolean usernameConfigured = StringUtils.hasText(bootstrapAdminUsername);
        boolean passwordConfigured = StringUtils.hasText(bootstrapAdminPassword);

        if (!usernameConfigured && !passwordConfigured) {
            return null;
        }
        if (!usernameConfigured || !passwordConfigured) {
            throw new IllegalStateException(
                    "Properties 'edu.auth.bootstrap-admin-username' and 'edu.auth.bootstrap-admin-password' must both be configured together."
            );
        }

        String displayName = StringUtils.hasText(bootstrapAdminDisplayName)
                ? bootstrapAdminDisplayName.trim()
                : "Bootstrap Administrator";

        return new BootstrapAdminConfiguration(
                bootstrapAdminUsername.trim(),
                bootstrapAdminPassword,
                displayName
        );
    }

    public record BootstrapAdminConfiguration(
            String username,
            String password,
            String displayName
    ) {
    }
}

