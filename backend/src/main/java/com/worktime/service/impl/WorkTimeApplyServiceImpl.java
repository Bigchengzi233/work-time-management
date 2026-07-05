package com.worktime.service.impl;

import com.worktime.dto.WorkTimeApproveDTO;
import com.worktime.dto.WorkTimeCreateDTO;
import com.worktime.dto.WorkTimeRejectDTO;
import com.worktime.dto.WorkTimeUpdateDTO;
import com.worktime.entity.WorkTimeApply;
import com.worktime.entity.WorkTimeLog;
import com.worktime.exception.BusinessException;
import com.worktime.mapper.WorkTimeApplyMapper;
import com.worktime.mapper.WorkTimeLogMapper;
import com.worktime.service.WorkTimeApplyService;
import com.worktime.vo.WorkTimeApprovalRuleVO;
import com.worktime.vo.WorkTimeApplyRowVO;
import com.worktime.vo.WorkTimeApplyVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// 工时申报业务实现类：处理员工填报工时的业务规则。
@Service
public class WorkTimeApplyServiceImpl implements WorkTimeApplyService {

    // 草稿状态。
    private static final int STATUS_DRAFT = 0;

    // 待审批状态。
    private static final int STATUS_PENDING = 1;

    // 审批通过状态。
    private static final int STATUS_APPROVED = 2;

    // 已驳回状态。
    private static final int STATUS_REJECTED = 3;

    // 操作类型：新建。
    private static final int OPERATION_CREATE = 0;

    // 操作类型：修改。
    private static final int OPERATION_UPDATE = 1;

    // 操作类型：提交。
    private static final int OPERATION_SUBMIT = 2;

    // 操作类型：审批通过。
    private static final int OPERATION_APPROVE = 3;

    // 操作类型：驳回。
    private static final int OPERATION_REJECT = 4;

    // 工时申报数据访问对象。
    private final WorkTimeApplyMapper workTimeApplyMapper;

    // 工时日志数据访问对象。
    private final WorkTimeLogMapper workTimeLogMapper;

    public WorkTimeApplyServiceImpl(WorkTimeApplyMapper workTimeApplyMapper, WorkTimeLogMapper workTimeLogMapper) {
        this.workTimeApplyMapper = workTimeApplyMapper;
        this.workTimeLogMapper = workTimeLogMapper;
    }

    // 查询全部工时申报单。
    @Override
    public List<WorkTimeApplyVO> listWorkTimes() {
        return workTimeApplyMapper.selectAll().stream()
                .map(WorkTimeApplyVO::fromRow)
                .toList();
    }

    // 根据工时编号查询单条工时申报单。
    @Override
    public WorkTimeApplyVO getWorkTimeById(Integer workId) {
        WorkTimeApplyRowVO workTime = workTimeApplyMapper.selectById(workId);
        if (workTime == null) {
            throw new BusinessException(404, "工时申报单不存在");
        }
        return WorkTimeApplyVO.fromRow(workTime);
    }

    // 根据用户编号查询该用户的工时申报单。
    @Override
    public List<WorkTimeApplyVO> listWorkTimesByUserId(Integer userId) {
        if (workTimeApplyMapper.countUserById(userId) == 0) {
            throw new BusinessException(404, "用户不存在");
        }
        return workTimeApplyMapper.selectByUserId(userId).stream()
                .map(WorkTimeApplyVO::fromRow)
                .toList();
    }

    // 根据部门经理编号查询其本部门待审批工时。
    @Override
    public List<WorkTimeApplyVO> listPendingWorkTimesByManagerId(Integer managerId) {
        if (workTimeApplyMapper.countManagerById(managerId) == 0) {
            throw new BusinessException(400, "审批人不是部门经理");
        }
        return workTimeApplyMapper.selectPendingByManagerId(managerId).stream()
                .map(WorkTimeApplyVO::fromRow)
                .toList();
    }

    // 新建工时草稿，并写入新建日志。
    @Override
    @Transactional
    public WorkTimeApplyVO createWorkTime(WorkTimeCreateDTO createDTO) {
        validateCommonRule(createDTO.getUserId(), createDTO.getProjectId(), createDTO.getWorkDate(), createDTO.getWorkHours());

        if (workTimeApplyMapper.countDuplicateForCreate(
                createDTO.getUserId(),
                createDTO.getProjectId(),
                createDTO.getWorkDate()) > 0) {
            throw new BusinessException(400, "同一员工、同一项目、同一天只能填报一条工时");
        }

        WorkTimeApply workTime = new WorkTimeApply();
        workTime.setUserId(createDTO.getUserId());
        workTime.setProjectId(createDTO.getProjectId());
        workTime.setWorkDate(createDTO.getWorkDate());
        workTime.setWorkHours(createDTO.getWorkHours());
        workTime.setWorkDesc(normalizeDesc(createDTO.getWorkDesc()));
        workTime.setStatus(STATUS_DRAFT);

        workTimeApplyMapper.insert(workTime);
        addLog(workTime.getWorkId(), OPERATION_CREATE, workTime.getUserId(), "员工新建工时草稿");
        return getWorkTimeById(workTime.getWorkId());
    }

    // 修改工时草稿或已驳回工时，并写入修改日志。
    @Override
    @Transactional
    public WorkTimeApplyVO updateWorkTime(Integer workId, WorkTimeUpdateDTO updateDTO) {
        WorkTimeApplyRowVO oldWorkTime = workTimeApplyMapper.selectById(workId);
        if (oldWorkTime == null) {
            throw new BusinessException(404, "工时申报单不存在");
        }

        if (oldWorkTime.getStatus() != STATUS_DRAFT && oldWorkTime.getStatus() != STATUS_REJECTED) {
            throw new BusinessException(400, "只有草稿或已驳回的工时可以修改");
        }

        validateCommonRule(oldWorkTime.getUserId(), updateDTO.getProjectId(), updateDTO.getWorkDate(), updateDTO.getWorkHours());

        if (workTimeApplyMapper.countDuplicateForUpdate(
                workId,
                oldWorkTime.getUserId(),
                updateDTO.getProjectId(),
                updateDTO.getWorkDate()) > 0) {
            throw new BusinessException(400, "同一员工、同一项目、同一天只能填报一条工时");
        }

        WorkTimeApply workTime = new WorkTimeApply();
        workTime.setWorkId(workId);
        workTime.setUserId(oldWorkTime.getUserId());
        workTime.setProjectId(updateDTO.getProjectId());
        workTime.setWorkDate(updateDTO.getWorkDate());
        workTime.setWorkHours(updateDTO.getWorkHours());
        workTime.setWorkDesc(normalizeDesc(updateDTO.getWorkDesc()));
        workTime.setStatus(STATUS_DRAFT);

        workTimeApplyMapper.updateById(workTime);
        addLog(workId, OPERATION_UPDATE, oldWorkTime.getUserId(), "员工修改工时申报单");
        return getWorkTimeById(workId);
    }

    // 提交工时审批，并写入提交日志。
    @Override
    @Transactional
    public WorkTimeApplyVO submitWorkTime(Integer workId) {
        WorkTimeApplyRowVO oldWorkTime = workTimeApplyMapper.selectById(workId);
        if (oldWorkTime == null) {
            throw new BusinessException(404, "工时申报单不存在");
        }

        if (oldWorkTime.getStatus() != STATUS_DRAFT) {
            throw new BusinessException(400, "只有草稿状态的工时可以提交审批");
        }

        workTimeApplyMapper.updateStatusById(workId, STATUS_PENDING);
        addLog(workId, OPERATION_SUBMIT, oldWorkTime.getUserId(), "员工提交审批");
        return getWorkTimeById(workId);
    }

    // 部门经理审批通过工时，并写入审批通过日志。
    @Override
    @Transactional
    public WorkTimeApplyVO approveWorkTime(Integer workId, WorkTimeApproveDTO approveDTO) {
        validateApprovalRule(workId, approveDTO.getManagerId());
        workTimeApplyMapper.updateStatusById(workId, STATUS_APPROVED);
        addLog(workId, OPERATION_APPROVE, approveDTO.getManagerId(), normalizeApproveDesc(approveDTO.getOperationDesc()));
        return getWorkTimeById(workId);
    }

    // 部门经理驳回工时，并写入驳回日志。
    @Override
    @Transactional
    public WorkTimeApplyVO rejectWorkTime(Integer workId, WorkTimeRejectDTO rejectDTO) {
        validateApprovalRule(workId, rejectDTO.getManagerId());
        workTimeApplyMapper.updateStatusById(workId, STATUS_REJECTED);
        addLog(workId, OPERATION_REJECT, rejectDTO.getManagerId(), rejectDTO.getRejectReason().trim());
        return getWorkTimeById(workId);
    }

    // 校验通用工时规则：用户存在、项目存在、有效授权、日期和工时数合法。
    private void validateCommonRule(Integer userId, Integer projectId, LocalDate workDate, BigDecimal workHours) {
        if (workTimeApplyMapper.countUserById(userId) == 0) {
            throw new BusinessException(404, "用户不存在");
        }

        if (workTimeApplyMapper.countProjectById(projectId) == 0) {
            throw new BusinessException(404, "项目不存在");
        }

        if (workTimeApplyMapper.countValidUserProject(userId, projectId) == 0) {
            throw new BusinessException(400, "该员工没有此项目的有效授权，或项目已禁用");
        }

        if (workDate.isAfter(LocalDate.now())) {
            throw new BusinessException(400, "工作日期不能晚于当前日期");
        }

        if (workHours.multiply(new BigDecimal("10")).remainder(new BigDecimal("5")).compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException(400, "工时数必须按0.5小时粒度填写");
        }
    }

    // 校验审批规则：工时存在、审批人是部门经理、只能审批本部门待审批工时。
    private void validateApprovalRule(Integer workId, Integer managerId) {
        if (workTimeApplyMapper.selectById(workId) == null) {
            throw new BusinessException(404, "工时申报单不存在");
        }

        if (workTimeApplyMapper.countManagerById(managerId) == 0) {
            throw new BusinessException(400, "审批人不是部门经理");
        }

        WorkTimeApprovalRuleVO rule = workTimeApplyMapper.selectApprovalRule(workId, managerId);

        if (!"1".equals(rule.getManagerRole())) {
            throw new BusinessException(400, "只有部门经理可以审批工时");
        }

        if (!rule.getManagerDeptId().equals(rule.getWorkUserDeptId())) {
            throw new BusinessException(400, "部门经理只能审批本部门员工工时");
        }

        if (rule.getWorkStatus() != STATUS_PENDING) {
            throw new BusinessException(400, "只有待审批状态的工时可以审批");
        }
    }

    // 新增一条工时操作日志。
    private void addLog(Integer workId, Integer operationType, Integer operatorId, String operationDesc) {
        WorkTimeLog log = new WorkTimeLog();
        log.setWorkId(workId);
        log.setOperationType(operationType);
        log.setOperationTime(LocalDateTime.now());
        log.setOperatorId(operatorId);
        log.setOperationDesc(operationDesc);
        workTimeLogMapper.insert(log);
    }

    // 工作描述允许为空字符串，入库前统一转成 null。
    private String normalizeDesc(String workDesc) {
        if (workDesc == null || workDesc.isBlank()) {
            return null;
        }
        return workDesc.trim();
    }

    // 审批通过说明允许为空，空时使用默认说明。
    private String normalizeApproveDesc(String operationDesc) {
        if (operationDesc == null || operationDesc.isBlank()) {
            return "部门经理审批通过";
        }
        return operationDesc.trim();
    }
}
