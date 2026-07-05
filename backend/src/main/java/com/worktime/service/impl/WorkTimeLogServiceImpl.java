package com.worktime.service.impl;

import com.worktime.exception.BusinessException;
import com.worktime.mapper.WorkTimeApplyMapper;
import com.worktime.mapper.WorkTimeLogMapper;
import com.worktime.service.WorkTimeLogService;
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

    // 根据工时编号查询该工时申报单的操作日志。
    @Override
    public List<WorkTimeLogVO> listLogsByWorkId(Integer workId) {
        if (workTimeApplyMapper.countByIdIncludeDeleted(workId) == 0) {
            throw new BusinessException(404, "工时申报单不存在");
        }
        return workTimeLogMapper.selectByWorkId(workId).stream()
                .map(WorkTimeLogVO::fromRow)
                .toList();
    }
}
