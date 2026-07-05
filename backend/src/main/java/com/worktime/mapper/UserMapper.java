package com.worktime.mapper;

import com.worktime.entity.User;
import com.worktime.vo.UserRowVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

// 用户数据访问接口：定义 user 表相关数据库操作。
public interface UserMapper {

    // 查询全部用户，并带出部门名称。
    List<UserRowVO> selectAll();

    // 根据用户编号查询用户，并带出部门名称。
    UserRowVO selectById(Integer userId);

    // 根据手机号查询用户，并带出部门名称。
    UserRowVO selectByPhone(String phone);

    // 根据手机号统计用户数量，用于新增时检查手机号唯一。
    int countByPhone(String phone);

    // 根据手机号统计其他用户数量，用于修改时检查手机号唯一。
    int countByPhoneExcludeId(@Param("phone") String phone, @Param("userId") Integer userId);

    // 根据部门编号统计部门数量，用于检查部门是否存在。
    int countDepartmentById(Integer deptId);

    // 新增用户。
    int insert(User user);

    // 根据用户编号修改用户。
    int updateById(User user);

    // 根据用户编号更新密码。
    int updatePasswordById(@Param("userId") Integer userId, @Param("psw") String psw);

    // 统计用户是否存在工时申报单。
    int countWorkTimesByUserId(Integer userId);

    // 统计用户是否作为操作人存在工时日志。
    int countLogsByOperatorId(Integer userId);

    // 删除用户项授权记录。
    int deleteUserProjectsByUserId(Integer userId);

    // 根据用户编号删除用户。
    int deleteById(Integer userId);
}
