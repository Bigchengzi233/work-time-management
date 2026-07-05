package com.worktime.mapper;

import com.worktime.entity.WorkTimeApply;
import com.worktime.vo.WorkTimeApprovalRuleVO;
import com.worktime.vo.WorkTimeApplyRowVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

// 工时申报数据访问接口：定义 work_time_apply 表相关数据库操作。
public interface WorkTimeApplyMapper {

    // 查询全部工时申报单。
    List<WorkTimeApplyRowVO> selectAll();

    // 根据工时编号查询单条工时申报单。
    WorkTimeApplyRowVO selectById(Integer workId);

    // 根据工时编号统计工时申报单数量，包含已软删除记录。
    int countByIdIncludeDeleted(Integer workId);

    // 根据用户编号查询该用户的工时申报单。
    List<WorkTimeApplyRowVO> selectByUserId(Integer userId);

    // 根据部门经理编号查询其本部门待审批工时。
    List<WorkTimeApplyRowVO> selectPendingByManagerId(Integer managerId);

    // 检查用户是否存在。
    int countUserById(Integer userId);

    // 检查用户是否是部门经理。
    int countManagerById(Integer managerId);

    // 检查项目是否存在。
    int countProjectById(Integer projectId);

    // 检查用户和项目之间是否存在有效授权，同时要求项目处于启用状态。
    int countValidUserProject(@Param("userId") Integer userId, @Param("projectId") Integer projectId);

    // 查询审批规则所需信息。
    WorkTimeApprovalRuleVO selectApprovalRule(@Param("workId") Integer workId, @Param("managerId") Integer managerId);

    // 新增时检查同一用户、同一项目、同一天是否已存在工时申报单。
    int countDuplicateForCreate(
            @Param("userId") Integer userId,
            @Param("projectId") Integer projectId,
            @Param("workDate") LocalDate workDate);

    // 修改时检查同一用户、同一项目、同一天是否被其他工时申报单占用。
    int countDuplicateForUpdate(
            @Param("workId") Integer workId,
            @Param("userId") Integer userId,
            @Param("projectId") Integer projectId,
            @Param("workDate") LocalDate workDate);

    // 新增工时申报单。
    int insert(WorkTimeApply workTimeApply);

    // 修改工时申报单。
    int updateById(WorkTimeApply workTimeApply);

    // 修改工时状态。
    int updateStatusById(@Param("workId") Integer workId, @Param("status") Integer status);

    // 软删除工时申报单。
    int softDeleteById(Integer workId);
}
