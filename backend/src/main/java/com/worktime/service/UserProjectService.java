package com.worktime.service;

import com.worktime.dto.UserProjectCreateDTO;
import com.worktime.dto.UserProjectUpdateDTO;
import com.worktime.vo.UserProjectVO;

import java.util.List;

// 用户项目授权业务接口：定义员工项目分配模块对外提供的业务能力。
public interface UserProjectService {

    // 查询全部授权记录。
    List<UserProjectVO> listUserProjects();

    // 根据授权编号查询单条授权记录。
    UserProjectVO getUserProjectById(Integer authId);

    // 根据用户编号查询该用户的授权项目。
    List<UserProjectVO> listUserProjectsByUserId(Integer userId);

    // 新增授权记录。
    UserProjectVO createUserProject(UserProjectCreateDTO createDTO);

    // 修改授权状态。
    UserProjectVO updateUserProject(Integer authId, UserProjectUpdateDTO updateDTO);

    // 取消授权。
    void cancelUserProject(Integer authId);
}
