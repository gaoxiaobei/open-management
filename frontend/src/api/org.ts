import request from './request'
import type { PageQuery, PageResult } from '@/types/api'

export interface DeptTreeNode {
  id: number
  deptCode: string
  deptName: string
  parentId: number
  status: string
  children?: DeptTreeNode[]
}

export interface PositionVO {
  id?: number
  positionCode: string
  positionName: string
  deptId?: number
  status: string
}

export function getDeptTree() {
  return request.get<DeptTreeNode[]>('/org/depts/tree')
}

export function listPositions(deptId?: number) {
  return request.get<PositionVO[]>('/org/positions', { params: { deptId } })
}

export function pagePositions(params: PageQuery & Partial<PositionVO>) {
  return request.get<PageResult<PositionVO>>('/org/positions/page', { params })
}

export function createPosition(data: PositionVO) {
  return request.post<void>('/org/positions', data)
}

export function updatePosition(id: number, data: PositionVO) {
  return request.put<void>(`/org/positions/${id}`, data)
}

export function deletePosition(id: number) {
  return request.delete<void>(`/org/positions/${id}`)
}
