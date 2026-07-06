import request from './request'

// 查询当前登录用户个人信息：对应后端 GET /api/profile。
export function getProfileApi() {
  return request.get('/profile')
}

// 修改当前登录用户个人信息：对应后端 PUT /api/profile。
export function updateProfileApi(data) {
  return request.put('/profile', data)
}

// 修改当前登录用户密码：对应后端 PUT /api/profile/password。
export function updatePasswordApi(data) {
  return request.put('/profile/password', data)
}
