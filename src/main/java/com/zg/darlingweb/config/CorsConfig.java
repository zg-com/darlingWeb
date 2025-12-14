package com.zg.darlingweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置类
 * 作用：允许前端（浏览器）访问我们的后端接口
 */
@Configuration // 告诉 Spring Boot：这是一个配置类，启动时要加载我
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 1. 允许所有的接口被访问
                .allowedOriginPatterns("*") // 2. 允许所有的来源（不管是 file:// 还是 http://www.你的域名.com）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 3. 允许的动作
                .allowCredentials(true) // 4. 允许携带 Cookie
                .maxAge(3600);
    }
}