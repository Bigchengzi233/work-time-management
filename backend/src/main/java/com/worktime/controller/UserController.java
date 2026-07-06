package com.worktime.controller;

import com.worktime.common.ApiResponse;
import com.worktime.common.AuthUtil;
import com.worktime.common.CurrentUser;
import com.worktime.common.RoleConstants;
import com.worktime.dto.UserCreateDTO;
import com.worktime.dto.UserUpdateDTO;
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

// 用户管理接口：负责管理员维护用户信息。
@RestController
@RequestMapping("/api/users")
public class UserController {

    // 用户业务对象。
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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

    // 删除用户，对应 DELETE /api/users/{userId}。
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable Integer userId) {
        AuthUtil.requireAdmin();
        userService.deleteUser(userId);
        return ApiResponse.success();
    }
}
