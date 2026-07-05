package com.worktime.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 个人信息修改入参对象：接收当前登录用户修改本人资料时传来的 JSON 数据。
public class ProfileUpdateDTO {

    // 用户姓名不能为空。
    @NotBlank(message = "用户姓名不能为空")
    @Size(max = 50, message = "用户姓名长度不能超过50个字符")
    private String userName;

    // 邮箱可以为空；如果填写则必须是邮箱格式。
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
