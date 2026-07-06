import request from './request'

// 查询员工个人工时统计：对应后端 GET /api/statistics/personal。
export function getPersonalStatisticsApi(params) {
  return request.get('/statistics/personal', {
    params,
  })
}

// 查询部门工时统计：对应后端 GET /api/statistics/department。
export function getDepartmentStatisticsApi(params) {
  return request.get('/statistics/department', {
    params,
  })
}

// 管理员按部门查询工时统计：对应后端 GET /api/statistics/department-by-dept。
export function getDepartmentStatisticsByDeptApi(params) {
  return request.get('/statistics/department-by-dept', {
    params,
  })
}

// 查询公司工时统计：对应后端 GET /api/statistics/company。
export function getCompanyStatisticsApi(params) {
  return request.get('/statistics/company', {
    params,
  })
}
