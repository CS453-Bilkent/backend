package com.bilkent.devinsight.security;

import com.bilkent.devinsight.entity.User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;

@Slf4j
@Component
public class JwtTokenUtil {

    @Value("${spring.jwt.access-secret}")
    private String accessSecretKey;

    @Value("${spring.jwt.refresh-secret}")
    private String refreshSecretKey;

    @Value("${spring.jwt.access-ttl}")
    private long accessTtl;

    @Value("${spring.jwt.refresh-ttl}")
    private long refreshTtl;

    @Value("${spring.jwt.issuer}")
    private String issuer;

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTtl))
                .signWith(SignatureAlgorithm.HS512, accessSecretKey)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTtl))
                .signWith(SignatureAlgorithm.HS512, refreshSecretKey)
                .compact();
    }



    public Date extractRefreshExpiration(String token) {
        return parseClaims(token, refreshSecretKey).getExpiration();
    }

    public String extractRefreshUsername(String token) {
        return parseClaims(token, accessSecretKey).getSubject();
    }

    public boolean isRefreshTokenExpired(String token) {
        return extractRefreshExpiration(token).before(new Date());
    }

    public Date extractAccessExpiration(String token) {
        return parseClaims(token, accessSecretKey).getExpiration();
    }

    public String extractAccessUsername(String token) {
        return parseClaims(token, accessSecretKey).getSubject();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.debug("Expired JWT Access token: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.debug("JWT Access Token is null, empty or only whitespace " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.debug("JWT Access is invalid " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.debug("JWT Access is not supported " + ex.getMessage());
        } catch (SignatureException ex) {
            log.debug("Signature validation failed for access token " + ex.getMessage());
        }

        return false;
    }


    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.debug("Expired JWT Refresh token: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.debug("JWT Refresh Token is null, empty or only whitespace " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.debug("JWT Refresh is invalid " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.debug("JWT Refresh is not supported " + ex.getMessage());
        } catch (SignatureException ex) {
            log.debug("Signature validation failed for refresh token " + ex.getMessage());
        }
        return false;
    }

    private String getSubject(String token, String key) {
        return parseClaims(token, key).getSubject();
    }

    private Claims parseClaims(String token, String key) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        return !(ObjectUtils.isEmpty(header) || !header.startsWith("Bearer"));
    }

}
