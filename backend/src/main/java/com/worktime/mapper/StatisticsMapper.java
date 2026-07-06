package com.worktime.mapper;

import com.worktime.vo.WorkTimeStatisticsDetailVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

// 工时统计数据访问接口：定义个人、部门、公司维度的工时统计查询。
public interface StatisticsMapper {

    // 检查用户是否存在。
    int countUserById(Integer userId);

    // 检查用户是否是部门经理。
    int countManagerById(Integer managerId);

    // 检查用户是否是管理员。
    int countAdminById(Integer adminId);

    // 检查部门是否存在。
    int countDepartmentById(Integer deptId);

    // 查询用户姓名，用于统计结果标题。
    String selectUserNameById(Integer userId);

    // 查询部门名称，用于统计结果标题。
    String selectDepartmentNameById(Integer deptId);

    // 查询部门经理所在部门名称，用于部门统计结果标题。
    String selectManagerDepartmentName(Integer managerId);

    // 查询个人审批通过工时明细。
    List<WorkTimeStatisticsDetailVO> selectPersonalDetails(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 查询部门审批通过工时明细。
    List<WorkTimeStatisticsDetailVO> selectDepartmentDetails(
            @Param("managerId") Integer managerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 管理员按部门编号查询审批通过工时明细。
    List<WorkTimeStatisticsDetailVO> selectDepartmentDetailsByDeptId(
            @Param("deptId") Integer deptId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 查询公司审批通过工时明细。
    List<WorkTimeStatisticsDetailVO> selectCompanyDetails(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
