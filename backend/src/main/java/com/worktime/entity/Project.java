package com.worktime.entity;

// 项目实体类：对应数据库中的 project 表。
public class Project {

    // 项目编号，对应 project.project_id。
    private Integer projectId;

    // 项目名称，对应 project.project_name。
    private String projectName;

    // 项目状态：0禁用，1启用。
    private Integer projectStatus;

    // 所属部门编号，对应 project.dept_id。
    private Integer deptId;

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
}
