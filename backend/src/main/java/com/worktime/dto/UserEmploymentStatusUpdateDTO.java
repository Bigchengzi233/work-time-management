package com.worktime.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// 用户在职状态修改入参：当前状态不改数据库，只保存到后端本地 JSON 文件。
public class UserEmploymentStatusUpdateDTO {

    // 在职状态：active 表示在职，inactive 表示离职。
    @NotBlank(message = "在职状态不能为空")
    @Pattern(regexp = "^(active|inactive)$", message = "在职状态只能是 active 或 inactive")
    private String employmentStatus;

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
}
