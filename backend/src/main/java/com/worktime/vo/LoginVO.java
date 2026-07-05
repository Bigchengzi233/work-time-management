package com.worktime.vo;

// 登录返回对象：返回给前端保存的登录用户信息。
public class LoginVO {

    // 登录令牌；后续请求需要放到 Authorization 请求头中。
    private String token;

    // 用户编号。
    private Integer userId;

    // 用户姓名。
    private String userName;

    // 手机号。
    private String phone;

    // 用户角色：0管理员，1部门经理，2员工。
    private String userRole;

    // 所属部门编号。
    private Integer deptId;

    // 所属部门名称。
    private String deptName;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

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

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
