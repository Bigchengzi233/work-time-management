package com.worktime.vo;

import com.worktime.entity.Project;

// 项目返回对象：返回给前端展示项目基础信息。
public class ProjectVO {

    // 项目编号。
    private Integer projectId;

    // 项目名称。
    private String projectName;

    // 项目状态：0禁用，1启用。
    private Integer projectStatus;

    // 所属部门编号。
    private Integer deptId;

    // 所属部门名称。
    private String deptName;

    public static ProjectVO fromEntity(Project project, String deptName) {
        ProjectVO projectVO = new ProjectVO();
        projectVO.setProjectId(project.getProjectId());
        projectVO.setProjectName(project.getProjectName());
        projectVO.setProjectStatus(project.getProjectStatus());
        projectVO.setDeptId(project.getDeptId());
        projectVO.setDeptName(deptName);
        return projectVO;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(Integer projectStatus) {
        this.projectStatus = projectStatus;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
