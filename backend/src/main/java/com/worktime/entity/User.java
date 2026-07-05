package com.worktime.entity;

// 用户实体类：对应数据库中的 user 表。
public class User {

    // 用户编号，对应 user.user_id。
    private Integer userId;

    // 用户姓名，对应 user.user_name。
    private String userName;

    // 手机号，对应 user.phone，用作登录账号。
    private String phone;

    // 密码哈希值，对应 user.psw。
    private String psw;

    // 邮箱，对应 user.email。
    private String email;

    // 用户角色：0管理员，1部门经理，2员工。
    private String userRole;

    // 所属部门编号，对应 user.dept_id。
    private Integer deptId;

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

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
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
