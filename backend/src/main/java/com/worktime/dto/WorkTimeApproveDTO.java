package com.worktime.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// 工时审批通过入参对象：接收部门经理审批通过时传来的 JSON 数据。
public class WorkTimeApproveDTO {

    // 审批人编号不能为空；当前阶段用 managerId 模拟登录后的部门经理。
    @NotNull(message = "审批人不能为空")
    private Integer managerId;

    // 审批说明可以为空，最多 500 个字符。
    @Size(max = 500, message = "审批说明不能超过500个字符")
    private String operationDesc;

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getOperationDesc() {
        return operationDesc;
    }

    public void setOperationDesc(String operationDesc) {
        this.operationDesc = operationDesc;
    }
}
