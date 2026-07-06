import request from './request'

// 查询授权记录列表：对应后端 GET /api/user-projects。
export function listUserProjectsApi() {
  return request.get('/user-projects')
}

// 根据用户编号查询授权项目：对应后端 GET /api/user-projects/users/{userId}。
export function listUserProjectsByUserIdApi(userId) {
  return request.get(`/user-projects/users/${userId}`)
}

// 新增授权记录：对应后端 POST /api/user-projects。
export function createUserProjectApi(data) {
  return request.post('/user-projects', data)
}

// 修改授权状态：对应后端 PUT /api/user-projects/{authId}。
export function updateUserProjectApi(authId, data) {
  return request.put(`/user-projects/${authId}`, data)
}

// 取消授权：对应后端 DELETE /api/user-projects/{authId}。
export function cancelUserProjectApi(authId) {
  return request.delete(`/user-projects/${authId}`)
}
