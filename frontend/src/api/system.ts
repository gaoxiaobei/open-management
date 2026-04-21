import request from './request'
import type { PageQuery, PageResult } from '@/types/api'

export interface UserVO {
  id: number
  username: string
  realName: string
  mobile?: string
  email?: string
  deptId?: number
  deptName?: string
  positionId?: number
  status: string
  roleIds?: number[]
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
  id?: number
  roleCode: string
  roleName: string
  dataScope: string
  status: string
  remark?: string
}

export interface MenuVO {
  id?: number
  parentId?: number
  menuName: string
  menuType: string
  path?: string
  component?: string
  icon?: string
  permissionCode?: string
  sortOrder?: number
  status: string
  children?: MenuVO[]
}

export interface DictTypeVO {
  id?: number
  dictType: string
  dictName: string
  status: string
  remark?: string
}

export interface DictItemVO {
  id?: number
  dictType: string
  itemLabel: string
  itemValue: string
  sortOrder?: number
  status: string
}

export interface ConfigVO {
  id?: number
  configName: string
  configKey: string
  configValue?: string
  configType?: string
  remark?: string
}

export function pageUsers(params: PageQuery & Partial<UserVO>) {
  return request.get<PageResult<UserVO>>('/system/users', { params })
}

export function getUserById(id: number) {
  return request.get<UserVO>(`/system/users/${id}`)
}

export function createUser(data: UserCreateParams) {
  return request.post<void>('/system/users', data)
}

export function updateUser(id: number, data: UserCreateParams) {
  return request.put<void>(`/system/users/${id}`, data)
}

export function resetPassword(id: number) {
  return request.post<void>(`/system/users/${id}/reset-password`)
}

export function changeUserStatus(id: number, status: string) {
  return request.put<void>(`/system/users/${id}/status`, null, { params: { status } })
}

export function deleteUser(id: number) {
  return request.delete<void>(`/system/users/${id}`)
}

export function pageRoles(params: PageQuery & Partial<RoleVO>) {
  return request.get<PageResult<RoleVO>>('/system/roles', { params })
}

export function listAllRoles() {
  return request.get<RoleVO[]>('/system/roles/all')
}

export function getRoleById(id: number) {
  return request.get<RoleVO>(`/system/roles/${id}`)
}

export function createRole(data: RoleVO) {
  return request.post<void>('/system/roles', data)
}

export function updateRole(id: number, data: RoleVO) {
  return request.put<void>(`/system/roles/${id}`, data)
}

export function deleteRole(id: number) {
  return request.delete<void>(`/system/roles/${id}`)
}

export function getRoleMenuIds(id: number) {
  return request.get<number[]>(`/system/roles/${id}/menu-ids`)
}

export function assignRoleMenus(id: number, menuIds: number[]) {
  return request.post<void>(`/system/roles/${id}/menus`, menuIds)
}

export function getMenuTree() {
  return request.get<MenuVO[]>('/system/menus')
}

export function getMenuById(id: number) {
  return request.get<MenuVO>(`/system/menus/${id}`)
}

export function createMenu(data: MenuVO) {
  return request.post<void>('/system/menus', data)
}

export function updateMenu(id: number, data: MenuVO) {
  return request.put<void>(`/system/menus/${id}`, data)
}

export function deleteMenu(id: number) {
  return request.delete<void>(`/system/menus/${id}`)
}

export function pageDictTypes(params: PageQuery & Partial<DictTypeVO>) {
  return request.get<PageResult<DictTypeVO>>('/system/dict/types', { params })
}

export function createDictType(data: DictTypeVO) {
  return request.post<void>('/system/dict/types', data)
}

export function updateDictType(id: number, data: DictTypeVO) {
  return request.put<void>(`/system/dict/types/${id}`, data)
}

export function deleteDictType(id: number) {
  return request.delete<void>(`/system/dict/types/${id}`)
}

export function getDictItems(dictType: string, includeDisabled = false) {
  return request.get<DictItemVO[]>(`/system/dict/items/${dictType}`, {
    params: { includeDisabled },
  })
}

export function createDictItem(data: DictItemVO) {
  return request.post<void>('/system/dict/items', data)
}

export function updateDictItem(id: number, data: DictItemVO) {
  return request.put<void>(`/system/dict/items/${id}`, data)
}

export function deleteDictItem(id: number) {
  return request.delete<void>(`/system/dict/items/${id}`)
}

export function pageConfigs(params: PageQuery & Partial<ConfigVO>) {
  return request.get<PageResult<ConfigVO>>('/system/configs', { params })
}

export function createConfig(data: ConfigVO) {
  return request.post<void>('/system/configs', data)
}

export function updateConfig(id: number, data: ConfigVO) {
  return request.put<void>(`/system/configs/${id}`, data)
}

export function deleteConfig(id: number) {
  return request.delete<void>(`/system/configs/${id}`)
}
