package com.worktime.mapper;

import com.worktime.entity.WorkTimeLog;
import com.worktime.vo.WorkTimeLogRowVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 工时日志数据访问接口：定义 work_time_log 表相关数据库操作。
public interface WorkTimeLogMapper {

    // 按当前登录用户权限查询可见的工时操作日志列表。
    List<WorkTimeLogRowVO> selectVisibleLogs(
            @Param("userRole") String userRole,
            @Param("currentUserId") Integer currentUserId,
            @Param("deptId") Integer deptId,
            @Param("workId") Integer workId,
            @Param("operationType") Integer operationType);

    // 根据工时编号查询该工时申报单的操作日志。
    List<WorkTimeLogRowVO> selectByWorkId(Integer workId);

    // 新增工时操作日志。
    int insert(WorkTimeLog workTimeLog);
}
