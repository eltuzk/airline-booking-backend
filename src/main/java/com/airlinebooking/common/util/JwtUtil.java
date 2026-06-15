package com.airlinebooking.common.util;

import com.airlinebooking.auth.dto.response.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    @Value("${jwt.private-access-key}")
    private String accessKey;
    @Value("${jwt.private-refresh-key}")
    private String refreshKey;
    private final int expriedAccessTime = 15 * 60 * 1000;
    private final int expriedRefreshTime = 7 * 24 * 60 * 60 * 1000;
    public String generateAccessToken(UserDTO user) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
        Date currentDate = new Date();
        Date futureDate =  new Date(currentDate.getTime() + expriedAccessTime);
        return Jwts.builder().subject(String.valueOf(user.getUserId())).claim("email", user.getEmail()).expiration(futureDate).signWith(key).compact();
    }
    public String generateRefreshToken(UserDTO user) {
        SecretKey key  = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));
        String jti = UUID.randomUUID().toString();
        Date currentDate = new Date();
        Date futureDate =  new Date(currentDate.getTime() + expriedRefreshTime);
        return Jwts.builder().subject(String.valueOf(user.getUserId())).id(jti).expiration(futureDate).signWith(key).compact();
    }
    public String decodeToken(String token){
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }
    public Claims parseToken(String token){
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKey));

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public Claims parseAccessToken(String token){
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
