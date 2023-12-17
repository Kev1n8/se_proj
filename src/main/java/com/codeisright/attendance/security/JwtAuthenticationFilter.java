package com.codeisright.attendance.security;

import com.codeisright.attendance.service.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final String secretKey;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(String secretKey, UserDetailsServiceImpl userDetailsService) {
        this.secretKey = secretKey;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        logger.debug("Request Header parsing..." + request.getHeaderNames());
        String header = request.getHeader("Authorization");
        logger.info("Header: " + header);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                logger.info("Start parsing...");
                Claims claims = Jwts.parser()
                        .setSigningKey(secretKey.getBytes())
                        .parseClaimsJws(token)
                        .getBody();
                Instant expirationTime = claims.getExpiration().toInstant();
                if (expirationTime.isBefore(Instant.now())) {
                    throw new ExpiredJwtException(null, claims, "JWT has expired.");
                }
                String username = claims.getSubject();
                logger.info("username: " + username);

                String expectedToken = userDetailsService.getJwt(username);
                if (!token.equals(expectedToken)) {
                    throw new Exception("Invalid token");
                }

                // Authorization
                Collection<? extends GrantedAuthority> authorities = userDetailsService.getAuthoritiesById(username);

                logger.info("authorities giving: " + authorities);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null,
                        authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.debug("authentication success");
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                logger.error("authentication failed " + e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}
