package com.rucc.campuscard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        
        // 将根路径映射到index.html
        registry.addViewController("/").setViewName("forward:/index.html");
        
        // 明确列出需要转发到前端路由的路径
        String[] spaRoutes = {
            "/create",
            "/query",
            "/recharge",
            "/consume",
            "/delete"
        };
        
        for (String route : spaRoutes) {
            registry.addViewController(route).setViewName("forward:/index.html");
            // 处理子路径
            registry.addViewController(route + "/**").setViewName("forward:/index.html");
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Swagger UI资源映射
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/")
                .resourceChain(false);

        // API文档端点
        registry.addResourceHandler("/v3/api-docs/**")
                .addResourceLocations("classpath:/META-INF/resources/v3/api-docs/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .resourceChain(false);

        // 静态资源映射 - 需要放在最后，避免覆盖Swagger UI的路径
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                    "http://localhost:[*]",
                    "http://127.0.0.1:[*]",
                    "http://*.wlms.dev",
                    "https://*.wlms.dev"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization", "X-Requested-With", 
                              "accept", "Origin", "Access-Control-Request-Method", 
                              "Access-Control-Request-Headers")
                .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
                .allowCredentials(true)
                .maxAge(3600);
    }
}