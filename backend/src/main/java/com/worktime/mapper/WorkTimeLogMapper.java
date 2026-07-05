package com.worktime.mapper;

import com.worktime.entity.WorkTimeLog;
import com.worktime.vo.WorkTimeLogRowVO;

import java.util.List;

// 工时日志数据访问接口：定义 work_time_log 表相关数据库操作。
public interface WorkTimeLogMapper {

    // 根据工时编号查询该工时申报单的操作日志。
    List<WorkTimeLogRowVO> selectByWorkId(Integer workId);

    // 新增工时操作日志。
    int insert(WorkTimeLog workTimeLog);
}
