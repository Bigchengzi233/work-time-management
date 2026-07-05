package com.worktime.service;

import com.worktime.dto.PasswordUpdateDTO;
import com.worktime.dto.ProfileUpdateDTO;
import com.worktime.vo.UserVO;

// 个人中心业务接口：定义当前登录用户个人信息维护能力。
public interface ProfileService {

    // 查询当前登录用户个人信息。
    UserVO getProfile();

    // 修改当前登录用户个人信息。
    UserVO updateProfile(ProfileUpdateDTO updateDTO);

    // 修改当前登录用户密码。
    void updatePassword(PasswordUpdateDTO updateDTO);
}
