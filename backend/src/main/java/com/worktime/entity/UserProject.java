package com.worktime.entity;

import java.time.LocalDateTime;

// 用户项目授权实体类：对应数据库中的 user_project 表。
public class UserProject {

    // 授权编号，对应 user_project.auth_id。
    private Integer authId;

    // 用户编号，对应 user_project.user_id。
    private Integer userId;

    // 项目编号，对应 user_project.project_id。
    private Integer projectId;

    // 授权时间，对应 user_project.auth_time。
    private LocalDateTime authTime;

    // 授权状态：0取消授权，1有效授权。
    private Integer authStatus;

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

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
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
