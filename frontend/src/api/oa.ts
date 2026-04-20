import request from './request'
import type { PageQuery, PageResult } from '@/types/api'

export interface LeaveApplyVO {
  id: number
  applyNo: string
  leaveType: string
  startTime: string
  endTime: string
  leaveDays: number
  reason: string
  applyStatus: string
}

export interface LeaveApplyParams {
  leaveType: string
  startTime: string
  endTime: string
  reason: string
  fileIds?: number[]
}

export function submitLeaveApply(data: LeaveApplyParams) {
  return request.post('/oa/leave-applications', data)
}

export function myLeaveApplies(params: PageQuery) {
  return request.get<PageResult<LeaveApplyVO>>('/oa/leave-applications/my', { params })
}
