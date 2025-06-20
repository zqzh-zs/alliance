package com.neu.alliance.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 指定允许访问的前端地址（不能是 *，否则带 Authorization 的请求会失败）
        config.setAllowedOriginPatterns(Collections.singletonList("http://localhost:5173"));
        config.setAllowCredentials(true); // 允许发送 cookie / token
        config.addAllowedHeader("*"); // 允许所有请求头（包括 Authorization）
        config.addAllowedMethod("*"); // 允许所有方法：GET POST PUT DELETE ...
        config.setMaxAge(3600L); // 预检请求缓存时间
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 对所有接口生效
        return new CorsFilter(source);
    }
}
