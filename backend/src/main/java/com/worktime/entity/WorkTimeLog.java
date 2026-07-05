package com.worktime.entity;

import java.time.LocalDateTime;

// 工时操作日志实体类：对应数据库中的 work_time_log 表。
public class WorkTimeLog {

    // 日志编号，对应 work_time_log.log_id。
    private Integer logId;

    // 工时编号，对应 work_time_log.work_id。
    private Integer workId;

    // 操作类型：0新建，1修改，2提交，3审批通过，4驳回。
    private Integer operationType;

    // 操作时间。
    private LocalDateTime operationTime;

    // 操作人编号。
    private Integer operatorId;

    // 操作说明。
    private String operationDesc;

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

    public String getOperationDesc() {
        return operationDesc;
    }

    public void setOperationDesc(String operationDesc) {
        this.operationDesc = operationDesc;
    }
}
