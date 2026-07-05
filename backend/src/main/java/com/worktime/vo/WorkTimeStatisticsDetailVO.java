package com.worktime.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

// 工时统计明细返回对象：用于展示进入统计范围的每一条审批通过工时。
public class WorkTimeStatisticsDetailVO {

    // 工时编号。
    private Integer workId;

    // 用户编号。
    private Integer userId;

    // 用户姓名。
    private String userName;

    // 部门编号。
    private Integer deptId;

    // 部门名称。
    private String deptName;

    // 项目编号。
    private Integer projectId;

    // 项目名称。
    private String projectName;

    // 工作日期。
    private LocalDate workDate;

    // 工时数。
    private BigDecimal workHours;

    // 工作描述。
    private String workDesc;

    public Integer getWorkId() {
        return workId;
    }

    public void setWorkId(Integer workId) {
        this.workId = workId;
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

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public BigDecimal getWorkHours() {
        return workHours;
    }

    public void setWorkHours(BigDecimal workHours) {
        this.workHours = workHours;
    }

    public String getWorkDesc() {
        return workDesc;
    }

    public void setWorkDesc(String workDesc) {
        this.workDesc = workDesc;
    }
}
