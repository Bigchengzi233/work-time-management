package com.worktime.common;

// 登录上下文：在一次请求中保存当前登录用户信息。
public class AuthContext {

    // ThreadLocal 可以让每个请求线程保存自己的登录用户，互不影响。
    private static final ThreadLocal<CurrentUser> CURRENT_USER = new ThreadLocal<>();

    // 设置当前登录用户。
    public static void setCurrentUser(CurrentUser currentUser) {
        CURRENT_USER.set(currentUser);
    }

    // 获取当前登录用户。
    public static CurrentUser getCurrentUser() {
        return CURRENT_USER.get();
    }

    // 请求结束后清理当前线程中的用户信息，避免线程复用时串数据。
    public static void clear() {
        CURRENT_USER.remove();
    }
}
