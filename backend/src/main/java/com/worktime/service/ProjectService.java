package com.worktime.service;

import com.worktime.dto.ProjectCreateDTO;
import com.worktime.dto.ProjectUpdateDTO;
import com.worktime.vo.ProjectVO;

import java.util.List;

// 项目业务接口：定义项目管理模块对外提供的业务能力。
public interface ProjectService {

    // 查询全部项目。
    List<ProjectVO> listProjects();

    // 根据项目编号查询单个项目。
    ProjectVO getProjectById(Integer projectId);

    // 新增项目。
    ProjectVO createProject(ProjectCreateDTO createDTO);

    // 修改项目。
    ProjectVO updateProject(Integer projectId, ProjectUpdateDTO updateDTO);

    // 删除项目。
    void deleteProject(Integer projectId);
}
