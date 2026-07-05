package com.worktime.vo;

import com.worktime.entity.Project;

// 项目数据库查询结果对象：在 Project 基础上额外带出部门名称。
public class ProjectRowVO extends Project {

    // 所属部门名称，由 project 表关联 department 表查询得到。
    private String deptName;

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
