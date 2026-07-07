package com.worktime.vo;

import java.time.LocalDate;

// 工时异常返回对象：用于部门经理查看某天没有填报工时的本部门员工。
public class WorkTimeExceptionVO {

    // 员工编号。
    private Integer userId;

    // 员工姓名。
    private String userName;

    // 员工所属部门编号。
    private Integer deptId;

    // 员工所属部门名称。
    private String deptName;

    // 异常日期。
    private LocalDate workDate;

    // 当前可填报的有效授权项目数量。
    private Integer activeProjectCount;

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

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public Integer getActiveProjectCount() {
        return activeProjectCount;
    }

    public void setActiveProjectCount(Integer activeProjectCount) {
        this.activeProjectCount = activeProjectCount;
    }
}
