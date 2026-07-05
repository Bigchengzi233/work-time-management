package com.worktime.exception;

import com.worktime.common.ApiResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 全局异常处理器：把后端异常统一转换成 ApiResponse 返回给前端。
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理我们自己主动抛出的业务异常。
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException exception) {
        return ApiResponse.fail(exception.getCode(), exception.getMessage());
    }

    // 处理参数校验异常，例如 @NotBlank、@NotNull 校验失败。
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().isEmpty()
                ? "参数校验失败"
                : exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ApiResponse.fail(400, message);
    }

    // 处理请求体缺失或 JSON 格式错误，例如 POST 登录时没有填写 Body。
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return ApiResponse.fail(400, "请求体不能为空或JSON格式不正确");
    }

    // 处理其他未预料到的异常，避免把数据库错误、代码堆栈直接暴露给前端。
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception exception) {
        // 开发阶段先把真实异常打印到 IDEA 控制台，方便定位接口测试时的 500 问题。
        exception.printStackTrace();
        return ApiResponse.fail(500, "系统异常，请稍后再试");
    }
}
