package com.worktime.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// 修改项目授权入参对象：当前只允许修改授权状态。
public class UserProjectUpdateDTO {

    // 授权状态：0取消授权，1有效授权。
    @NotNull(message = "授权状态不能为空")
    @Min(value = 0, message = "授权状态只能是0或1")
    @Max(value = 1, message = "授权状态只能是0或1")
    private Integer authStatus;

    public Integer getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(Integer authStatus) {
        this.authStatus = authStatus;
    }
}
