package com.neu.alliance.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class JWTUtils {
    private static final String secret = "FzG6p48J80L6vFxLrQqy2JVN27NiYbgjtGuYCTpeX7w=";
    private static final Key key = Keys.hmacShaKeyFor(secret.getBytes());
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
        if (!StringUtils.hasText(token) || token.split("\\.").length != 3) {
            log.error("token 格式错误, token: {}", token);
            return null;
        }

        JwtParser builder = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();

        try {
            return builder.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            log.error("token已过期, token: {}", token);
        } catch (SignatureException e) {
            log.error("token签名错误, token: {}", token);
        } catch (Exception e) {
            log.error("token解析失败, token: {}, 原因: {}", token, e.getMessage());
        }
        return null;
    }

    public static Integer getUserIdFromToken(String jwtToken) {
        Claims claims = parseJWT(jwtToken);
        if (claims != null) {
            Object id = claims.get("id");
            if (id instanceof Integer) {
                return (Integer) id;
            } else if (id instanceof String) {
                try {
                    return Integer.parseInt((String) id);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public static Integer getUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        log.debug("Authorization header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("请求未携带有效的 Authorization 头");
            return null;
        }
        String token = authHeader.substring(7);  // 去掉 "Bearer " 前缀
        return getUserIdFromToken(token);
    }

}
