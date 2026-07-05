package com.worktime.mapper;

import com.worktime.entity.Project;
import com.worktime.vo.ProjectRowVO;

import java.util.List;

// 项目数据访问接口：定义 project 表相关数据库操作。
public interface ProjectMapper {

    // 查询全部项目，并带出部门名称。
    List<ProjectRowVO> selectAll();

    // 根据项目编号查询项目，并带出部门名称。
    ProjectRowVO selectById(Integer projectId);

    // 根据部门编号统计部门数量，用于检查项目所属部门是否存在。
    int countDepartmentById(Integer deptId);

    // 新增项目。
    int insert(Project project);

    // 根据项目编号修改项目。
    int updateById(Project project);

    // 统计项目是否存在员工授权记录。
    int countUserProjectsByProjectId(Integer projectId);

    // 统计项目是否存在工时申报单。
    int countWorkTimesByProjectId(Integer projectId);

    // 根据项目编号删除项目。
    int deleteById(Integer projectId);
}
