package com.rucc.campuscard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class CampusCardApplication {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins(
                        "http://localhost:5173",
                        "http://127.0.0.1:5173",
                        "http://127.0.0.1:8080"
                    ) // 允许的源
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的方法
                    .allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers") // 允许的头部
                    .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials") // 暴露的响应头
                    .allowCredentials(true)
                    .maxAge(1800); // 缓存时间
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(CampusCardApplication.class, args);
    }

}
