// 统一处理前端表格的分页切片，避免各页面重复计算 start/end。
export function paginateList(list, currentPage, pageSize) {
  const startIndex = (currentPage - 1) * pageSize
  const endIndex = startIndex + pageSize
  return list.slice(startIndex, endIndex)
}

// 统一把搜索字段转成小写文本，便于姓名、项目名、编号等字段混合搜索。
export function normalizeSearchText(value) {
  return String(value ?? '').trim().toLowerCase()
}
