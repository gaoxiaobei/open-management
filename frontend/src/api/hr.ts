import request from './request'
import type { PageQuery, PageResult } from '@/types/api'

export interface EmployeeVO {
  id: number
  empNo: string
  empName: string
  gender: string
  idNo?: string
  deptId: number
  deptName: string
  positionId: number
  positionName: string
  hireDate: string
  empStatus: string
  mobile: string
  email: string
}

export interface EmployeeCreateParams {
  empNo: string
  empName: string
  gender: string
  idNo: string
  deptId: number
  positionId: number
  hireDate: string
  mobile?: string
  email?: string
}

export function pageEmployees(params: PageQuery & Partial<EmployeeVO>) {
  return request.get<PageResult<EmployeeVO>>('/hr/employees', { params })
}

export function createEmployee(data: EmployeeCreateParams) {
  return request.post('/hr/employees', data)
}

export function getEmployee(id: number) {
  return request.get<EmployeeVO>(`/hr/employees/${id}`)
}

export function updateEmployee(id: number, data: EmployeeCreateParams) {
  return request.put<void>(`/hr/employees/${id}`, data)
}

export function deleteEmployee(id: number) {
  return request.delete<void>(`/hr/employees/${id}`)
}
