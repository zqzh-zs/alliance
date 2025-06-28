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

    @Value("${upload.image-path}")
    private String imagePath;

    @Value("${upload.video-path}")
    private String videoPath;

    @Value("${upload.static-path}")
    private String staticPathPrefix;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/company/register",
                        "/auth/login",
                        "/auth/checkCode",
                        "/files/**",
                        "/uploads/**",
                        "/news/upload/**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + staticPathPrefix);
        registry.addResourceHandler("/static/image/**")
                .addResourceLocations("file:" + imagePath);
        registry.addResourceHandler("/static/video/**")
                .addResourceLocations("file:" + videoPath);
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:/Users/zqz/local/alliance/uploads/");//调试
        // 映射/uploads/** 到本地 uploads 文件夹
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");

    }

    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(100));
        factory.setMaxRequestSize(DataSize.ofMegabytes(100));
        return factory.createMultipartConfig();
    }
}