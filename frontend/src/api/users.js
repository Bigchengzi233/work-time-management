import request from './request'

// 查询用户列表：对应后端 GET /api/users。
export function listUsersApi() {
  return request.get('/users')
}

// 查询用户在职状态映射：对应后端 GET /api/users/employment-statuses。
export function listUserEmploymentStatusesApi() {
  return request.get('/users/employment-statuses')
}

// 新增用户：对应后端 POST /api/users。
export function createUserApi(data) {
  return request.post('/users', data)
}

// 修改用户：对应后端 PUT /api/users/{userId}。
export function updateUserApi(userId, data) {
  return request.put(`/users/${userId}`, data)
}

// 重置用户密码：对应后端 PUT /api/users/{userId}/reset-password。
export function resetUserPasswordApi(userId) {
  return request.put(`/users/${userId}/reset-password`)
}

// 修改用户在职状态：对应后端 PUT /api/users/{userId}/employment-status。
export function updateUserEmploymentStatusApi(userId, employmentStatus) {
  return request.put(`/users/${userId}/employment-status`, { employmentStatus })
}

// 删除用户：对应后端 DELETE /api/users/{userId}。
export function deleteUserApi(userId) {
  return request.delete(`/users/${userId}`)
}
