import request from './request'

// 查询项目列表：对应后端 GET /api/projects。
export function listProjectsApi() {
  return request.get('/projects')
}

// 新增项目：对应后端 POST /api/projects。
export function createProjectApi(data) {
  return request.post('/projects', data)
}

// 修改项目：对应后端 PUT /api/projects/{projectId}。
export function updateProjectApi(projectId, data) {
  return request.put(`/projects/${projectId}`, data)
}

// 删除项目：对应后端 DELETE /api/projects/{projectId}。
export function deleteProjectApi(projectId) {
  return request.delete(`/projects/${projectId}`)
}
