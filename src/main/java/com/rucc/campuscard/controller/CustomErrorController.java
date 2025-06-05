package com.rucc.campuscard.controller;

import com.rucc.campuscard.common.ApiResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ApiResponse<Void>> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            String errorMessage = message != null ? message.toString() : getDefaultErrorMessage(statusCode);

            return switch (statusCode) {
                case 404 -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "请求的资源不存在"));

                case 403 -> ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "没有权限访问该资源"));

                case 401 -> ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "未经授权的访问"));

                case 400 -> ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "请求参数错误"));

                case 405 -> ResponseEntity
                        .status(HttpStatus.METHOD_NOT_ALLOWED)
                        .body(ApiResponse.error(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持的请求方法"));

                case 415 -> ResponseEntity
                        .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body(ApiResponse.error(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "不支持的媒体类型"));

                case 429 -> ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(ApiResponse.error(HttpStatus.TOO_MANY_REQUESTS.value(), "请求过于频繁，请稍后再试"));

                default -> ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            statusCode >= 500 ? "服务器内部错误" : errorMessage));
            };
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "未知错误"));
    }

    private String getDefaultErrorMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> "请求参数错误";
            case 401 -> "未经授权的访问";
            case 403 -> "没有权限访问该资源";
            case 404 -> "请求的资源不存在";
            case 405 -> "不支持的请求方法";
            case 415 -> "不支持的媒体类型";
            case 429 -> "请求过于频繁，请稍后再试";
            default -> statusCode >= 500 ? "服务器内部错误" : "请求处理失败";
        };
    }
}
