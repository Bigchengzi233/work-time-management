import request from './request'

// 根据用户编号查询工时报单：对应后端 GET /api/work-times/users/{userId}。
export function listWorkTimesByUserIdApi(userId) {
  return request.get(`/work-times/users/${userId}`)
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

// 删除工时草稿或已驳回工时：对应后端 DELETE /api/work-times/{workId}?userId={userId}。
export function deleteWorkTimeApi(workId, userId) {
  return request.delete(`/work-times/${workId}`, {
    params: {
      userId,
    },
  })
}
