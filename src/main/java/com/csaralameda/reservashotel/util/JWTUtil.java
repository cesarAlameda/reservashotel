package com.csaralameda.reservashotel.util;



import com.csaralameda.reservashotel.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.List;

public class JWTUtil {

    private static final long EXPIRATION_TIME = 86400000;

    public static String generateToken(String username, List<String> roles, String secret) {
        return Jwts.builder()
                .setSubject(username)
                .claim("authorities", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }
}
