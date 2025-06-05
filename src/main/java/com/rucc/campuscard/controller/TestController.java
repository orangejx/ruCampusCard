package com.rucc.campuscard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Tag(name = "测试接口", description = "用于测试Swagger集成")
public class TestController {

    @GetMapping("/hello")
    @Operation(summary = "测试问候接口", description = "返回简单的问候语")
    public String hello() {
        return "Hello, Swagger!";
    }
}
