package com.darkfactory.education.identityaccess.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthenticationTokenService {

    private static final String TENANT_ID_CLAIM = "tenantId";
    private static final String DISPLAY_NAME_CLAIM = "displayName";
    private static final String AUTHORITY_CLAIM = "authority";

    private final AuthProperties authProperties;

    public AuthenticationTokenService(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public IssuedAccessToken issueAccessToken(AuthenticatedUserPrincipal principal) {
        return issueAccessToken(principal, OffsetDateTime.now(ZoneOffset.UTC), authProperties.requirePositiveTokenTtl());
    }

    public IssuedAccessToken issueAccessToken(
            AuthenticatedUserPrincipal principal,
            OffsetDateTime issuedAt,
            Duration tokenTtl
    ) {
        OffsetDateTime expiresAt = issuedAt.plus(tokenTtl);
        String token = Jwts.builder()
                .id(principal.userId().toString())
                .subject(principal.username())
                .issuedAt(Date.from(issuedAt.toInstant()))
                .expiration(Date.from(expiresAt.toInstant()))
                .claim(TENANT_ID_CLAIM, principal.tenantId().toString())
                .claim(AUTHORITY_CLAIM, principal.authority().name())
                .claim(DISPLAY_NAME_CLAIM, principal.displayName())
                .signWith(signingKey())
                .compact();

        return new IssuedAccessToken(token, expiresAt);
    }

    public AuthenticatedUserPrincipal parseAccessToken(String token) {
        Claims claims = parser().parseSignedClaims(token).getPayload();

        String username = claims.getSubject();
        String authority = claims.get(AUTHORITY_CLAIM, String.class);
        String tenantId = claims.get(TENANT_ID_CLAIM, String.class);
        String displayName = claims.get(DISPLAY_NAME_CLAIM, String.class);
        String userId = claims.getId();

        if (!StringUtils.hasText(username)
                || !StringUtils.hasText(authority)
                || !StringUtils.hasText(tenantId)
                || !StringUtils.hasText(userId)) {
            throw new IllegalArgumentException("JWT is missing one or more required identity claims.");
        }

        return new AuthenticatedUserPrincipal(
                UUID.fromString(userId),
                UUID.fromString(tenantId),
                username,
                displayName,
                IdentityUserAuthority.valueOf(authority)
        );
    }

    private JwtParser parser() {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build();
    }

    private SecretKey signingKey() {
        String configuredSecret = authProperties.requireJwtSecret();
        if (looksLikeBase64(configuredSecret)) {
            try {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(configuredSecret));
            } catch (IllegalArgumentException ignored) {
                // fall through to plain UTF-8 secret handling
            }
        }
        return Keys.hmacShaKeyFor(configuredSecret.getBytes(StandardCharsets.UTF_8));
    }

    private boolean looksLikeBase64(String value) {
        return value.length() % 4 == 0 && value.matches("^[A-Za-z0-9+/=]+$");
    }
}

