package com.worktime.common;

import com.worktime.exception.BusinessException;

// 权限工具类：统一处理当前登录用户和角色校验。
public class AuthUtil {

    private AuthUtil() {
    }

    // 获取当前登录用户；如果不存在，说明登录态异常。
    public static CurrentUser currentUser() {
        CurrentUser currentUser = AuthContext.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(401, "请先登录");
        }
        return currentUser;
    }

    // 校验当前用户必须是管理员。
    public static CurrentUser requireAdmin() {
        CurrentUser currentUser = currentUser();
        if (!RoleConstants.ADMIN.equals(currentUser.getUserRole())) {
            throw new BusinessException(403, "只有管理员可以执行该操作");
        }
        return currentUser;
    }

    // 校验当前用户必须是部门经理。
    public static CurrentUser requireManager() {
        CurrentUser currentUser = currentUser();
        if (!RoleConstants.MANAGER.equals(currentUser.getUserRole())) {
            throw new BusinessException(403, "只有部门经理可以执行该操作");
        }
        return currentUser;
    }

    // 校验当前用户必须是管理员或部门经理。
    public static CurrentUser requireAdminOrManager() {
        CurrentUser currentUser = currentUser();
        if (!RoleConstants.ADMIN.equals(currentUser.getUserRole())
                && !RoleConstants.MANAGER.equals(currentUser.getUserRole())) {
            throw new BusinessException(403, "只有管理员或部门经理可以执行该操作");
        }
        return currentUser;
    }

    // 校验当前用户必须是普通员工。
    public static CurrentUser requireEmployee() {
        CurrentUser currentUser = currentUser();
        if (!RoleConstants.EMPLOYEE.equals(currentUser.getUserRole())) {
            throw new BusinessException(403, "只有员工可以执行该操作");
        }
        return currentUser;
    }

    // 校验当前登录用户编号必须等于传入用户编号。
    public static CurrentUser requireSelf(Integer userId) {
        CurrentUser currentUser = currentUser();
        if (!currentUser.getUserId().equals(userId)) {
            throw new BusinessException(403, "只能操作本人数据");
        }
        return currentUser;
    }
}
