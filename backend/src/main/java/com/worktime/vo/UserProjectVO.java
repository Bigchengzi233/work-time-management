package com.worktime.vo;

import java.time.LocalDateTime;

// 用户项目授权返回对象：返回给前端展示授权关系。
public class UserProjectVO {

    // 授权编号。
    private Integer authId;

    // 用户编号。
    private Integer userId;

    // 用户姓名。
    private String userName;

    // 用户所属部门编号。
    private Integer userDeptId;

    // 用户所属部门名称。
    private String userDeptName;

    // 项目编号。
    private Integer projectId;

    // 项目名称。
    private String projectName;

    // 项目状态：0禁用，1启用。
    private Integer projectStatus;

    // 项目所属部门编号。
    private Integer projectDeptId;

    // 项目所属部门名称。
    private String projectDeptName;

    // 授权时间。
    private LocalDateTime authTime;

    // 授权状态：0取消授权，1有效授权。
    private Integer authStatus;

    public static UserProjectVO fromRow(UserProjectRowVO row) {
        UserProjectVO vo = new UserProjectVO();
        vo.setAuthId(row.getAuthId());
        vo.setUserId(row.getUserId());
        vo.setUserName(row.getUserName());
        vo.setUserDeptId(row.getUserDeptId());
        vo.setUserDeptName(row.getUserDeptName());
        vo.setProjectId(row.getProjectId());
        vo.setProjectName(row.getProjectName());
        vo.setProjectStatus(row.getProjectStatus());
        vo.setProjectDeptId(row.getProjectDeptId());
        vo.setProjectDeptName(row.getProjectDeptName());
        vo.setAuthTime(row.getAuthTime());
        vo.setAuthStatus(row.getAuthStatus());
        return vo;
    }

    public Integer getAuthId() {
        return authId;
    }

    public void setAuthId(Integer authId) {
        this.authId = authId;
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

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
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

    public LocalDateTime getAuthTime() {
        return authTime;
    }

    public void setAuthTime(LocalDateTime authTime) {
        this.authTime = authTime;
    }

    public Integer getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(Integer authStatus) {
        this.authStatus = authStatus;
    }
}
