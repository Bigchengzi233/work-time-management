package com.worktime.mapper;

import com.worktime.entity.Department;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 部门数据访问接口：定义 Java 调用数据库的方法。
public interface DepartmentMapper {

    // 查询全部部门，真正执行的 SQL 写在 DepartmentMapper.xml 中。
    List<Department> selectAll();

    // 根据部门编号查询单个部门。
    Department selectById(Integer deptId);

    // 根据部门名称统计数量，用于新增时检查重名。
    int countByDeptName(String deptName);

    // 根据部门名称统计其他部门数量，用于修改时检查重名。
    int countByDeptNameExcludeId(@Param("deptName") String deptName, @Param("deptId") Integer deptId);

    // 新增部门。
    int insert(Department department);

    // 根据部门编号修改部门。
    int updateById(Department department);

    // 统计某部门下的用户数量。
    int countUsersByDeptId(Integer deptId);

    // 统计某部门下的项目数量。
    int countProjectsByDeptId(Integer deptId);

    // 根据部门编号删除部门。
    int deleteById(Integer deptId);
}
