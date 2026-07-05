package com.worktime.controller;

import com.worktime.common.ApiResponse;
import com.worktime.dto.UserProjectCreateDTO;
import com.worktime.dto.UserProjectUpdateDTO;
import com.worktime.service.UserProjectService;
import com.worktime.vo.UserProjectVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 用户项目授权接口：负责员工项目分配相关请求。
@RestController
@RequestMapping("/api/user-projects")
public class UserProjectController {

    // 用户项目授权业务对象。
    private final UserProjectService userProjectService;

    public UserProjectController(UserProjectService userProjectService) {
        this.userProjectService = userProjectService;
    }

    // 查询全部授权记录，对应 GET /api/user-projects。
    @GetMapping
    public ApiResponse<List<UserProjectVO>> listUserProjects() {
        return ApiResponse.success(userProjectService.listUserProjects());
    }

    // 根据授权编号查询授权记录，对应 GET /api/user-projects/{authId}。
    @GetMapping("/{authId}")
    public ApiResponse<UserProjectVO> getUserProjectById(@PathVariable Integer authId) {
        return ApiResponse.success(userProjectService.getUserProjectById(authId));
    }

    // 根据用户编号查询授权项目，对应 GET /api/user-projects/users/{userId}。
    @GetMapping("/users/{userId}")
    public ApiResponse<List<UserProjectVO>> listUserProjectsByUserId(@PathVariable Integer userId) {
        return ApiResponse.success(userProjectService.listUserProjectsByUserId(userId));
    }

    // 新增授权记录，对应 POST /api/user-projects。
    @PostMapping
    public ApiResponse<UserProjectVO> createUserProject(@Valid @RequestBody UserProjectCreateDTO createDTO) {
        return ApiResponse.success(userProjectService.createUserProject(createDTO));
    }

    // 修改授权状态，对应 PUT /api/user-projects/{authId}。
    @PutMapping("/{authId}")
    public ApiResponse<UserProjectVO> updateUserProject(
            @PathVariable Integer authId,
            @Valid @RequestBody UserProjectUpdateDTO updateDTO) {
        return ApiResponse.success(userProjectService.updateUserProject(authId, updateDTO));
    }

    // 取消授权，对应 DELETE /api/user-projects/{authId}，这里执行逻辑取消而不是物理删除。
    @DeleteMapping("/{authId}")
    public ApiResponse<Void> cancelUserProject(@PathVariable Integer authId) {
        userProjectService.cancelUserProject(authId);
        return ApiResponse.success();
    }
}
