package com.worktime.common;

// 当前登录用户对象：保存 token 解析出来的用户身份。
public class CurrentUser {

    // 当前登录用户编号。
    private Integer userId;

    // 当前登录用户角色：0管理员，1部门经理，2员工。
    private String userRole;

    public CurrentUser(Integer userId, String userRole) {
        this.userId = userId;
        this.userRole = userRole;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
