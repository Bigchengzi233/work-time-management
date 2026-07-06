import request from './request'

// 查询部门列表：对应后端 GET /api/departments。
export function listDepartmentsApi() {
  return request.get('/departments')
}

// 新增部门：对应后端 POST /api/departments。
export function createDepartmentApi(data) {
  return request.post('/departments', data)
}

// 修改部门：对应后端 PUT /api/departments/{deptId}。
export function updateDepartmentApi(deptId, data) {
  return request.put(`/departments/${deptId}`, data)
}

// 删除部门：对应后端 DELETE /api/departments/{deptId}。
export function deleteDepartmentApi(deptId) {
  return request.delete(`/departments/${deptId}`)
}
