import request from './request'
import type { PageQuery, PageResult } from '@/types/api'

export interface LoginLogVO {
  id: number
  username: string
  userId?: number
  ipAddr?: string
  browser?: string
  os?: string
  loginStatus?: string
  loginTime?: string
  msg?: string
}

export interface OperateLogVO {
  id: number
  moduleName?: string
  operationName?: string
  requestMethod?: string
  requestUrl?: string
  requestParams?: string
  responseResult?: string
  userId?: number
  username?: string
  ipAddr?: string
  operateTime?: string
  costTime?: number
}

export function pageLoginLogs(params: PageQuery & { userId?: number }) {
  return request.get<PageResult<LoginLogVO>>('/audit/login-logs', { params })
}

export function pageOperateLogs(params: PageQuery & { userId?: number }) {
  return request.get<PageResult<OperateLogVO>>('/audit/operate-logs', { params })
}
