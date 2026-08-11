package com.iiotedge.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class StatelessJwtFilter extends OncePerRequestFilter {

    private final String jwtSecret;

    /** This service's own name (spring.application.name) - the expected `aud` on internal-service tokens. */
    private final String expectedAudience;

    private SecretKey getSigningKey() {
        // jwt.secret is Base64-encoded (matches how auth-service's JwtTokenProvider
        // signs tokens via Keys.hmacShaKeyFor(Decoders.BASE64.decode(...))) - using
        // the raw UTF-8 bytes of the Base64 string here would derive a different key
        // and every token would fail signature verification.
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            // JJWT 0.12.x fluent API - parserBuilder()/parseClaimsJws()/getBody()
            // were removed (not just deprecated), replaced by parser()/
            // parseSignedClaims()/getPayload().
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            String tenantId = claims.get("tenantId", String.class);

            // auth-service's JwtTokenProvider puts roles under claim key "role" (singular);
            // fall back to "roles" in case a future issuer uses the plural form.
            Object rolesClaim = claims.get("role") != null ? claims.get("role") : claims.get("roles");
            List<String> roles = rolesClaim instanceof List<?> rawRoles
                    ? rawRoles.stream().map(String::valueOf).collect(Collectors.toList())
                    : List.of();

            List<SimpleGrantedAuthority> authorities = new ArrayList<>(roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));

            // Internal service-to-service tokens (scope=internal) only count if the
            // audience matches THIS service - otherwise a token minted for another
            // downstream service could be replayed here.
            String scope = claims.get("scope", String.class);
            String audience = claims.get("aud", String.class);
            if ("internal".equals(scope) && expectedAudience != null && expectedAudience.equals(audience)) {
                authorities.add(new SimpleGrantedAuthority("SCOPE_INTERNAL"));
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, authorities
                );

                // CRITICAL: Store the tenantId in the details so the Evaluator can use it
                authToken.setDetails(tenantId);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            log.warn("Invalid JWT Token in Microservice: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}