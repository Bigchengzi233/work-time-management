package com.worktime.service.impl;

import com.worktime.exception.BusinessException;
import com.worktime.mapper.StatisticsMapper;
import com.worktime.service.StatisticsService;
import com.worktime.vo.WorkTimeStatisticsDetailVO;
import com.worktime.vo.WorkTimeStatisticsVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// 工时统计业务实现类：处理不同角色的数据范围和统计口径。
@Service
public class StatisticsServiceImpl implements StatisticsService {

    // 工时统计数据访问对象。
    private final StatisticsMapper statisticsMapper;

    public StatisticsServiceImpl(StatisticsMapper statisticsMapper) {
        this.statisticsMapper = statisticsMapper;
    }

    // 查询个人工时统计。
    @Override
    public WorkTimeStatisticsVO getPersonalStatistics(Integer userId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        if (statisticsMapper.countUserById(userId) == 0) {
            throw new BusinessException(404, "用户不存在");
        }

        List<WorkTimeStatisticsDetailVO> details = statisticsMapper.selectPersonalDetails(userId, startDate, endDate);
        return buildStatistics("personal", userId, statisticsMapper.selectUserNameById(userId), startDate, endDate, details);
    }

    // 查询部门工时统计。
    @Override
    public WorkTimeStatisticsVO getDepartmentStatistics(Integer managerId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        if (statisticsMapper.countManagerById(managerId) == 0) {
            throw new BusinessException(400, "查询人不是部门经理");
        }

        List<WorkTimeStatisticsDetailVO> details = statisticsMapper.selectDepartmentDetails(managerId, startDate, endDate);
        return buildStatistics("department", managerId, statisticsMapper.selectManagerDepartmentName(managerId), startDate, endDate, details);
    }

    // 查询公司工时统计。
    @Override
    public WorkTimeStatisticsVO getCompanyStatistics(Integer adminId, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);

        if (statisticsMapper.countAdminById(adminId) == 0) {
            throw new BusinessException(400, "查询人不是管理员");
        }

        List<WorkTimeStatisticsDetailVO> details = statisticsMapper.selectCompanyDetails(startDate, endDate);
        return buildStatistics("company", adminId, "公司总体", startDate, endDate, details);
    }

    // 校验日期范围。
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BusinessException(400, "开始日期不能晚于结束日期");
        }
    }

    // 组装统一统计结果。
    private WorkTimeStatisticsVO buildStatistics(
            String statisticsType,
            Integer scopeId,
            String scopeName,
            LocalDate startDate,
            LocalDate endDate,
            List<WorkTimeStatisticsDetailVO> details) {
        WorkTimeStatisticsVO statisticsVO = new WorkTimeStatisticsVO();
        statisticsVO.setStatisticsType(statisticsType);
        statisticsVO.setScopeId(scopeId);
        statisticsVO.setScopeName(scopeName);
        statisticsVO.setStartDate(startDate);
        statisticsVO.setEndDate(endDate);
        statisticsVO.setDetails(details);
        statisticsVO.setTotalHours(calculateTotalHours(details));
        return statisticsVO;
    }

    // 计算明细列表中的总工时。
    private BigDecimal calculateTotalHours(List<WorkTimeStatisticsDetailVO> details) {
        return details.stream()
                .map(WorkTimeStatisticsDetailVO::getWorkHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
