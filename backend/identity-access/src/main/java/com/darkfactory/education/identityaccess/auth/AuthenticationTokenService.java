package com.darkfactory.education.identityaccess.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
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
    private static final String TOKEN_USE_CLAIM = "tokenUse";
    private static final String CHALLENGE_PURPOSE_CLAIM = "challengePurpose";
    private static final String ACCESS_TOKEN_USE = "ACCESS";
    private static final String MFA_CHALLENGE_TOKEN_USE = "MFA_CHALLENGE";

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
        Duration resolvedTokenTtl = normalizePositiveDuration(tokenTtl, "Access-token TTL");
        OffsetDateTime expiresAt = issuedAt.plus(resolvedTokenTtl);
        String token = tokenBuilder(principal, issuedAt, expiresAt)
                .claim(TOKEN_USE_CLAIM, ACCESS_TOKEN_USE)
                .signWith(signingKey())
                .compact();

        return new IssuedAccessToken(token, expiresAt);
    }

    public IssuedMfaChallengeToken issueMfaChallengeToken(
            AuthenticatedUserPrincipal principal,
            MfaChallengePurpose purpose
    ) {
        OffsetDateTime issuedAt = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime expiresAt = issuedAt.plus(authProperties.requirePositiveMfaChallengeTtl());
        String token = tokenBuilder(principal, issuedAt, expiresAt)
                .claim(TOKEN_USE_CLAIM, MFA_CHALLENGE_TOKEN_USE)
                .claim(CHALLENGE_PURPOSE_CLAIM, purpose.name())
                .signWith(signingKey())
                .compact();

        return new IssuedMfaChallengeToken(token, expiresAt, purpose);
    }

    public AuthenticatedUserPrincipal parseAccessToken(String token) {
        Claims claims = parser().parseSignedClaims(token).getPayload();
        requireTokenUse(claims, ACCESS_TOKEN_USE);
        return principalFromClaims(claims);
    }

    public ParsedMfaChallengeToken parseMfaChallengeToken(String token) {
        Claims claims = parser().parseSignedClaims(token).getPayload();
        requireTokenUse(claims, MFA_CHALLENGE_TOKEN_USE);

        String challengePurpose = claims.get(CHALLENGE_PURPOSE_CLAIM, String.class);
        if (!StringUtils.hasText(challengePurpose)) {
            throw new IllegalArgumentException("JWT is missing MFA challenge purpose.");
        }

        return new ParsedMfaChallengeToken(
                principalFromClaims(claims),
                MfaChallengePurpose.parse(challengePurpose),
                claims.getExpiration().toInstant().atOffset(ZoneOffset.UTC)
        );
    }

    private AuthenticatedUserPrincipal principalFromClaims(Claims claims) {
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

    private void requireTokenUse(Claims claims, String expectedTokenUse) {
        String tokenUse = claims.get(TOKEN_USE_CLAIM, String.class);
        if (!StringUtils.hasText(tokenUse)) {
            if (ACCESS_TOKEN_USE.equals(expectedTokenUse)) {
                return;
            }
            throw new IllegalArgumentException("JWT is missing token-use metadata.");
        }
        if (!expectedTokenUse.equals(tokenUse)) {
            throw new IllegalArgumentException("JWT token use is not valid for this operation.");
        }
    }

    private JwtBuilder tokenBuilder(
            AuthenticatedUserPrincipal principal,
            OffsetDateTime issuedAt,
            OffsetDateTime expiresAt
    ) {
        return Jwts.builder()
                .id(principal.userId().toString())
                .subject(principal.username())
                .issuedAt(Date.from(issuedAt.toInstant()))
                .expiration(Date.from(expiresAt.toInstant()))
                .claim(TENANT_ID_CLAIM, principal.tenantId().toString())
                .claim(AUTHORITY_CLAIM, principal.authority().name())
                .claim(DISPLAY_NAME_CLAIM, principal.displayName());
    }

    private Duration normalizePositiveDuration(Duration value, String description) {
        if (value == null || value.isZero() || value.isNegative()) {
            throw new IllegalArgumentException(description + " must be a positive duration.");
        }
        return value;
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

