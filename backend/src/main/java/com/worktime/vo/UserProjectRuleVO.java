package com.worktime.vo;

// 用户项目授权规则对象：用于校验用户、项目、部门和项目状态是否满足授权规则。
public class UserProjectRuleVO {

    // 用户编号。
    private Integer userId;

    // 用户角色：0管理员，1部门经理，2员工。
    private String userRole;

    // 用户所属部门编号。
    private Integer userDeptId;

    // 项目编号。
    private Integer projectId;

    // 项目所属部门编号。
    private Integer projectDeptId;

    // 项目状态：0禁用，1启用。
    private Integer projectStatus;

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

    public Integer getUserDeptId() {
        return userDeptId;
    }

    public void setUserDeptId(Integer userDeptId) {
        this.userDeptId = userDeptId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getProjectDeptId() {
        return projectDeptId;
    }

    public void setProjectDeptId(Integer projectDeptId) {
        this.projectDeptId = projectDeptId;
    }

    public Integer getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(Integer projectStatus) {
        this.projectStatus = projectStatus;
    }
}
