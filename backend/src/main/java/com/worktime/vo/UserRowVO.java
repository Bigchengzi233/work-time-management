package com.worktime.vo;

import com.worktime.entity.User;

// 用户查询行对象：承接 user 表和 department 表连接查询后的结果。
public class UserRowVO extends User {

    // 所属部门名称，来自 department.dept_name。
    private String deptName;

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
