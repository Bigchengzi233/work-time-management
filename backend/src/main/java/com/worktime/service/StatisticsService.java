package com.worktime.service;

import com.worktime.vo.WorkTimeStatisticsVO;

import java.time.LocalDate;

// 工时统计业务接口：定义个人、部门、公司维度的统计能力。
public interface StatisticsService {

    // 查询个人工时统计。
    WorkTimeStatisticsVO getPersonalStatistics(Integer userId, LocalDate startDate, LocalDate endDate);

    // 查询部门工时统计。
    WorkTimeStatisticsVO getDepartmentStatistics(Integer managerId, LocalDate startDate, LocalDate endDate);

    // 管理员按部门查询工时统计。
    WorkTimeStatisticsVO getDepartmentStatisticsByDeptId(Integer adminId, Integer deptId, LocalDate startDate, LocalDate endDate);

    // 查询公司工时统计。
    WorkTimeStatisticsVO getCompanyStatistics(Integer adminId, LocalDate startDate, LocalDate endDate);
}
