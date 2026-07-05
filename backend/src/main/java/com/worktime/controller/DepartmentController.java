package com.worktime.controller;

import com.worktime.common.ApiResponse;
import com.worktime.service.DepartmentService;
import com.worktime.vo.DepartmentVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 部门管理接口：负责接收部门相关的前端请求。
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    // 注入部门业务层对象，Controller 只负责调用业务，不直接写 SQL。
    private final DepartmentService departmentService;

    // 构造方法注入：Spring 会自动把 DepartmentService 对象传进来。
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // 查询全部部门接口，对应 GET /api/departments。
    @GetMapping
    public ApiResponse<List<DepartmentVO>> listDepartments() {
        // 调用业务层查询部门列表，并用统一响应格式返回给前端。
        return ApiResponse.success(departmentService.listDepartments());
    }
}
