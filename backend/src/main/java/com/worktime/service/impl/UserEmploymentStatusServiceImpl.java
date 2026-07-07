package com.worktime.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worktime.common.RoleConstants;
import com.worktime.exception.BusinessException;
import com.worktime.mapper.UserMapper;
import com.worktime.mapper.WorkTimeApplyMapper;
import com.worktime.service.UserEmploymentStatusService;
import com.worktime.vo.UserRowVO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

// 用户在职状态业务实现类：通过后端本地 JSON 文件跨浏览器保存状态。
@Service
public class UserEmploymentStatusServiceImpl implements UserEmploymentStatusService {

    // 在职状态。
    private static final String STATUS_ACTIVE = "active";

    // 离职状态。
    private static final String STATUS_INACTIVE = "inactive";

    // 状态文件路径，相对后端运行目录生成 data/user-employment-status.json。
    private static final Path STATUS_FILE_PATH = Path.of("data", "user-employment-status.json");

    // JSON 读写工具，复用 Spring Boot 默认 Jackson 依赖。
    private final ObjectMapper objectMapper;

    // 用户数据访问对象，用来确认用户是否存在。
    private final UserMapper userMapper;

    // 工时数据访问对象，用来校验离职前的工时和审批状态。
    private final WorkTimeApplyMapper workTimeApplyMapper;

    public UserEmploymentStatusServiceImpl(
            ObjectMapper objectMapper,
            UserMapper userMapper,
            WorkTimeApplyMapper workTimeApplyMapper) {
        this.objectMapper = objectMapper;
        this.userMapper = userMapper;
        this.workTimeApplyMapper = workTimeApplyMapper;
    }

    // 查询全部用户在职状态。
    @Override
    public synchronized Map<String, String> listEmploymentStatuses() {
        return readStatusMap();
    }

    // 修改指定用户的在职状态。
    @Override
    public synchronized void updateEmploymentStatus(Integer userId, String employmentStatus) {
        UserRowVO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        if (!STATUS_ACTIVE.equals(employmentStatus) && !STATUS_INACTIVE.equals(employmentStatus)) {
            throw new BusinessException(400, "在职状态只能是 active 或 inactive");
        }

        if (STATUS_INACTIVE.equals(employmentStatus)) {
            validateCanSetInactive(user);
        }

        Map<String, String> statusMap = readStatusMap();
        statusMap.put(String.valueOf(userId), employmentStatus);
        writeStatusMap(statusMap);
    }

    // 判断指定用户当前是否为离职状态。
    @Override
    public synchronized boolean isInactive(Integer userId) {
        return STATUS_INACTIVE.equals(readStatusMap().get(String.valueOf(userId)));
    }

    // 校验用户是否可以设置为离职。
    private void validateCanSetInactive(UserRowVO user) {
        if (RoleConstants.ADMIN.equals(user.getUserRole())) {
            throw new BusinessException(400, "管理员账号不能设置为离职");
        }

        if (RoleConstants.EMPLOYEE.equals(user.getUserRole())
                && workTimeApplyMapper.countUnapprovedByUserId(user.getUserId()) > 0) {
            throw new BusinessException(400, "该员工还有未审批通过的工时，不能设置为离职");
        }

        if (RoleConstants.MANAGER.equals(user.getUserRole())
                && workTimeApplyMapper.countPendingByManagerId(user.getUserId()) > 0) {
            throw new BusinessException(400, "该部门经理还有待审批工时，不能设置为离职");
        }
    }

    // 从本地 JSON 文件读取状态；文件不存在时返回空映射。
    private Map<String, String> readStatusMap() {
        if (!Files.exists(STATUS_FILE_PATH)) {
            return new LinkedHashMap<>();
        }

        try {
            return objectMapper.readValue(
                    STATUS_FILE_PATH.toFile(),
                    new TypeReference<LinkedHashMap<String, String>>() {
                    });
        } catch (IOException exception) {
            throw new BusinessException(500, "读取用户在职状态文件失败");
        }
    }

    // 把状态映射写回本地 JSON 文件。
    private void writeStatusMap(Map<String, String> statusMap) {
        try {
            Files.createDirectories(STATUS_FILE_PATH.getParent());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(STATUS_FILE_PATH.toFile(), statusMap);
        } catch (IOException exception) {
            throw new BusinessException(500, "保存用户在职状态文件失败");
        }
    }
}
