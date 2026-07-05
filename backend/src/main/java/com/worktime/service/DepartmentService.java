package com.worktime.service;

import com.worktime.dto.DepartmentCreateDTO;
import com.worktime.dto.DepartmentUpdateDTO;
import com.worktime.vo.DepartmentVO;

import java.util.List;

// 部门业务接口：定义部门模块对外提供哪些业务能力。
public interface DepartmentService {

    // 查询全部部门，返回给 Controller 使用。
    List<DepartmentVO> listDepartments();

    // 根据部门编号查询单个部门。
    DepartmentVO getDepartmentById(Integer deptId);

    // 新增部门。
    DepartmentVO createDepartment(DepartmentCreateDTO createDTO);

    // 修改部门。
    DepartmentVO updateDepartment(Integer deptId, DepartmentUpdateDTO updateDTO);

    // 删除部门。
    void deleteDepartment(Integer deptId);
}
