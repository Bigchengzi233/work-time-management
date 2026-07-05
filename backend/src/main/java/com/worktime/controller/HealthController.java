package com.worktime.controller;

import com.worktime.common.ApiResponse;
import com.worktime.exception.BusinessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 健康检查接口：用于确认后端服务是否已经正常启动。
@RestController
// 这个 Controller 下所有接口都以 /api/health 开头。
@RequestMapping("/api/health")
public class HealthController {

    // 浏览器访问 GET /api/health/ping 时，会进入这个方法。
    @GetMapping("/ping")
    public ApiResponse<String> ping() {
        // 返回统一格式，说明后端服务运行正常。
        return ApiResponse.success("员工工时管理系统后端启动成功");
    }

    //测试错误接口
    @GetMapping("/fail")
    public ApiResponse<String> fail(){
        return ApiResponse.fail(400,"后端服务异常");
    }

    @GetMapping("/exception")
    public ApiResponse<String> exception(){
        throw new BusinessException(400,"这是一个业务异常测试");
    }
}
