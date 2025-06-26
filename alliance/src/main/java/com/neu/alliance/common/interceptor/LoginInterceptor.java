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

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        // ✅ 放行静态资源路径
        if (uri.startsWith("/static/")) {
            return true;
        }

        // ✅ 放行接口：课程图片上传、视频上传、添加课程
        if (uri.contains("/course/uploadImage") ||
                uri.contains("/course/uploadVideo") ||
                uri.contains("/addcourse")) {
            return true;
        }

        try {
            // 1. 获取 Authorization 头部的 token，格式为 "Bearer xxx.xxx.xxx"
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(401);
                log.warn("未携带有效的 Authorization 头部");
                return false;
            }
            String token = authHeader.substring(7); // 去除 "Bearer "

            // 2. 解析 JWT
            Claims claims = JWTUtils.parseJWT(token);
            if (claims == null) {
                response.setStatus(401);
                log.warn("JWT 解析失败");
                return false;
            }

            // 3. 可选校验：校验 token 中的用户 ID 与请求头一致（如果你有这需求）
            String userIdHeader = request.getHeader("user_id");
            Object idObj = claims.get("id");

            if (userIdHeader != null && idObj != null) {
                int tokenUserId = (int) claims.get("id");
                if (tokenUserId != Integer.parseInt(userIdHeader)) {
                    response.setStatus(401);
                    log.warn("用户ID不一致，token中的ID={}, header中的ID={}", tokenUserId, userIdHeader);
                    return false;
                }
            }

            // 4. ✅ 将用户信息存入 request 属性，供 @RequestAttribute("user") 使用
            User user = new User();
            user.setId((int) ((Number) claims.get("id")).longValue());
            user.setNickname((String) claims.get("nickname"));
            user.setRole((Integer) claims.get("role")); // 确保 JWT 里有 role 字段
            user.setUsername((String) claims.get("username"));
            request.setAttribute("user", user);          // ✅ 供 @RequestAttribute("user") 注入使用
            request.setAttribute("claims", claims);      // （可选）供其他用途使用

            return true;

        } catch (Exception e) {
            log.error("JWT 校验失败", e);
            response.setStatus(401);
            return false;
        }

    }
}
