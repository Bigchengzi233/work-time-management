package com.worktime.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 密码修改入参对象：接收当前登录用户修改密码时传来的 JSON 数据。
public class PasswordUpdateDTO {

    // 原密码不能为空。
    @NotBlank(message = "原密码不能为空")
    @Size(min = 6, max = 50, message = "原密码长度应为6到50个字符")
    private String oldPassword;

    // 新密码不能为空。
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 50, message = "新密码长度应为6到50个字符")
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
