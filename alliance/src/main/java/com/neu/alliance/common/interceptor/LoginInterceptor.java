package com.neu.alliance.common.interceptor;

import com.neu.alliance.common.utils.JWTUtils;
import com.neu.alliance.entity.User; // ✅ 添加 import
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        log.info("请求URI: {}, 请求方法: {}", uri, method);

        if ("OPTIONS".equalsIgnoreCase(method)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        if (uri.startsWith("/static/")) {
            log.info("静态资源，放行");
            return true;
        }

        if (uri.contains("/course/uploadImage") ||
                uri.contains("/course/uploadVideo") ||
                uri.contains("/addcourse") ||
                uri.contains("/meeting/upload")) {
            log.info("上传相关接口，放行");
            return true;
        }

        try {
            String authHeader = request.getHeader("Authorization");
            log.info("Authorization 头: {}", authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("未携带有效的 Authorization 头部");
                response.setStatus(401);
                return false;
            }

            String token = authHeader.substring(7);
            log.info("提取的token: {}", token);

            Claims claims = JWTUtils.parseJWT(token);
            if (claims == null) {
                log.warn("JWT 解析失败，claims为空");
                response.setStatus(401);
                return false;
            }

            log.info("JWT 解析成功，claims内容: {}", claims);

            String userIdHeader = request.getHeader("user_id");
            Object idObj = claims.get("id");

            if (userIdHeader != null && idObj != null) {
                int tokenUserId = ((Number) claims.get("id")).intValue();
                log.info("请求头 user_id: {}, token 中 id: {}", userIdHeader, tokenUserId);
                if (tokenUserId != Integer.parseInt(userIdHeader)) {
                    log.warn("用户ID不一致，token中的ID={}, header中的ID={}", tokenUserId, userIdHeader);
                    response.setStatus(401);
                    return false;
                }
            }

            User user = new User();
            user.setId(((Number) claims.get("id")).intValue());
            user.setNickname((String) claims.get("nickname"));
            user.setRole((Integer) claims.get("role"));
            user.setUsername((String) claims.get("username"));

            request.setAttribute("user", user);
            request.setAttribute("claims", claims);

            return true;

        } catch (Exception e) {
            log.error("JWT 校验失败，异常信息：", e);
            response.setStatus(401);
            return false;
        }
    }
}
