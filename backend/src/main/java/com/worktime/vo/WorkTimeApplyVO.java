package com.worktime.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

// 工时申报返回对象：返回给前端展示工时申报信息。
public class WorkTimeApplyVO {

    // 工时编号。
    private Integer workId;

    // 填报人编号。
    private Integer userId;

    // 填报人姓名。
    private String userName;

    // 填报人所属部门编号。
    private Integer userDeptId;

    // 填报人所属部门名称。
    private String userDeptName;

    // 项目编号。
    private Integer projectId;

    // 项目名称。
    private String projectName;

    // 项目状态。
    private Integer projectStatus;

    // 项目所属部门编号。
    private Integer projectDeptId;

    // 项目所属部门名称。
    private String projectDeptName;

    // 工作日期。
    private LocalDate workDate;

    // 工时数。
    private BigDecimal workHours;

    // 工作描述。
    private String workDesc;

    // 工时状态：0草稿，1待审批，2审批通过，3已驳回。
    private Integer status;

    // 删除标记：0未删除，1已删除。
    private Integer isDeleted;

    public static WorkTimeApplyVO fromRow(WorkTimeApplyRowVO row) {
        WorkTimeApplyVO vo = new WorkTimeApplyVO();
        vo.setWorkId(row.getWorkId());
        vo.setUserId(row.getUserId());
        vo.setUserName(row.getUserName());
        vo.setUserDeptId(row.getUserDeptId());
        vo.setUserDeptName(row.getUserDeptName());
        vo.setProjectId(row.getProjectId());
        vo.setProjectName(row.getProjectName());
        vo.setProjectStatus(row.getProjectStatus());
        vo.setProjectDeptId(row.getProjectDeptId());
        vo.setProjectDeptName(row.getProjectDeptName());
        vo.setWorkDate(row.getWorkDate());
        vo.setWorkHours(row.getWorkHours());
        vo.setWorkDesc(row.getWorkDesc());
        vo.setStatus(row.getStatus());
        vo.setIsDeleted(row.getIsDeleted());
        return vo;
    }

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
