package com.worktime.controller;

import com.worktime.common.ApiResponse;
import com.worktime.dto.WorkTimeApproveDTO;
import com.worktime.dto.WorkTimeCreateDTO;
import com.worktime.dto.WorkTimeRejectDTO;
import com.worktime.dto.WorkTimeUpdateDTO;
import com.worktime.service.WorkTimeApplyService;
import com.worktime.vo.WorkTimeApplyVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 工时申报接口：负责员工填报、修改、提交和查询工时申报单。
@RestController
@RequestMapping("/api/work-times")
public class WorkTimeApplyController {

    // 工时申报业务对象。
    private final WorkTimeApplyService workTimeApplyService;

    public WorkTimeApplyController(WorkTimeApplyService workTimeApplyService) {
        this.workTimeApplyService = workTimeApplyService;
    }

    // 查询全部工时申报单，对应 GET /api/work-times。
    @GetMapping
    public ApiResponse<List<WorkTimeApplyVO>> listWorkTimes() {
        return ApiResponse.success(workTimeApplyService.listWorkTimes());
    }

    // 根据工时编号查询单条工时申报单，对应 GET /api/work-times/{workId}。
    @GetMapping("/{workId}")
    public ApiResponse<WorkTimeApplyVO> getWorkTimeById(@PathVariable Integer workId) {
        return ApiResponse.success(workTimeApplyService.getWorkTimeById(workId));
    }

    // 根据用户编号查询该用户的工时申报单，对应 GET /api/work-times/users/{userId}。
    @GetMapping("/users/{userId}")
    public ApiResponse<List<WorkTimeApplyVO>> listWorkTimesByUserId(@PathVariable Integer userId) {
        return ApiResponse.success(workTimeApplyService.listWorkTimesByUserId(userId));
    }

    // 根据部门经理编号查询其本部门待审批工时，对应 GET /api/work-times/pending/managers/{managerId}。
    @GetMapping("/pending/managers/{managerId}")
    public ApiResponse<List<WorkTimeApplyVO>> listPendingWorkTimesByManagerId(@PathVariable Integer managerId) {
        return ApiResponse.success(workTimeApplyService.listPendingWorkTimesByManagerId(managerId));
    }

    // 新建工时草稿，对应 POST /api/work-times。
    @PostMapping
    public ApiResponse<WorkTimeApplyVO> createWorkTime(@Valid @RequestBody WorkTimeCreateDTO createDTO) {
        return ApiResponse.success(workTimeApplyService.createWorkTime(createDTO));
    }

    // 修改工时草稿或已驳回工时，对应 PUT /api/work-times/{workId}。
    @PutMapping("/{workId}")
    public ApiResponse<WorkTimeApplyVO> updateWorkTime(
            @PathVariable Integer workId,
            @Valid @RequestBody WorkTimeUpdateDTO updateDTO) {
        return ApiResponse.success(workTimeApplyService.updateWorkTime(workId, updateDTO));
    }

    // 提交工时审批，对应 POST /api/work-times/{workId}/submit。
    @PostMapping("/{workId}/submit")
    public ApiResponse<WorkTimeApplyVO> submitWorkTime(@PathVariable Integer workId) {
        return ApiResponse.success(workTimeApplyService.submitWorkTime(workId));
    }

    // 删除工时草稿或已驳回工时，对应 DELETE /api/work-times/{workId}?userId=员工编号。
    @DeleteMapping("/{workId}")
    public ApiResponse<Void> deleteWorkTime(
            @PathVariable Integer workId,
            @RequestParam Integer userId) {
        workTimeApplyService.deleteWorkTime(workId, userId);
        return ApiResponse.success();
    }

    // 部门经理审批通过工时，对应 POST /api/work-times/{workId}/approve。
    @PostMapping("/{workId}/approve")
    public ApiResponse<WorkTimeApplyVO> approveWorkTime(
            @PathVariable Integer workId,
            @Valid @RequestBody WorkTimeApproveDTO approveDTO) {
        return ApiResponse.success(workTimeApplyService.approveWorkTime(workId, approveDTO));
    }

    // 部门经理驳回工时，对应 POST /api/work-times/{workId}/reject。
    @PostMapping("/{workId}/reject")
    public ApiResponse<WorkTimeApplyVO> rejectWorkTime(
            @PathVariable Integer workId,
            @Valid @RequestBody WorkTimeRejectDTO rejectDTO) {
        return ApiResponse.success(workTimeApplyService.rejectWorkTime(workId, rejectDTO));
    }
}
