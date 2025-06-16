package com.neu.alliance.common.interceptor;

import com.neu.alliance.common.utils.JWTUtils;
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
        String jwt = request.getHeader("user_token");
        Claims claims = JWTUtils.parseJWT(jwt);
        if(claims == null){
            response.setStatus(401);
            log.info("令牌解析失败");
            return false;
        }
        Integer id = (Integer) claims.getOrDefault("id",null);
        if(id == null || id != Integer.parseInt(request.getHeader("user_id"))){
            response.setStatus(401);
            log.info("用户id不一致");
            return false;
        }
        return true;
    }
}
