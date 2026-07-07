import request from './request'

// 根据用户编号查询工时报单：对应后端 GET /api/work-times/users/{userId}。
export function listWorkTimesByUserIdApi(userId) {
  return request.get(`/work-times/users/${userId}`)
}

// 根据部门经理编号查询待审批工时报单：对应后端 GET /api/work-times/pending/managers/{managerId}。
export function listPendingWorkTimesByManagerIdApi(managerId) {
  return request.get(`/work-times/pending/managers/${managerId}`)
}

// 查询经理本部门昨天未填报工时的员工：对应后端 GET /api/work-times/exceptions/managers/{managerId}。
export function listMissingWorkTimesByManagerIdApi(managerId) {
  return request.get(`/work-times/exceptions/managers/${managerId}`)
}

// 新增工时草稿：对应后端 POST /api/work-times。
export function createWorkTimeApi(data) {
  return request.post('/work-times', data)
}

// 修改工时草稿或已驳回工时：对应后端 PUT /api/work-times/{workId}。
export function updateWorkTimeApi(workId, data) {
  return request.put(`/work-times/${workId}`, data)
}

// 提交工时审批：对应后端 POST /api/work-times/{workId}/submit。
export function submitWorkTimeApi(workId) {
  return request.post(`/work-times/${workId}/submit`)
}

// 部门经理审批通过工时：对应后端 POST /api/work-times/{workId}/approve。
export function approveWorkTimeApi(workId, data) {
  return request.post(`/work-times/${workId}/approve`, data)
}

// 部门经理驳回工时：对应后端 POST /api/work-times/{workId}/reject。
export function rejectWorkTimeApi(workId, data) {
  return request.post(`/work-times/${workId}/reject`, data)
}

// 删除工时草稿或已驳回工时：对应后端 DELETE /api/work-times/{workId}?userId={userId}。
export function deleteWorkTimeApi(workId, userId) {
  return request.delete(`/work-times/${workId}`, {
    params: {
      userId,
    },
  })
}
