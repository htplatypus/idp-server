package org.project.idpserver;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "my-secret-key-for-jwt-signing-and-validation";
    private final SecretKey key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256"); // Use the same secret as the IdP



    public String generateToken(Authentication authentication) {

        // extract user details from the Authentication object
        String username = authentication.getName(); // Get the authenticated username
        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // Cast to UserDetails
        String roles = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.joining(",")); // Join roles with commas

        return Jwts.builder()
                .subject(authentication.getName())
                .issuedAt(new Date())
                .claim("email", "user@example.com") // TODO: IMPLEMENT
                .claim("roles", roles)
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
