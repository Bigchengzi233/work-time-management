package com.worktime.service.impl;

import com.worktime.dto.ProjectCreateDTO;
import com.worktime.dto.ProjectUpdateDTO;
import com.worktime.entity.Project;
import com.worktime.exception.BusinessException;
import com.worktime.mapper.ProjectMapper;
import com.worktime.service.ProjectService;
import com.worktime.vo.ProjectRowVO;
import com.worktime.vo.ProjectVO;
import org.springframework.stereotype.Service;

import java.util.List;

// 项目业务实现类：处理项目管理的业务规则。
@Service
public class ProjectServiceImpl implements ProjectService {

    // 项目数据访问对象。
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    // 查询全部项目。
    @Override
    public List<ProjectVO> listProjects() {
        return projectMapper.selectAll().stream()
                .map(project -> ProjectVO.fromEntity(project, project.getDeptName()))
                .toList();
    }

    // 根据项目编号查询单个项目。
    @Override
    public ProjectVO getProjectById(Integer projectId) {
        ProjectRowVO project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(404, "项目不存在");
        }
        return ProjectVO.fromEntity(project, project.getDeptName());
    }

    // 新增项目。
    @Override
    public ProjectVO createProject(ProjectCreateDTO createDTO) {
        // 检查所属部门是否存在，避免项目挂到不存在的部门下。
        if (projectMapper.countDepartmentById(createDTO.getDeptId()) == 0) {
            throw new BusinessException(404, "所属部门不存在");
        }

        Project project = new Project();
        project.setProjectName(createDTO.getProjectName().trim());
        project.setProjectStatus(createDTO.getProjectStatus());
        project.setDeptId(createDTO.getDeptId());

        projectMapper.insert(project);
        return getProjectById(project.getProjectId());
    }

    // 修改项目。
    @Override
    public ProjectVO updateProject(Integer projectId, ProjectUpdateDTO updateDTO) {
        ProjectRowVO oldProject = projectMapper.selectById(projectId);
        if (oldProject == null) {
            throw new BusinessException(404, "项目不存在");
        }

        // 检查新的所属部门是否存在。
        if (projectMapper.countDepartmentById(updateDTO.getDeptId()) == 0) {
            throw new BusinessException(404, "所属部门不存在");
        }

        Project project = new Project();
        project.setProjectId(projectId);
        project.setProjectName(updateDTO.getProjectName().trim());
        project.setProjectStatus(updateDTO.getProjectStatus());
        project.setDeptId(updateDTO.getDeptId());

        projectMapper.updateById(project);
        return getProjectById(projectId);
    }

    // 删除项目。
    @Override
    public void deleteProject(Integer projectId) {
        ProjectRowVO project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException(404, "项目不存在");
        }

        if (projectMapper.countWorkTimesByProjectId(projectId) > 0) {
            throw new BusinessException(400, "该项目存在工时申报数据，不能删除");
        }

        if (projectMapper.countUserProjectsByProjectId(projectId) > 0) {
            throw new BusinessException(400, "该项目存在员工授权记录，不能删除");
        }

        projectMapper.deleteById(projectId);
    }
}
