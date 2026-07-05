export const ROLE_ADMIN = '0'
export const ROLE_MANAGER = '1'
export const ROLE_EMPLOYEE = '2'

// 把数据库里的角色值转换成页面展示文本。
export function getRoleName(userRole) {
  const roleMap = {
    [ROLE_ADMIN]: '管理员',
    [ROLE_MANAGER]: '部门经理',
    [ROLE_EMPLOYEE]: '员工',
  }

  return roleMap[userRole] || '未知角色'
}
