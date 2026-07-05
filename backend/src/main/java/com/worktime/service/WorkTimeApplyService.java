package com.worktime.service;

import com.worktime.dto.WorkTimeCreateDTO;
import com.worktime.dto.WorkTimeUpdateDTO;
import com.worktime.vo.WorkTimeApplyVO;

import java.util.List;

// 工时申报业务接口：定义员工填报工时模块对外提供的业务能力。
public interface WorkTimeApplyService {

    // 查询全部工时申报单。
    List<WorkTimeApplyVO> listWorkTimes();

    // 根据工时编号查询单条工时申报单。
    WorkTimeApplyVO getWorkTimeById(Integer workId);

    // 根据用户编号查询该用户的工时申报单。
    List<WorkTimeApplyVO> listWorkTimesByUserId(Integer userId);

    // 新建工时草稿。
    WorkTimeApplyVO createWorkTime(WorkTimeCreateDTO createDTO);

    // 修改工时草稿或已驳回工时。
    WorkTimeApplyVO updateWorkTime(Integer workId, WorkTimeUpdateDTO updateDTO);

    // 提交工时审批。
    WorkTimeApplyVO submitWorkTime(Integer workId);
}
