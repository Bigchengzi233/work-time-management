package com.worktime.common;

// token 负载对象：保存 token 中携带的用户编号、角色和过期时间。
public class TokenPayload {

    // 用户编号。
    private Integer userId;

    // 用户角色：0管理员，1部门经理，2员工。
    private String userRole;

    // 所属部门编号。
    private Integer deptId;

    // 过期时间戳，单位毫秒。
    private Long expireAt;

    public TokenPayload(Integer userId, String userRole, Integer deptId, Long expireAt) {
        this.userId = userId;
        this.userRole = userRole;
        this.deptId = deptId;
        this.expireAt = expireAt;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public Long getExpireAt() {
        return expireAt;
    }
}
