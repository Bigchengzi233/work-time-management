package com.worktime.controller;

import com.worktime.common.ApiResponse;
import com.worktime.service.StatisticsService;
import com.worktime.vo.WorkTimeStatisticsVO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

// 工时统计接口：负责个人、部门、公司维度的工时查询统计。
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    // 工时统计业务对象。
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    // 查询个人工时统计，对应 GET /api/statistics/personal。
    @GetMapping("/personal")
    public ApiResponse<WorkTimeStatisticsVO> getPersonalStatistics(
            @RequestParam Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.success(statisticsService.getPersonalStatistics(userId, startDate, endDate));
    }

    // 查询部门工时统计，对应 GET /api/statistics/department。
    @GetMapping("/department")
    public ApiResponse<WorkTimeStatisticsVO> getDepartmentStatistics(
            @RequestParam Integer managerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.success(statisticsService.getDepartmentStatistics(managerId, startDate, endDate));
    }

    // 管理员按部门查询工时统计，对应 GET /api/statistics/department-by-dept。
    @GetMapping("/department-by-dept")
    public ApiResponse<WorkTimeStatisticsVO> getDepartmentStatisticsByDeptId(
            @RequestParam Integer adminId,
            @RequestParam Integer deptId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.success(statisticsService.getDepartmentStatisticsByDeptId(adminId, deptId, startDate, endDate));
    }

    // 查询公司工时统计，对应 GET /api/statistics/company。
    @GetMapping("/company")
    public ApiResponse<WorkTimeStatisticsVO> getCompanyStatistics(
            @RequestParam Integer adminId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ApiResponse.success(statisticsService.getCompanyStatistics(adminId, startDate, endDate));
    }
}
