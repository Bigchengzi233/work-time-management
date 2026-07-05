package com.worktime.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// 修改用户入参对象：接收管理员修改用户时传来的 JSON 数据。
public class UserUpdateDTO {

    // 用户姓名不能为空。
    @NotBlank(message = "用户姓名不能为空")
    @Size(max = 50, message = "用户姓名长度不能超过50个字符")
    private String userName;

    // 手机号不能为空，并限制为常见 11 位手机号格式。
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String phone;

    // 密码可为空字符串；如果填写真实密码，则长度必须是 6 到 50 个字符。
    @Pattern(regexp = "^$|^.{6,50}$", message = "密码长度应为6到50个字符")
    private String password;

    // 邮箱可以为空；如果填写则必须是邮箱格式。
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    // 用户角色：0管理员，1部门经理，2员工。
    @NotBlank(message = "用户角色不能为空")
    @Pattern(regexp = "^[012]$", message = "用户角色只能是0、1、2")
    private String userRole;

    // 所属部门编号不能为空。
    @NotNull(message = "所属部门不能为空")
    private Integer deptId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }
}
