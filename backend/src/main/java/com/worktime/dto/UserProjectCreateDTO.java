package com.worktime.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// 新增项目授权入参对象：接收部门经理或管理员给员工分配项目时传来的 JSON 数据。
public class UserProjectCreateDTO {

    // 被授权的用户编号不能为空。
    @NotNull(message = "用户不能为空")
    private Integer userId;

    // 被授权的项目编号不能为空。
    @NotNull(message = "项目不能为空")
    private Integer projectId;

    // 授权状态：0取消授权，1有效授权。
    @NotNull(message = "授权状态不能为空")
    @Min(value = 0, message = "授权状态只能是0或1")
    @Max(value = 1, message = "授权状态只能是0或1")
    private Integer authStatus;

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

    public Integer getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(Integer authStatus) {
        this.authStatus = authStatus;
    }
}
