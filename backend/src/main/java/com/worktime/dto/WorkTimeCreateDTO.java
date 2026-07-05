package com.worktime.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

// 新建工时申报入参对象：接收员工新建工时草稿时传来的 JSON 数据。
public class WorkTimeCreateDTO {

    // 填报人编号不能为空。
    @NotNull(message = "填报人不能为空")
    private Integer userId;

    // 项目编号不能为空。
    @NotNull(message = "项目不能为空")
    private Integer projectId;

    // 工作日期不能为空。
    @NotNull(message = "工作日期不能为空")
    private LocalDate workDate;

    // 工时数不能为空，范围为 0.5 到 24 小时。
    @NotNull(message = "工时数不能为空")
    @DecimalMin(value = "0.5", message = "工时数不能小于0.5")
    @DecimalMax(value = "24.0", message = "工时数不能大于24")
    private BigDecimal workHours;

    // 工作描述可以为空，最多 500 个字符。
    @Size(max = 500, message = "工作描述不能超过500个字符")
    private String workDesc;

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
}
