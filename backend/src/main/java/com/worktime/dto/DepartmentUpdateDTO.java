package com.worktime.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 修改部门入参对象：接收前端修改部门时传来的 JSON 数据。
public class DepartmentUpdateDTO {

    // 部门名称不能为空，并且长度不能超过数据库字段 dept_name 的 50 个字符限制。
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 50, message = "部门名称长度不能超过50个字符")
    private String deptName;

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
