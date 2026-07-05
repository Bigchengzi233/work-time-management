package com.worktime.mapper;

import com.worktime.entity.UserProject;
import com.worktime.vo.UserProjectRowVO;
import com.worktime.vo.UserProjectRuleVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 用户项目授权数据访问接口：定义 user_project 表相关数据库操作。
public interface UserProjectMapper {

    // 查询全部授权记录。
    List<UserProjectRowVO> selectAll();

    // 根据授权编号查询授权记录。
    UserProjectRowVO selectById(Integer authId);

    // 根据用户编号查询该用户的全部授权记录。
    List<UserProjectRowVO> selectByUserId(Integer userId);

    // 根据用户和项目查询授权规则所需信息。
    UserProjectRuleVO selectRuleInfo(@Param("userId") Integer userId, @Param("projectId") Integer projectId);

    // 根据用户编号统计用户是否存在。
    int countUserById(Integer userId);

    // 根据项目编号统计项目是否存在。
    int countProjectById(Integer projectId);

    // 统计同一用户和同一项目是否已经存在授权记录。
    int countByUserIdAndProjectId(@Param("userId") Integer userId, @Param("projectId") Integer projectId);

    // 新增授权记录。
    int insert(UserProject userProject);

    // 修改授权状态。
    int updateStatusById(@Param("authId") Integer authId, @Param("authStatus") Integer authStatus);
}
