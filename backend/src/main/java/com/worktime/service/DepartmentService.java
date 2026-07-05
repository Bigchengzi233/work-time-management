package com.worktime.service;

import com.worktime.vo.DepartmentVO;

import java.util.List;

// 部门业务接口：定义部门模块对外提供哪些业务能力。
public interface DepartmentService {

    // 查询全部部门，返回给 Controller 使用。
    List<DepartmentVO> listDepartments();
}
