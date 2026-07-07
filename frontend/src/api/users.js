import request from './request'

// 查询用户列表：对应后端 GET /api/users。
export function listUsersApi() {
  return request.get('/users')
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

// 删除用户：对应后端 DELETE /api/users/{userId}。
export function deleteUserApi(userId) {
  return request.delete(`/users/${userId}`)
}
