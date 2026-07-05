package com.worktime.common;

// 统一接口返回结果。
// 以后所有 Controller 都尽量返回这个对象，让前端拿到的数据格式保持一致。
public class ApiResponse<T> {

    // code 表示业务状态码，200 表示成功，其他值表示失败。
    private Integer code;

    // message 表示提示信息，成功或失败时都可以给前端展示。
    private String message;

    // data 表示真正返回给前端的数据，类型用泛型 T 保持灵活。
    private T data;

    // 无参构造方法，给 Spring 和 JSON 序列化工具使用。
    public ApiResponse() {
    }

    // 全参构造方法，方便在静态方法里快速创建返回对象。
    public ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功但不需要返回数据时使用。
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "操作成功", null);
    }

    // 成功且需要返回数据时使用。
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data);
    }

    // 业务失败时使用，例如参数错误、权限不足、数据不存在。
    public static <T> ApiResponse<T> fail(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
