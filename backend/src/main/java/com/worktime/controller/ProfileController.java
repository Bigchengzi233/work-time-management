package com.worktime.controller;

import com.worktime.common.ApiResponse;
import com.worktime.dto.PasswordUpdateDTO;
import com.worktime.dto.ProfileUpdateDTO;
import com.worktime.service.ProfileService;
import com.worktime.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 个人中心接口：负责当前登录用户查看和维护本人信息。
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    // 个人中心业务对象。
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // 查询当前登录用户个人信息，对应 GET /api/profile。
    @GetMapping
    public ApiResponse<UserVO> getProfile() {
        return ApiResponse.success(profileService.getProfile());
    }

    // 修改当前登录用户个人信息，对应 PUT /api/profile。
    @PutMapping
    public ApiResponse<UserVO> updateProfile(@Valid @RequestBody ProfileUpdateDTO updateDTO) {
        return ApiResponse.success(profileService.updateProfile(updateDTO));
    }

    // 修改当前登录用户密码，对应 PUT /api/profile/password。
    @PutMapping("/password")
    public ApiResponse<Void> updatePassword(@Valid @RequestBody PasswordUpdateDTO updateDTO) {
        profileService.updatePassword(updateDTO);
        return ApiResponse.success();
    }
}
