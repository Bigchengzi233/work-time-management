package com.worktime.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// 登录入参对象：接收用户登录时传来的手机号、密码和验证码。
public class LoginDTO {

    // 手机号不能为空，并限制为常见 11 位手机号格式。
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String phone;

    // 密码不能为空。
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度应为6到50个字符")
    private String password;

    // 验证码编号不能为空，用于后端找到本次验证码答案。
    @NotBlank(message = "验证码编号不能为空")
    private String captchaId;

    // 用户输入的验证码不能为空。
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptchaId() {
        return captchaId;
    }

    public void setCaptchaId(String captchaId) {
        this.captchaId = captchaId;
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }
}
