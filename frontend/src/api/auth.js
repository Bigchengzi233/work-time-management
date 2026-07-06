import request from './request'

// 登录接口：对应后端 POST /api/auth/login。
export function loginApi(data) {
  return request.post('/auth/login', data)
}

// 登录验证码接口：对应后端 GET /api/auth/captcha。
export function getCaptchaApi() {
  return request.get('/auth/captcha')
}

// 当前登录用户接口：对应后端 GET /api/auth/me。
export function getCurrentUserApi() {
  return request.get('/auth/me')
}
