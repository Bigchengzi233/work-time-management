package com.worktime.service;

import com.worktime.dto.UserCreateDTO;
import com.worktime.dto.UserUpdateDTO;
import com.worktime.vo.UserVO;

import java.util.List;

// 用户业务接口：定义用户管理模块对外提供的业务能力。
public interface UserService {

    // 查询全部用户。
    List<UserVO> listUsers();

    // 根据用户编号查询单个用户。
    UserVO getUserById(Integer userId);

    // 新增用户。
    UserVO createUser(UserCreateDTO createDTO);

    // 修改用户。
    UserVO updateUser(Integer userId, UserUpdateDTO updateDTO);

    // 删除用户。
    void deleteUser(Integer userId);
}
