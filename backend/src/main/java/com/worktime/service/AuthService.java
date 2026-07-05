package com.worktime.service;

import com.worktime.dto.LoginDTO;
import com.worktime.vo.LoginVO;
import com.worktime.vo.UserVO;

// 登录业务接口：定义认证模块对外提供的业务能力。
public interface AuthService {

    // 用户登录。
    LoginVO login(LoginDTO loginDTO);

    // 查询当前登录用户信息。
    UserVO getCurrentUser(Integer userId);
}
