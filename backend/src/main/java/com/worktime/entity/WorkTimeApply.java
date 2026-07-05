package com.worktime.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

// 工时申报单实体类：对应数据库中的 work_time_apply 表。
public class WorkTimeApply {

    // 工时编号，对应 work_time_apply.work_id。
    private Integer workId;

    // 用户编号，对应 work_time_apply.user_id。
    private Integer userId;

    // 项目编号，对应 work_time_apply.project_id。
    private Integer projectId;

    // 工作日期，对应 work_time_apply.work_date。
    private LocalDate workDate;

    // 工时数，对应 work_time_apply.work_hours。
    private BigDecimal workHours;

    // 工作描述，对应 work_time_apply.work_desc。
    private String workDesc;

    // 工时状态：0草稿，1待审批，2审批通过，3已驳回。
    private Integer status;

    // 删除标记：0未删除，1已删除。
    private Integer isDeleted;

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

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
