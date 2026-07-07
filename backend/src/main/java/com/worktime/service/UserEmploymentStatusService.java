package com.worktime.service;

import java.util.Map;

// 用户在职状态业务接口：使用后端本地文件保存状态，不修改数据库结构。
public interface UserEmploymentStatusService {

    // 查询全部用户在职状态映射，key 为用户编号，value 为 active 或 inactive。
    Map<String, String> listEmploymentStatuses();

    // 修改指定用户的在职状态。
    void updateEmploymentStatus(Integer userId, String employmentStatus);

    // 判断指定用户当前是否为离职状态。
    boolean isInactive(Integer userId);
}
