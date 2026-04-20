import request from './request'
import type { PageQuery, PageResult } from '@/types/api'

export interface UserVO {
  id: number
  username: string
  realName: string
  mobile: string
  email: string
  deptId: number
  deptName: string
  status: string
}

export interface UserCreateParams {
  username: string
  realName: string
  mobile?: string
  email?: string
  deptId?: number
  positionId?: number
  roleIds?: number[]
}

export interface RoleVO {
  id: number
  roleCode: string
  roleName: string
  dataScope: string
  status: string
}

export interface DictItemVO {
  itemLabel: string
  itemValue: string
}

export function pageUsers(params: PageQuery & Partial<UserVO>) {
  return request.get<PageResult<UserVO>>('/system/users', { params })
}

export function createUser(data: UserCreateParams) {
  return request.post('/system/users', data)
}

export function updateUser(id: number, data: UserCreateParams) {
  return request.put(`/system/users/${id}`, data)
}

export function resetPassword(id: number) {
  return request.post(`/system/users/${id}/reset-password`)
}

export function listRoles() {
  return request.get<RoleVO[]>('/system/roles')
}

export function getDictItems(dictType: string) {
  return request.get<DictItemVO[]>(`/system/dicts/${dictType}/items`)
}
