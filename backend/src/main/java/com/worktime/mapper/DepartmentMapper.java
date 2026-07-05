package com.worktime.mapper;

import com.worktime.entity.Department;

import java.util.List;

// 部门数据访问接口：定义 Java 调用数据库的方法。
public interface DepartmentMapper {

    // 查询全部部门，真正执行的 SQL 写在 DepartmentMapper.xml 中。
    List<Department> selectAll();
}
