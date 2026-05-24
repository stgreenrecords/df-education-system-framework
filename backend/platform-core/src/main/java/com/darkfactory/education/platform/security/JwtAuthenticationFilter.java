package com.darkfactory.education.platform.security;

import com.darkfactory.education.identityaccess.auth.AuthenticatedUserPrincipal;
import com.darkfactory.education.identityaccess.auth.AuthenticatedPrincipalRoleService;
import com.darkfactory.education.identityaccess.auth.AuthenticationTokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthenticationTokenService authenticationTokenService;
    private final AuthenticatedPrincipalRoleService authenticatedPrincipalRoleService;

    public JwtAuthenticationFilter(
            AuthenticationTokenService authenticationTokenService,
            AuthenticatedPrincipalRoleService authenticatedPrincipalRoleService
    ) {
        this.authenticationTokenService = authenticationTokenService;
        this.authenticatedPrincipalRoleService = authenticatedPrincipalRoleService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(token)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired access token");
            return;
        }

        try {
            AuthenticatedUserPrincipal principal = authenticatedPrincipalRoleService.enrich(
                    authenticationTokenService.parseAccessToken(token)
            );
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    token,
                    principal.grantedAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired access token");
        }
    }
}

