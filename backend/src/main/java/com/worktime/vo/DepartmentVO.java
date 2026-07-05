package com.worktime.vo;

import com.worktime.entity.Department;

// 部门返回对象：用于把部门数据返回给前端展示。
public class DepartmentVO {

    // 部门编号，前端展示或后续选择部门时会用到。
    private Integer deptId;

    // 部门名称，前端页面主要展示这个字段。
    private String deptName;

    // 无参构造方法，给 JSON 序列化工具使用。
    public DepartmentVO() {
    }

    // 全参构造方法，方便从实体对象转换成返回对象。
    public DepartmentVO(Integer deptId, String deptName) {
        this.deptId = deptId;
        this.deptName = deptName;
    }

    // 把数据库实体 Department 转成前端返回对象 DepartmentVO。
    public static DepartmentVO fromEntity(Department department) {
        return new DepartmentVO(department.getDeptId(), department.getDeptName());
    }

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
