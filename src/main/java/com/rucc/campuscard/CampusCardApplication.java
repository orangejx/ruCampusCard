package com.rucc.campuscard;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "校园卡管理系统",
        version = "1.0.0",
        description = "校园卡管理系统的REST API文档"
    )
)
public class CampusCardApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusCardApplication.class, args);
    }
}