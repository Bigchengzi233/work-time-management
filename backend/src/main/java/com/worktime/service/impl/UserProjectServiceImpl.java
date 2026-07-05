package com.worktime.service.impl;

import com.worktime.dto.UserProjectCreateDTO;
import com.worktime.dto.UserProjectUpdateDTO;
import com.worktime.entity.UserProject;
import com.worktime.exception.BusinessException;
import com.worktime.mapper.UserProjectMapper;
import com.worktime.service.UserProjectService;
import com.worktime.vo.UserProjectRowVO;
import com.worktime.vo.UserProjectRuleVO;
import com.worktime.vo.UserProjectVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// 用户项目授权业务实现类：处理员工项目分配的业务规则。
@Service
public class UserProjectServiceImpl implements UserProjectService {

    // 用户项目授权数据访问对象。
    private final UserProjectMapper userProjectMapper;

    public UserProjectServiceImpl(UserProjectMapper userProjectMapper) {
        this.userProjectMapper = userProjectMapper;
    }

    // 查询全部授权记录。
    @Override
    public List<UserProjectVO> listUserProjects() {
        return userProjectMapper.selectAll().stream()
                .map(UserProjectVO::fromRow)
                .toList();
    }

    // 根据授权编号查询单条授权记录。
    @Override
    public UserProjectVO getUserProjectById(Integer authId) {
        UserProjectRowVO userProject = userProjectMapper.selectById(authId);
        if (userProject == null) {
            throw new BusinessException(404, "授权记录不存在");
        }
        return UserProjectVO.fromRow(userProject);
    }

    // 根据用户编号查询该用户的授权项目。
    @Override
    public List<UserProjectVO> listUserProjectsByUserId(Integer userId) {
        if (userProjectMapper.countUserById(userId) == 0) {
            throw new BusinessException(404, "用户不存在");
        }
        return userProjectMapper.selectByUserId(userId).stream()
                .map(UserProjectVO::fromRow)
                .toList();
    }

    // 新增授权记录。
    @Override
    public UserProjectVO createUserProject(UserProjectCreateDTO createDTO) {
        validateCreateRule(createDTO.getUserId(), createDTO.getProjectId(), createDTO.getAuthStatus());

        UserProject userProject = new UserProject();
        userProject.setUserId(createDTO.getUserId());
        userProject.setProjectId(createDTO.getProjectId());
        userProject.setAuthTime(LocalDateTime.now());
        userProject.setAuthStatus(createDTO.getAuthStatus());

        userProjectMapper.insert(userProject);
        return getUserProjectById(userProject.getAuthId());
    }

    // 修改授权状态。
    @Override
    public UserProjectVO updateUserProject(Integer authId, UserProjectUpdateDTO updateDTO) {
        UserProjectRowVO oldUserProject = userProjectMapper.selectById(authId);
        if (oldUserProject == null) {
            throw new BusinessException(404, "授权记录不存在");
        }

        validateUpdateRule(oldUserProject.getUserId(), oldUserProject.getProjectId(), updateDTO.getAuthStatus());
        userProjectMapper.updateStatusById(authId, updateDTO.getAuthStatus());
        return getUserProjectById(authId);
    }

    // 取消授权：这里不物理删除记录，只把 auth_status 改为 0。
    @Override
    public void cancelUserProject(Integer authId) {
        UserProjectRowVO oldUserProject = userProjectMapper.selectById(authId);
        if (oldUserProject == null) {
            throw new BusinessException(404, "授权记录不存在");
        }
        userProjectMapper.updateStatusById(authId, 0);
    }

    // 新增授权前校验业务规则。
    private void validateCreateRule(Integer userId, Integer projectId, Integer authStatus) {
        validateBaseRule(userId, projectId, authStatus);

        if (userProjectMapper.countByUserIdAndProjectId(userId, projectId) > 0) {
            throw new BusinessException(400, "该用户和项目已存在授权记录");
        }
    }

    // 修改授权状态前校验业务规则。
    private void validateUpdateRule(Integer userId, Integer projectId, Integer authStatus) {
        validateBaseRule(userId, projectId, authStatus);
    }

    // 授权通用规则：用户存在、项目存在、员工角色、同部门、禁用项目不能有效授权。
    private void validateBaseRule(Integer userId, Integer projectId, Integer authStatus) {
        if (userProjectMapper.countUserById(userId) == 0) {
            throw new BusinessException(404, "用户不存在");
        }

        if (userProjectMapper.countProjectById(projectId) == 0) {
            throw new BusinessException(404, "项目不存在");
        }

        UserProjectRuleVO rule = userProjectMapper.selectRuleInfo(userId, projectId);

        if (!"2".equals(rule.getUserRole())) {
            throw new BusinessException(400, "只能给普通员工分配项目");
        }

        if (!rule.getUserDeptId().equals(rule.getProjectDeptId())) {
            throw new BusinessException(400, "不能跨部门分配项目");
        }

        if (authStatus == 1 && rule.getProjectStatus() == 0) {
            throw new BusinessException(400, "禁用项目不能设置为有效授权");
        }
    }
}
