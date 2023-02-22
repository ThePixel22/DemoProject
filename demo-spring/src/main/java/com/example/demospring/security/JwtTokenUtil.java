package com.example.demospring.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenUtil{

    private static final long serialVersionUID = -2550185165626007488L;

    private static final String AUTHORITIES_KEY = "auth";

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.JWT_TOKEN_VALIDITY*100000);
        } else {
            validity = new Date(now + 20*1000);
        }

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(validity)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        //JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secret).build(); //version 0.11.5
        //final Claims claims = jwtParser.parseClaimsJws(token).getBody();
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJwt(token).getBody();
        return claims.getSubject();
    }

    public boolean validateJwtToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJwt(token).getBody();
        Boolean isTokenExpired = claims.getExpiration().before(new Date());
        return (username.equals(userDetails.getUsername()) && !isTokenExpired);
    }
}
