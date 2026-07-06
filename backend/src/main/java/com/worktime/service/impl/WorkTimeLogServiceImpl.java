package com.worktime.service.impl;

import com.worktime.common.AuthUtil;
import com.worktime.common.CurrentUser;
import com.worktime.common.RoleConstants;
import com.worktime.exception.BusinessException;
import com.worktime.mapper.WorkTimeApplyMapper;
import com.worktime.mapper.WorkTimeLogMapper;
import com.worktime.service.WorkTimeLogService;
import com.worktime.vo.WorkTimeApplyRowVO;
import com.worktime.vo.WorkTimeLogVO;
import org.springframework.stereotype.Service;

import java.util.List;

// 工时日志业务实现类：处理工时操作日志查询规则。
@Service
public class WorkTimeLogServiceImpl implements WorkTimeLogService {

    // 工时申报数据访问对象，用于检查工时申报单是否存在。
    private final WorkTimeApplyMapper workTimeApplyMapper;

    // 工时日志数据访问对象。
    private final WorkTimeLogMapper workTimeLogMapper;

    public WorkTimeLogServiceImpl(WorkTimeApplyMapper workTimeApplyMapper, WorkTimeLogMapper workTimeLogMapper) {
        this.workTimeApplyMapper = workTimeApplyMapper;
        this.workTimeLogMapper = workTimeLogMapper;
    }

    // 查询当前登录用户可见的工时操作日志列表。
    @Override
    public List<WorkTimeLogVO> listVisibleLogs(Integer workId, Integer operationType) {
        CurrentUser currentUser = AuthUtil.currentUser();
        return workTimeLogMapper.selectVisibleLogs(
                        currentUser.getUserRole(),
                        currentUser.getUserId(),
                        currentUser.getDeptId(),
                        workId,
                        operationType)
                .stream()
                .map(WorkTimeLogVO::fromRow)
                .toList();
    }

    // 根据工时编号查询该工时申报单的操作日志。
    @Override
    public List<WorkTimeLogVO> listLogsByWorkId(Integer workId) {
        WorkTimeApplyRowVO workTime = workTimeApplyMapper.selectByIdIncludeDeleted(workId);
        if (workTime == null) {
            throw new BusinessException(404, "工时申报单不存在");
        }
        validateCanViewLog(workTime);
        return workTimeLogMapper.selectByWorkId(workId).stream()
                .map(WorkTimeLogVO::fromRow)
                .toList();
    }

    // 校验当前用户是否可以查看该工时报单的操作日志。
    private void validateCanViewLog(WorkTimeApplyRowVO workTime) {
        CurrentUser currentUser = AuthUtil.currentUser();
        if (RoleConstants.ADMIN.equals(currentUser.getUserRole())) {
            return;
        }
        if (RoleConstants.EMPLOYEE.equals(currentUser.getUserRole()) && currentUser.getUserId().equals(workTime.getUserId())) {
            return;
        }
        if (RoleConstants.MANAGER.equals(currentUser.getUserRole()) && currentUser.getDeptId().equals(workTime.getUserDeptId())) {
            return;
        }
        throw new BusinessException(403, "无权查看该工时日志");
    }
}
