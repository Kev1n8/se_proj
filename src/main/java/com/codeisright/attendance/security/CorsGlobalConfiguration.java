package com.codeisright.attendance.security;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsGlobalConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 这里可以调整为需要的路径模式
                .allowedOrigins("http://localhost:8080")  // 允许的来源，可根据需要调整
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // 允许的 HTTP 方法
                .allowedHeaders("*")  // 允许的头信息
                .allowCredentials(true)  // 是否允许发送 Cookie
                .maxAge(3600);  // 预检请求的缓存时间（秒）
    }
}
