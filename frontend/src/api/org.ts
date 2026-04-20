import request from './request'

export interface DeptTreeNode {
  id: number
  deptCode: string
  deptName: string
  parentId: number
  status: string
  children?: DeptTreeNode[]
}

export interface PositionVO {
  id: number
  positionCode: string
  positionName: string
  deptId: number
  status: string
}

export function getDeptTree() {
  return request.get<DeptTreeNode[]>('/org/depts/tree')
}

export function listPositions(deptId?: number) {
  return request.get<PositionVO[]>('/org/positions', { params: { deptId } })
}
