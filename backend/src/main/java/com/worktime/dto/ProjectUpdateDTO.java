package com.worktime.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// 修改项目入参对象：接收管理员修改项目时传来的 JSON 数据。
public class ProjectUpdateDTO {

    // 项目名称不能为空。
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 100, message = "项目名称长度不能超过100个字符")
    private String projectName;

    // 项目状态：0禁用，1启用。
    @NotNull(message = "项目状态不能为空")
    @Min(value = 0, message = "项目状态只能是0或1")
    @Max(value = 1, message = "项目状态只能是0或1")
    private Integer projectStatus;

    // 所属部门编号不能为空。
    @NotNull(message = "所属部门不能为空")
    private Integer deptId;

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

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }
}
