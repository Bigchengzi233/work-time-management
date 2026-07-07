package com.worktime.service.impl;

import com.worktime.common.RoleConstants;
import com.worktime.dto.UserCreateDTO;
import com.worktime.dto.UserUpdateDTO;
import com.worktime.entity.User;
import com.worktime.exception.BusinessException;
import com.worktime.mapper.UserMapper;
import com.worktime.service.UserService;
import com.worktime.vo.UserRowVO;
import com.worktime.vo.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 用户业务实现类：处理用户管理的业务规则。
@Service
public class UserServiceImpl implements UserService {

    // 用户数据访问对象。
    private final UserMapper userMapper;

    // 密码加密工具。
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // 查询全部用户。
    @Override
    public List<UserVO> listUsers() {
        return userMapper.selectAll().stream()
                .map(user -> UserVO.fromEntity(user, user.getDeptName()))
                .toList();
    }

    // 根据用户编号查询单个用户。
    @Override
    public UserVO getUserById(Integer userId) {
        UserRowVO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return UserVO.fromEntity(user, user.getDeptName());
    }

    // 新增用户。
    @Override
    public UserVO createUser(UserCreateDTO createDTO) {
        String userName = createDTO.getUserName().trim();
        String phone = createDTO.getPhone().trim();
        String email = normalizeEmail(createDTO.getEmail());

        if (RoleConstants.ADMIN.equals(createDTO.getUserRole())) {
            throw new BusinessException(400, "系统只能保留一个管理员，不能新增管理员账号");
        }

        if (userMapper.countByPhone(phone) > 0) {
            throw new BusinessException(400, "手机号已存在");
        }

        if (userMapper.countDepartmentById(createDTO.getDeptId()) == 0) {
            throw new BusinessException(404, "所属部门不存在");
        }

        User user = new User();
        user.setUserName(userName);
        user.setPhone(phone);
        user.setPsw(passwordEncoder.encode(createDTO.getPassword()));
        user.setEmail(email);
        user.setUserRole(createDTO.getUserRole());
        user.setDeptId(createDTO.getDeptId());

        userMapper.insert(user);
        return getUserById(user.getUserId());
    }

    // 修改用户。
    @Override
    public UserVO updateUser(Integer userId, UserUpdateDTO updateDTO) {
        UserRowVO oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(404, "用户不存在");
        }

        String userName = updateDTO.getUserName().trim();
        String phone = updateDTO.getPhone().trim();
        String email = normalizeEmail(updateDTO.getEmail());

        if (!RoleConstants.ADMIN.equals(oldUser.getUserRole())
                && RoleConstants.ADMIN.equals(updateDTO.getUserRole())) {
            throw new BusinessException(400, "系统只能保留一个管理员，不能把用户修改为管理员");
        }

        if (RoleConstants.ADMIN.equals(oldUser.getUserRole())
                && !RoleConstants.ADMIN.equals(updateDTO.getUserRole())) {
            throw new BusinessException(400, "系统必须保留唯一管理员，不能修改管理员角色");
        }

        if (userMapper.countByPhoneExcludeId(phone, userId) > 0) {
            throw new BusinessException(400, "手机号已存在");
        }

        if (userMapper.countDepartmentById(updateDTO.getDeptId()) == 0) {
            throw new BusinessException(404, "所属部门不存在");
        }

        User user = new User();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setPhone(phone);
        user.setEmail(email);
        user.setUserRole(updateDTO.getUserRole());
        user.setDeptId(updateDTO.getDeptId());

        if (updateDTO.getPassword() == null || updateDTO.getPassword().isBlank()) {
            user.setPsw(oldUser.getPsw());
        } else {
            user.setPsw(passwordEncoder.encode(updateDTO.getPassword()));
        }

        userMapper.updateById(user);
        return getUserById(userId);
    }

    // 删除用户。
    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        UserRowVO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        if (userMapper.countWorkTimesByUserId(userId) > 0) {
            throw new BusinessException(400, "该用户存在工时申报数据，不能删除");
        }

        if (userMapper.countLogsByOperatorId(userId) > 0) {
            throw new BusinessException(400, "该用户存在工时操作日志，不能删除");
        }

        userMapper.deleteUserProjectsByUserId(userId);
        userMapper.deleteById(userId);
    }

    // 邮箱允许为空字符串，入库前统一转成 null。
    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim();
    }
}
