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
                    .allowedOriginPatterns(
                        "http://localhost:5173",
                        "http://127.0.0.1:5173",
                        "http://127.0.0.1:8080",
                        "http://*.wlms.dev",     // 允许wlms.dev的所有子域名
                        "http://172.17.*.*",     // 允许172.17.0.0/16网段
                        "http://172.18.*.*",     // 允许172.18.0.0/16网段
                        "http://172.19.*.*",     // 允许172.19.0.0/16网段
                        "https://*.wlms.dev"     // 允许wlms.dev的所有子域名(HTTPS)
                    ) // 允许的源模式
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
