package com.rucc.campuscard.controller;

import com.rucc.campuscard.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        return ApiResponse.success("pong");
    }

    @GetMapping("/time")
    public ApiResponse<Long> getServerTime() {
        return ApiResponse.success(System.currentTimeMillis());
    }
}
