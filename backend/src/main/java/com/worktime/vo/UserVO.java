package com.worktime.vo;

import com.worktime.entity.User;

// 用户返回对象：返回给前端展示，不包含密码字段。
public class UserVO {

    // 用户编号。
    private Integer userId;

    // 用户姓名。
    private String userName;

    // 手机号。
    private String phone;

    // 邮箱。
    private String email;

    // 用户角色：0管理员，1部门经理，2员工。
    private String userRole;

    // 所属部门编号。
    private Integer deptId;

    // 所属部门名称。
    private String deptName;

    public static UserVO fromEntity(User user, String deptName) {
        UserVO userVO = new UserVO();
        userVO.setUserId(user.getUserId());
        userVO.setUserName(user.getUserName());
        userVO.setPhone(user.getPhone());
        userVO.setEmail(user.getEmail());
        userVO.setUserRole(user.getUserRole());
        userVO.setDeptId(user.getDeptId());
        userVO.setDeptName(deptName);
        return userVO;
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

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
