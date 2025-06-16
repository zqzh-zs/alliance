package com.neu.alliance.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class JWTUtils {
    private static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final Key key = Keys.hmacShaKeyFor(secretKey.getEncoded());
    private static final long expireTime = 1000 * 60 * 60 * 24;
    public static String getJWT(Map<String, Object> claim) {
        String token = Jwts.builder()
                .setClaims(claim)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(key)
                .compact();
        return token;
    }

    public static Claims parseJWT(String token){
        if(!StringUtils.hasLength(token)){
            return null;
        }

        JwtParser builder = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
        Claims claims = null;
        try{
            claims = builder.parseClaimsJws(token).getBody();
        }catch(ExpiredJwtException e){
            log.error("token已过期, token:{}",token);
        }catch (SignatureException e){
            log.error("token签名错误, token:{}",token);
        } catch (Exception e){
            log.error("token解析失败, token:{}",token);
        }
        return claims;
    }

    public static Integer getUserIdFromToken(String jwtToken){
        Claims claims = parseJWT(jwtToken);
        if(claims != null){
            Map<String,Object> userInfo = new HashMap<>(claims);
            return (Integer) userInfo.get("id");
        }
        return null;
    }
}
