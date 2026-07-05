package com.worktime.vo;

// 工时审批规则对象：用于校验审批人是否能审批指定工时。
public class WorkTimeApprovalRuleVO {

    // 审批人编号。
    private Integer managerId;

    // 审批人角色：0管理员，1部门经理，2员工。
    private String managerRole;

    // 审批人所属部门编号。
    private Integer managerDeptId;

    // 工时编号。
    private Integer workId;

    // 工时当前状态。
    private Integer workStatus;

    // 工时填报人编号。
    private Integer workUserId;

    // 工时填报人所属部门编号。
    private Integer workUserDeptId;

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getManagerRole() {
        return managerRole;
    }

    public void setManagerRole(String managerRole) {
        this.managerRole = managerRole;
    }

    public Integer getManagerDeptId() {
        return managerDeptId;
    }

    public void setManagerDeptId(Integer managerDeptId) {
        this.managerDeptId = managerDeptId;
    }

    public Integer getWorkId() {
        return workId;
    }

    public void setWorkId(Integer workId) {
        this.workId = workId;
    }

    public Integer getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(Integer workStatus) {
        this.workStatus = workStatus;
    }

    public Integer getWorkUserId() {
        return workUserId;
    }

    public void setWorkUserId(Integer workUserId) {
        this.workUserId = workUserId;
    }

    public Integer getWorkUserDeptId() {
        return workUserDeptId;
    }

    public void setWorkUserDeptId(Integer workUserDeptId) {
        this.workUserDeptId = workUserDeptId;
    }
}
