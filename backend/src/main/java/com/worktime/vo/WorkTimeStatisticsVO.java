package com.worktime.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// 工时统计返回对象：包含统计范围、总工时和明细列表。
public class WorkTimeStatisticsVO {

    // 统计类型：personal 个人，department 部门，company 公司。
    private String statisticsType;

    // 统计对象编号，例如用户编号、部门经理编号或管理员编号。
    private Integer scopeId;

    // 统计对象名称，例如员工姓名、部门名称或公司总体。
    private String scopeName;

    // 查询开始日期。
    private LocalDate startDate;

    // 查询结束日期。
    private LocalDate endDate;

    // 审批通过工时总和。
    private BigDecimal totalHours;

    // 工时明细列表。
    private List<WorkTimeStatisticsDetailVO> details;

    public String getStatisticsType() {
        return statisticsType;
    }

    public void setStatisticsType(String statisticsType) {
        this.statisticsType = statisticsType;
    }

    public Integer getScopeId() {
        return scopeId;
    }

    public void setScopeId(Integer scopeId) {
        this.scopeId = scopeId;
    }

    public String getScopeName() {
        return scopeName;
    }

    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(BigDecimal totalHours) {
        this.totalHours = totalHours;
    }

    public List<WorkTimeStatisticsDetailVO> getDetails() {
        return details;
    }

    public void setDetails(List<WorkTimeStatisticsDetailVO> details) {
        this.details = details;
    }
}
