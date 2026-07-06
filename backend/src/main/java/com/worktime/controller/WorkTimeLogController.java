package com.worktime.controller;

import com.worktime.common.ApiResponse;
import com.worktime.service.WorkTimeLogService;
import com.worktime.vo.WorkTimeLogVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 工时日志接口：负责查询工时申报单的操作记录。
@RestController
@RequestMapping("/api/work-time-logs")
public class WorkTimeLogController {

    // 工时日志业务对象。
    private final WorkTimeLogService workTimeLogService;

    public WorkTimeLogController(WorkTimeLogService workTimeLogService) {
        this.workTimeLogService = workTimeLogService;
    }

    // 查询当前登录用户可见的工时操作日志列表，对应 GET /api/work-time-logs。
    @GetMapping
    public ApiResponse<List<WorkTimeLogVO>> listVisibleLogs(
            @RequestParam(required = false) Integer workId,
            @RequestParam(required = false) Integer operationType) {
        return ApiResponse.success(workTimeLogService.listVisibleLogs(workId, operationType));
    }

    // 根据工时编号查询操作日志，对应 GET /api/work-time-logs/work-times/{workId}。
    @GetMapping("/work-times/{workId}")
    public ApiResponse<List<WorkTimeLogVO>> listLogsByWorkId(@PathVariable Integer workId) {
        return ApiResponse.success(workTimeLogService.listLogsByWorkId(workId));
    }
}
