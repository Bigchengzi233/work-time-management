package com.worktime.service;

import com.worktime.vo.CaptchaVO;

// 登录验证码业务接口：负责生成验证码和校验用户输入。
public interface CaptchaService {

    // 生成新的登录验证码。
    CaptchaVO createCaptcha();

    // 校验登录验证码，校验后验证码立即失效。
    void validateCaptcha(String captchaId, String captchaCode);
}
