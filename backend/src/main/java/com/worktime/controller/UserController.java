package com.worktime.controller;

import com.worktime.common.ApiResponse;
import com.worktime.common.AuthUtil;
import com.worktime.common.CurrentUser;
import com.worktime.common.RoleConstants;
import com.worktime.dto.UserCreateDTO;
import com.worktime.dto.UserEmploymentStatusUpdateDTO;
import com.worktime.dto.UserUpdateDTO;
import com.worktime.service.UserEmploymentStatusService;
import com.worktime.service.UserService;
import com.worktime.vo.UserVO;
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
import java.util.Map;

// 用户管理接口：负责管理员维护用户信息。
@RestController
@RequestMapping("/api/users")
public class UserController {

    // 用户业务对象。
    private final UserService userService;

    // 用户在职状态业务对象。
    private final UserEmploymentStatusService userEmploymentStatusService;

    public UserController(UserService userService, UserEmploymentStatusService userEmploymentStatusService) {
        this.userService = userService;
        this.userEmploymentStatusService = userEmploymentStatusService;
    }

    // 查询全部用户，对应 GET /api/users。
    @GetMapping
    public ApiResponse<List<UserVO>> listUsers() {
        CurrentUser currentUser = AuthUtil.requireAdminOrManager();
        List<UserVO> users = userService.listUsers();

        if (RoleConstants.MANAGER.equals(currentUser.getUserRole())) {
            users = users.stream()
                    .filter(user -> currentUser.getDeptId().equals(user.getDeptId()))
                    .filter(user -> RoleConstants.EMPLOYEE.equals(user.getUserRole()))
                    .toList();
        }

        return ApiResponse.success(users);
    }

    // 查询全部用户在职状态，对应 GET /api/users/employment-statuses。
    @GetMapping("/employment-statuses")
    public ApiResponse<Map<String, String>> listEmploymentStatuses() {
        AuthUtil.requireAdmin();
        return ApiResponse.success(userEmploymentStatusService.listEmploymentStatuses());
    }

    // 根据用户编号查询单个用户，对应 GET /api/users/{userId}。
    @GetMapping("/{userId}")
    public ApiResponse<UserVO> getUserById(@PathVariable Integer userId) {
        AuthUtil.requireAdmin();
        return ApiResponse.success(userService.getUserById(userId));
    }

    // 新增用户，对应 POST /api/users。
    @PostMapping
    public ApiResponse<UserVO> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        AuthUtil.requireAdmin();
        return ApiResponse.success(userService.createUser(createDTO));
    }

    // 修改用户，对应 PUT /api/users/{userId}。
    @PutMapping("/{userId}")
    public ApiResponse<UserVO> updateUser(
            @PathVariable Integer userId,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        AuthUtil.requireAdmin();
        return ApiResponse.success(userService.updateUser(userId, updateDTO));
    }

    // 修改用户在职状态，对应 PUT /api/users/{userId}/employment-status。
    @PutMapping("/{userId}/employment-status")
    public ApiResponse<Void> updateEmploymentStatus(
            @PathVariable Integer userId,
            @Valid @RequestBody UserEmploymentStatusUpdateDTO updateDTO) {
        AuthUtil.requireAdmin();
        userEmploymentStatusService.updateEmploymentStatus(userId, updateDTO.getEmploymentStatus());
        return ApiResponse.success();
    }

    // 重置用户密码，对应 PUT /api/users/{userId}/reset-password。
    @PutMapping("/{userId}/reset-password")
    public ApiResponse<Void> resetPassword(@PathVariable Integer userId) {
        AuthUtil.requireAdmin();
        userService.resetPassword(userId);
        return ApiResponse.success();
    }

    // 删除用户，对应 DELETE /api/users/{userId}。
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable Integer userId) {
        AuthUtil.requireAdmin();
        userService.deleteUser(userId);
        return ApiResponse.success();
    }
}
