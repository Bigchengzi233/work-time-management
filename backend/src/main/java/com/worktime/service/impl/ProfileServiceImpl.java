package com.worktime.service.impl;

import com.worktime.common.AuthUtil;
import com.worktime.common.CurrentUser;
import com.worktime.dto.PasswordUpdateDTO;
import com.worktime.dto.ProfileUpdateDTO;
import com.worktime.exception.BusinessException;
import com.worktime.mapper.UserMapper;
import com.worktime.service.ProfileService;
import com.worktime.vo.UserRowVO;
import com.worktime.vo.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// 个人中心业务实现类：处理当前登录用户个人资料和密码修改。
@Service
public class ProfileServiceImpl implements ProfileService {

    // 用户数据访问对象。
    private final UserMapper userMapper;

    // 密码加密和校验工具。
    private final PasswordEncoder passwordEncoder;

    public ProfileServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // 查询当前登录用户个人信息。
    @Override
    public UserVO getProfile() {
        CurrentUser currentUser = AuthUtil.currentUser();
        UserRowVO user = getExistingUser(currentUser.getUserId());
        return UserVO.fromEntity(user, user.getDeptName());
    }

    // 修改当前登录用户个人信息。
    @Override
    public UserVO updateProfile(ProfileUpdateDTO updateDTO) {
        CurrentUser currentUser = AuthUtil.currentUser();
        UserRowVO oldUser = getExistingUser(currentUser.getUserId());

        userMapper.updateProfileById(
                oldUser.getUserId(),
                updateDTO.getUserName().trim(),
                normalizeEmail(updateDTO.getEmail()));

        UserRowVO updatedUser = getExistingUser(oldUser.getUserId());
        return UserVO.fromEntity(updatedUser, updatedUser.getDeptName());
    }

    // 修改当前登录用户密码。
    @Override
    public void updatePassword(PasswordUpdateDTO updateDTO) {
        CurrentUser currentUser = AuthUtil.currentUser();
        UserRowVO user = getExistingUser(currentUser.getUserId());

        if (!matchesPassword(updateDTO.getOldPassword(), user.getPsw())) {
            throw new BusinessException(400, "原密码错误");
        }

        if (updateDTO.getOldPassword().equals(updateDTO.getNewPassword())) {
            throw new BusinessException(400, "新密码不能和原密码相同");
        }

        userMapper.updatePasswordById(user.getUserId(), passwordEncoder.encode(updateDTO.getNewPassword()));
    }

    // 根据用户编号查询用户，查询不到时返回业务错误。
    private UserRowVO getExistingUser(Integer userId) {
        UserRowVO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    // 校验输入密码和数据库密码是否匹配，兼容初始化数据中的旧明文密码。
    private boolean matchesPassword(String rawPassword, String storedPassword) {
        if (isBcryptPassword(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return rawPassword.equals(storedPassword);
    }

    // 判断数据库中保存的密码是否已经是 BCrypt 哈希。
    private boolean isBcryptPassword(String storedPassword) {
        return storedPassword != null
                && (storedPassword.startsWith("$2a$")
                || storedPassword.startsWith("$2b$")
                || storedPassword.startsWith("$2y$"));
    }

    // 邮箱允许为空字符串，入库前统一转成 null。
    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim();
    }
}
