package com.worktime.vo;

import com.worktime.entity.UserProject;

// 用户项目授权数据库查询结果对象：在 UserProject 基础上带出用户、项目和部门名称。
public class UserProjectRowVO extends UserProject {

    // 用户姓名。
    private String userName;

    // 用户所属部门编号。
    private Integer userDeptId;

    // 用户所属部门名称。
    private String userDeptName;

    // 项目名称。
    private String projectName;

    // 项目状态：0禁用，1启用。
    private Integer projectStatus;

    // 项目所属部门编号。
    private Integer projectDeptId;

    // 项目所属部门名称。
    private String projectDeptName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserDeptId() {
        return userDeptId;
    }

    public void setUserDeptId(Integer userDeptId) {
        this.userDeptId = userDeptId;
    }

    public String getUserDeptName() {
        return userDeptName;
    }

    public void setUserDeptName(String userDeptName) {
        this.userDeptName = userDeptName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(Integer projectStatus) {
        this.projectStatus = projectStatus;
    }

    public Integer getProjectDeptId() {
        return projectDeptId;
    }

    public void setProjectDeptId(Integer projectDeptId) {
        this.projectDeptId = projectDeptId;
    }

    public String getProjectDeptName() {
        return projectDeptName;
    }

    public void setProjectDeptName(String projectDeptName) {
        this.projectDeptName = projectDeptName;
    }
}
