package com.worktime.vo;

// 登录验证码返回对象：返回验证码编号和验证码图片。
public class CaptchaVO {

    // 验证码编号，登录时前端需要原样带回。
    private String captchaId;

    // 验证码图片，使用 base64 data URL，前端可以直接放进 img 的 src。
    private String captchaImage;

    public String getCaptchaId() {
        return captchaId;
    }

    public void setCaptchaId(String captchaId) {
        this.captchaId = captchaId;
    }

    public String getCaptchaImage() {
        return captchaImage;
    }

    public void setCaptchaImage(String captchaImage) {
        this.captchaImage = captchaImage;
    }
}
