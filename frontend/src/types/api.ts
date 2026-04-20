export interface R<T = unknown> {
  code: number
  message: string
  data: T
  traceId?: string
}

export interface PageResult<T> {
  total: number
  records: T[]
}

export interface PageQuery {
  pageNum: number
  pageSize: number
  sortField?: string
  sortOrder?: string
}
