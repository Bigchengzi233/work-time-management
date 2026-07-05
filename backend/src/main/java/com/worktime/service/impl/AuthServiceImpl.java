package com.worktime.service.impl;

import com.worktime.dto.LoginDTO;
import com.worktime.exception.BusinessException;
import com.worktime.mapper.UserMapper;
import com.worktime.service.AuthService;
import com.worktime.vo.LoginVO;
import com.worktime.vo.UserRowVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

// 登录业务实现类：处理手机号密码校验和登录返回数据组装。
@Service
public class AuthServiceImpl implements AuthService {

    // 用户数据访问对象。
    private final UserMapper userMapper;

    // 密码加密和校验工具。
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // 用户登录。
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        String phone = loginDTO.getPhone().trim();
        String password = loginDTO.getPassword();

        UserRowVO user = userMapper.selectByPhone(phone);
        if (user == null) {
            throw new BusinessException(401, "手机号或密码错误");
        }

        if (!matchesPassword(password, user.getPsw())) {
            throw new BusinessException(401, "手机号或密码错误");
        }

        upgradePlainPasswordIfNeeded(user, password);
        return buildLoginVO(user);
    }

    // 校验输入密码和数据库密码是否匹配。
    private boolean matchesPassword(String rawPassword, String storedPassword) {
        if (isBcryptPassword(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return rawPassword.equals(storedPassword);
    }

    // 如果数据库里还是旧的明文密码，登录成功后自动升级成 BCrypt 哈希。
    private void upgradePlainPasswordIfNeeded(UserRowVO user, String rawPassword) {
        if (!isBcryptPassword(user.getPsw())) {
            userMapper.updatePasswordById(user.getUserId(), passwordEncoder.encode(rawPassword));
        }
    }

    // 判断数据库中保存的密码是否已经是 BCrypt 哈希。
    private boolean isBcryptPassword(String storedPassword) {
        return storedPassword != null
                && (storedPassword.startsWith("$2a$")
                || storedPassword.startsWith("$2b$")
                || storedPassword.startsWith("$2y$"));
    }

    // 组装登录返回对象。
    private LoginVO buildLoginVO(UserRowVO user) {
        LoginVO loginVO = new LoginVO();
        loginVO.setToken("TEMP-" + UUID.randomUUID());
        loginVO.setUserId(user.getUserId());
        loginVO.setUserName(user.getUserName());
        loginVO.setPhone(user.getPhone());
        loginVO.setUserRole(user.getUserRole());
        loginVO.setDeptId(user.getDeptId());
        loginVO.setDeptName(user.getDeptName());
        return loginVO;
    }
}
