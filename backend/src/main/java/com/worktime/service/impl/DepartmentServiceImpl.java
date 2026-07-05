package com.worktime.service.impl;

import com.worktime.entity.Department;
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
}
