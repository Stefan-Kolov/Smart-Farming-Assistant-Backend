package com.smartfarmingassistant.sfa.helpers;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import com.smartfarmingassistant.sfa.constants.JwtConstants;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtHelper {
    private final JwtConstants jwtConstants;

    public JwtHelper(JwtConstants jwtConstants) {
        this.jwtConstants = jwtConstants;
    }

    private Key getSignIn() {
        return Keys.hmacShaKeyFor(jwtConstants.getSecretKey().getBytes());
    }
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignIn())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims allClaims = extractAllClaims(token);
        return claimsResolver.apply(allClaims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            String subject,
            Long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtConstants.getExpirationTime()))
                .signWith(getSignIn(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", userDetails.getAuthorities());
        return buildToken(extraClaims, userDetails.getUsername(), jwtConstants.getExpirationTime());
    }

    public boolean isExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}

