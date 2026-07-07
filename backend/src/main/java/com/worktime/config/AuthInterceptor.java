package com.worktime.config;

import com.worktime.common.AuthContext;
import com.worktime.common.CurrentUser;
import com.worktime.common.TokenPayload;
import com.worktime.common.TokenUtil;
import com.worktime.service.UserEmploymentStatusService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// 登录拦截器：校验除登录和健康检查之外的接口是否携带有效 token。
@Component
public class AuthInterceptor implements HandlerInterceptor {

    // token 工具对象。
    private final TokenUtil tokenUtil;

    // 用户在职状态业务对象，用来让离职用户的旧 token 立即失效。
    private final UserEmploymentStatusService userEmploymentStatusService;

    public AuthInterceptor(TokenUtil tokenUtil, UserEmploymentStatusService userEmploymentStatusService) {
        this.tokenUtil = tokenUtil;
        this.userEmploymentStatusService = userEmploymentStatusService;
    }

    // 请求进入 Controller 前执行。
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            writeUnauthorized(response, "请先登录");
            return false;
        }

        String token = authorization.substring("Bearer ".length()).trim();
        TokenPayload tokenPayload = tokenUtil.parseToken(token);
        if (tokenPayload == null) {
            writeUnauthorized(response, "登录状态已失效，请重新登录");
            return false;
        }

        if (userEmploymentStatusService.isInactive(tokenPayload.getUserId())) {
            writeUnauthorized(response, "该账号已离职，禁止登录系统");
            return false;
        }

        AuthContext.setCurrentUser(new CurrentUser(tokenPayload.getUserId(), tokenPayload.getUserRole(), tokenPayload.getDeptId()));
        return true;
    }

    // 请求结束后清理当前登录用户。
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        AuthContext.clear();
    }

    // 返回未登录 JSON。
    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"" + message + "\",\"data\":null}");
    }
}
