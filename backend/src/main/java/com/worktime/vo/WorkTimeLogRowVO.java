package com.worktime.vo;

import com.worktime.entity.WorkTimeLog;

import java.time.LocalDate;

// 工时日志数据库查询结果对象：在 WorkTimeLog 基础上带出操作人姓名。
public class WorkTimeLogRowVO extends WorkTimeLog {

    // 操作人姓名。
    private String operatorName;

    // 工时所属员工编号。
    private Integer workUserId;

    // 工时所属员工姓名。
    private String workUserName;

    // 工时所属员工部门名称。
    private String userDeptName;

    // 工时关联项目名称。
    private String projectName;

    // 工时对应的工作日期。
    private LocalDate workDate;

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getWorkUserId() {
        return workUserId;
    }

    public void setWorkUserId(Integer workUserId) {
        this.workUserId = workUserId;
    }

    public String getWorkUserName() {
        return workUserName;
    }

    public void setWorkUserName(String workUserName) {
        this.workUserName = workUserName;
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

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }
}
