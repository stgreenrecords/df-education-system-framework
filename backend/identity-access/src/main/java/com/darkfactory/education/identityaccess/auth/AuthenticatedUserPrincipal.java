package com.darkfactory.education.identityaccess.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

public record AuthenticatedUserPrincipal(
        UUID userId,
        UUID tenantId,
        String username,
        String displayName,
        IdentityUserAuthority authority
) {
    public List<? extends GrantedAuthority> grantedAuthorities() {
        return List.of(new SimpleGrantedAuthority(authority.name()));
    }
}

