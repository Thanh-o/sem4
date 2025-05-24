package com.example.appointmentservices.security;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;



import javax.crypto.SecretKey;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        logger.info("Authorization Header: "+ requestTokenHeader);

        String username = null;
        String jwtToken = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            logger.info("JWT Token: "+ jwtToken);
            try {
                username = extractUsername(jwtToken);
                logger.info("Extracted Username: "+ username);
            } catch (ExpiredJwtException e) {
                logger.warn("JWT Token has expired: "+ e.getMessage());
            } catch (JwtException e) {
                logger.error("Invalid JWT token: "+ e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Invalid JWT token\", \"message\": \"" + e.getMessage() + "\"}");
                return;
            }
        } else {
            logger.debug("JWT Token does not begin with Bearer String");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, null);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            logger.info("Authentication set for user: "+ username);
        } else {
            logger.debug("No valid authentication set. Username:  " +
                    username);
        }

        chain.doFilter(request, response);
    }

    private String extractUsername(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            return io.jsonwebtoken.Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            throw new JwtException("Invalid JWT token: " + e.getMessage());
        }
    }
}