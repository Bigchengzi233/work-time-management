package com.worktime.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 工时操作日志返回对象：返回给前端展示工时流转记录。
public class WorkTimeLogVO {

    // 日志编号。
    private Integer logId;

    // 工时编号。
    private Integer workId;

    // 操作类型：0新建，1修改，2提交，3审批通过，4驳回。
    private Integer operationType;

    // 操作时间。
    private LocalDateTime operationTime;

    // 操作人编号。
    private Integer operatorId;

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

    // 操作说明。
    private String operationDesc;

    public static WorkTimeLogVO fromRow(WorkTimeLogRowVO row) {
        WorkTimeLogVO vo = new WorkTimeLogVO();
        vo.setLogId(row.getLogId());
        vo.setWorkId(row.getWorkId());
        vo.setOperationType(row.getOperationType());
        vo.setOperationTime(row.getOperationTime());
        vo.setOperatorId(row.getOperatorId());
        vo.setOperatorName(row.getOperatorName());
        vo.setWorkUserId(row.getWorkUserId());
        vo.setWorkUserName(row.getWorkUserName());
        vo.setUserDeptName(row.getUserDeptName());
        vo.setProjectName(row.getProjectName());
        vo.setWorkDate(row.getWorkDate());
        vo.setOperationDesc(row.getOperationDesc());
        return vo;
    }

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Integer getWorkId() {
        return workId;
    }

    public void setWorkId(Integer workId) {
        this.workId = workId;
    }

    public Integer getOperationType() {
        return operationType;
    }

    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }

    public LocalDateTime getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

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

    public String getOperationDesc() {
        return operationDesc;
    }

    public void setOperationDesc(String operationDesc) {
        this.operationDesc = operationDesc;
    }
}
