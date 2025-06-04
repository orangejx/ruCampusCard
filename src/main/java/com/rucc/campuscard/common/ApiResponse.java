package com.rucc.campuscard.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 统一API响应结果
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    /**
     * HTTP状态码
     */
    private Integer code;

    /**
     * 接口返回状态的具体提示
     */
    private String msg;

    /**
     * 具体数据，可以是数组[]或对象{}
     */
    private T data;

    /**
     * 成功返回结果（带数据）
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>()
                .setCode(200)
                .setMsg("success")
                .setData(data);
    }

    /**
     * 成功返回结果（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * 失败返回结果
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<T>()
                .setCode(code)
                .setMsg(message)
                .setData(null);
    }

    /**
     * 参数错误返回结果
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return error(400, message);
    }

    /**
     * 资源未找到返回结果
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return error(404, message);
    }

    /**
     * 服务器错误返回结果
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return error(500, message);
    }
}
