package com.worktime.controller;

import com.worktime.common.ApiResponse;
import com.worktime.dto.DepartmentCreateDTO;
import com.worktime.dto.DepartmentUpdateDTO;
import com.worktime.service.DepartmentService;
import com.worktime.vo.DepartmentVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    // 根据部门编号查询单个部门，对应 GET /api/departments/{deptId}。
    @GetMapping("/{deptId}")
    public ApiResponse<DepartmentVO> getDepartmentById(@PathVariable Integer deptId) {
        // 从路径中接收 deptId，然后交给业务层查询部门详情。
        return ApiResponse.success(departmentService.getDepartmentById(deptId));
    }

    // 新增部门接口，对应 POST /api/departments。
    @PostMapping
    public ApiResponse<DepartmentVO> createDepartment(@Valid @RequestBody DepartmentCreateDTO createDTO) {
        // @RequestBody 表示从请求体 JSON 中读取数据，@Valid 表示启用参数校验。
        return ApiResponse.success(departmentService.createDepartment(createDTO));
    }

    // 修改部门接口，对应 PUT /api/departments/{deptId}。
    @PutMapping("/{deptId}")
    public ApiResponse<DepartmentVO> updateDepartment(
            @PathVariable Integer deptId,
            @Valid @RequestBody DepartmentUpdateDTO updateDTO) {
        // 路径中的 deptId 决定修改哪条部门记录，请求体中的 deptName 决定修改后的名称。
        return ApiResponse.success(departmentService.updateDepartment(deptId, updateDTO));
    }

    // 删除部门接口，对应 DELETE /api/departments/{deptId}。
    @DeleteMapping("/{deptId}")
    public ApiResponse<Void> deleteDepartment(@PathVariable Integer deptId) {
        // 删除前会在业务层检查该部门下是否存在用户或项目。
        departmentService.deleteDepartment(deptId);
        return ApiResponse.success();
    }
}
