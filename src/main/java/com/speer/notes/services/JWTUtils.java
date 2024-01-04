package com.speer.notes.services;

import com.speer.notes.models.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTUtils {
    Logger logger = LoggerFactory.getLogger(JWTUtils.class);

    private final long jwtTokenValidity;

    private final String secretKey;

    public JWTUtils(@Value("${JWTConfig.EXPIRY}") long jwtTokenValidity, @Value("${JWTConfig.SECRET}") String secretKey) {
        this.jwtTokenValidity = jwtTokenValidity;
        this.secretKey = secretKey;
    }

    public String generateToken(UserEntity user) {
        Claims claims = Jwts.claims().setSubject(user.getUsername()).build();
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + jwtTokenValidity);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).build().parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }

    public String[] authorizeToken(HttpHeaders httpHeaders) {
        String[] response = new String[2];
        response[0] = "false";
        try {
            String token = httpHeaders.get("authorization").get(0).substring(7);
            String username = getUsernameFromToken(token);
            response[0] = "true";
            response[1] = username;
        } catch (NullPointerException e) {
            response[1] = "Authorization token not provided";
        } catch (SignatureException e) {
            response[1] = "Authorization Token provided is not valid";
        } catch (ExpiredJwtException e) {
            response[1] = "Token expired, please generate new one";
        } catch (Exception e) {
            response[1] = "Something Went wrong";
        }
        return response;
    }
}
