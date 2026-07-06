import request from './request'

// 查询当前登录用户可见的工时操作日志列表：对应后端 GET /api/work-time-logs。
export function listWorkTimeLogsApi(params = {}) {
  return request.get('/work-time-logs', { params })
}

// 根据工时编号查询操作日志：对应后端 GET /api/work-time-logs/work-times/{workId}。
export function listWorkTimeLogsByWorkIdApi(workId) {
  return request.get(`/work-time-logs/work-times/${workId}`)
}
