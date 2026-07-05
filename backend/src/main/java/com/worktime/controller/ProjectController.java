package com.worktime.controller;

import com.worktime.common.ApiResponse;
import com.worktime.dto.ProjectCreateDTO;
import com.worktime.dto.ProjectUpdateDTO;
import com.worktime.service.ProjectService;
import com.worktime.vo.ProjectVO;
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

// 项目管理接口：负责接收项目相关的前端请求。
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    // 项目业务对象。
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // 查询全部项目，对应 GET /api/projects。
    @GetMapping
    public ApiResponse<List<ProjectVO>> listProjects() {
        return ApiResponse.success(projectService.listProjects());
    }

    // 根据项目编号查询单个项目，对应 GET /api/projects/{projectId}。
    @GetMapping("/{projectId}")
    public ApiResponse<ProjectVO> getProjectById(@PathVariable Integer projectId) {
        return ApiResponse.success(projectService.getProjectById(projectId));
    }

    // 新增项目，对应 POST /api/projects。
    @PostMapping
    public ApiResponse<ProjectVO> createProject(@Valid @RequestBody ProjectCreateDTO createDTO) {
        return ApiResponse.success(projectService.createProject(createDTO));
    }

    // 修改项目，对应 PUT /api/projects/{projectId}。
    @PutMapping("/{projectId}")
    public ApiResponse<ProjectVO> updateProject(
            @PathVariable Integer projectId,
            @Valid @RequestBody ProjectUpdateDTO updateDTO) {
        return ApiResponse.success(projectService.updateProject(projectId, updateDTO));
    }

    // 删除项目，对应 DELETE /api/projects/{projectId}。
    @DeleteMapping("/{projectId}")
    public ApiResponse<Void> deleteProject(@PathVariable Integer projectId) {
        projectService.deleteProject(projectId);
        return ApiResponse.success();
    }
}
