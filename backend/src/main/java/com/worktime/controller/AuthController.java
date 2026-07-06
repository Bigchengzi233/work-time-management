package com.worktime.controller;

import com.worktime.common.ApiResponse;
import com.worktime.common.AuthContext;
import com.worktime.common.CurrentUser;
import com.worktime.dto.LoginDTO;
import com.worktime.service.AuthService;
import com.worktime.service.CaptchaService;
import com.worktime.vo.CaptchaVO;
import com.worktime.vo.LoginVO;
import com.worktime.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 登录接口：负责用户登录认证。
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // 登录业务对象。
    private final AuthService authService;

    // 验证码业务对象。
    private final CaptchaService captchaService;

    public AuthController(AuthService authService, CaptchaService captchaService) {
        this.authService = authService;
        this.captchaService = captchaService;
    }

    // 获取登录验证码，对应 GET /api/auth/captcha。
    @GetMapping("/captcha")
    public ApiResponse<CaptchaVO> getCaptcha() {
        return ApiResponse.success(captchaService.createCaptcha());
    }

    // 用户登录，对应 POST /api/auth/login。
    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return ApiResponse.success(authService.login(loginDTO));
    }

    // 查询当前登录用户，对应 GET /api/auth/me。
    @GetMapping("/me")
    public ApiResponse<UserVO> getCurrentUser() {
        CurrentUser currentUser = AuthContext.getCurrentUser();
        return ApiResponse.success(authService.getCurrentUser(currentUser.getUserId()));
    }
}
