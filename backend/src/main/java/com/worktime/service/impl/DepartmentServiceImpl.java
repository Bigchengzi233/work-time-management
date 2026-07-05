package com.worktime.service.impl;

import com.worktime.dto.DepartmentCreateDTO;
import com.worktime.dto.DepartmentUpdateDTO;
import com.worktime.entity.Department;
import com.worktime.exception.BusinessException;
import com.worktime.mapper.DepartmentMapper;
import com.worktime.service.DepartmentService;
import com.worktime.vo.DepartmentVO;
import org.springframework.stereotype.Service;

import java.util.List;

// 部门业务实现类：真正编写部门相关业务逻辑。
@Service
public class DepartmentServiceImpl implements DepartmentService {

    // 注入 DepartmentMapper，用它访问 MySQL 数据库。
    private final DepartmentMapper departmentMapper;

    // 构造方法注入：Spring 会自动把 DepartmentMapper 对象传进来。
    public DepartmentServiceImpl(DepartmentMapper departmentMapper) {
        this.departmentMapper = departmentMapper;
    }

    // 查询全部部门，并把数据库实体转换成前端展示对象。
    @Override
    public List<DepartmentVO> listDepartments() {
        // 调用 mapper 查询 department 表中的所有部门。
        List<Department> departments = departmentMapper.selectAll();

        // 把每个 Department 实体转换成 DepartmentVO，再收集成列表返回。
        return departments.stream()
                .map(DepartmentVO::fromEntity)
                .toList();
    }

    // 根据部门编号查询单个部门；如果不存在，返回友好的业务错误。
    @Override
    public DepartmentVO getDepartmentById(Integer deptId) {
        // 根据主键查询 department 表。
        Department department = departmentMapper.selectById(deptId);

        // 如果数据库没有查到部门，抛出业务异常，由全局异常处理器统一返回 JSON。
        if (department == null) {
            throw new BusinessException(404, "部门不存在");
        }

        // 查到数据后，把数据库实体转换成前端返回对象。
        return DepartmentVO.fromEntity(department);
    }

    // 新增部门。
    @Override
    public DepartmentVO createDepartment(DepartmentCreateDTO createDTO) {
        // 去掉用户输入前后的空格，避免“研发部”和“ 研发部 ”被当作两个名称。
        String deptName = createDTO.getDeptName().trim();

        // 检查部门名称是否已经存在，避免基础数据重复。
        if (departmentMapper.countByDeptName(deptName) > 0) {
            throw new BusinessException(400, "部门名称已存在");
        }

        // 创建实体对象，用于写入 department 表。
        Department department = new Department();
        department.setDeptName(deptName);

        // 调用 mapper 插入数据库；插入后 deptId 会自动回填到 department 对象中。
        departmentMapper.insert(department);

        // 返回新增后的部门数据。
        return DepartmentVO.fromEntity(department);
    }

    // 修改部门。
    @Override
    public DepartmentVO updateDepartment(Integer deptId, DepartmentUpdateDTO updateDTO) {
        // 先查询部门是否存在。
        Department department = departmentMapper.selectById(deptId);
        if (department == null) {
            throw new BusinessException(404, "部门不存在");
        }

        // 去掉用户输入前后的空格。
        String deptName = updateDTO.getDeptName().trim();

        // 检查新名称是否和其他部门重复。
        if (departmentMapper.countByDeptNameExcludeId(deptName, deptId) > 0) {
            throw new BusinessException(400, "部门名称已存在");
        }

        // 设置新的部门名称。
        department.setDeptName(deptName);

        // 更新数据库中的部门名称。
        departmentMapper.updateById(department);

        // 返回修改后的部门数据。
        return DepartmentVO.fromEntity(department);
    }

    // 删除部门。
    @Override
    public void deleteDepartment(Integer deptId) {
        // 先查询部门是否存在。
        Department department = departmentMapper.selectById(deptId);
        if (department == null) {
            throw new BusinessException(404, "部门不存在");
        }

        // 如果该部门下还有用户，不允许删除，避免用户数据变成无所属部门。
        if (departmentMapper.countUsersByDeptId(deptId) > 0) {
            throw new BusinessException(400, "该部门下存在用户，不能删除");
        }

        // 如果该部门下还有项目，不允许删除，避免项目数据变成无所属部门。
        if (departmentMapper.countProjectsByDeptId(deptId) > 0) {
            throw new BusinessException(400, "该部门下存在项目，不能删除");
        }

        // 通过所有检查后，删除部门。
        departmentMapper.deleteById(deptId);
    }
}
