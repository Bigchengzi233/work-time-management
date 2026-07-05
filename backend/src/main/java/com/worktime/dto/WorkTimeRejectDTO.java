package com.worktime.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// 工时驳回入参对象：接收部门经理驳回工时时传来的 JSON 数据。
public class WorkTimeRejectDTO {

    // 审批人编号不能为空；当前阶段用 managerId 模拟登录后的部门经理。
    @NotNull(message = "审批人不能为空")
    private Integer managerId;

    // 驳回原因不能为空，方便员工修改时知道问题在哪里。
    @NotBlank(message = "驳回原因不能为空")
    @Size(max = 500, message = "驳回原因不能超过500个字符")
    private String rejectReason;

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}
