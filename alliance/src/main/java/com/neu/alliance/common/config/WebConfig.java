package com.neu.alliance.common.config;

import com.neu.alliance.common.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

<<<<<<< HEAD
=======
    @Value("${upload.image-path}")
    private String imagePath;

    @Value("${upload.video-path}")
    private String videoPath;

    @Value("${upload.static-path}")
    private String staticPathPrefix;
>>>>>>> main
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
<<<<<<< HEAD
                .excludePathPatterns(
                        "/auth/company/register",
                        "/auth/login",
                        "/auth/checkCode",
                        "/files/**",       // 放行所有 /files 下的请求，包括静态资源
                        "/news/upload/**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源映射
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:/Users/zqz/local/alliance/uploads/");
=======
                .excludePathPatterns("/auth/company/register","/auth/login",
            "/auth/checkCode","/files/download/**", "/files/upload");
}
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);

        // 图片资源映射
        registry.addResourceHandler("/static/image/**")
                .addResourceLocations("file:E:/uploads/images/");
        // 图片资源映射
        registry.addResourceHandler("/static/video/**")
                .addResourceLocations("file:E:/uploads/videos/");
    }

    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 允许所有源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 关键：必须加 OPTIONS
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 设置单个文件最大大小
        factory.setMaxFileSize(DataSize.ofMegabytes(100));
        // 设置总请求最大大小
        factory.setMaxRequestSize(DataSize.ofMegabytes(100));
        return factory.createMultipartConfig();
>>>>>>> main
    }
}