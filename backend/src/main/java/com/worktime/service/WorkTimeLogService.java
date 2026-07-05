package com.worktime.service;

import com.worktime.vo.WorkTimeLogVO;

import java.util.List;

// 工时日志业务接口：定义工时操作日志查询能力。
public interface WorkTimeLogService {

    // 根据工时编号查询该工时申报单的操作日志。
    List<WorkTimeLogVO> listLogsByWorkId(Integer workId);
}
