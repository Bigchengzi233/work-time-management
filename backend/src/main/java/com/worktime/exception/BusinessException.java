package com.worktime.exception;

// 业务异常：用于主动抛出可预期的业务错误。
// 例如手机号重复、员工无权访问某条工时、项目已禁用等。
public class BusinessException extends RuntimeException {

    // code 用来区分不同错误类型，前端也可以根据 code 做不同提示。
    private final Integer code;

    // 创建业务异常时传入错误码和错误提示。
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
