package com.worktime.entity;

// 部门实体类：对应数据库中的 department 表。
public class Department {

    // 部门编号，对应 department.dept_id 字段。
    private Integer deptId;

    // 部门名称，对应 department.dept_name 字段。
    private String deptName;

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
