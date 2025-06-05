package com.rucc.campuscard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * Web配置类 - 仅处理路径匹配配置
 * 注意：资源处理已移至WebMvcConfig类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 使用PathPatternParser替代已弃用的setUseTrailingSlashMatch
        configurer.setPatternParser(new PathPatternParser());
    }
}
