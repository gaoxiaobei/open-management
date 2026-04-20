import request from './request'
import type { PageQuery, PageResult } from '@/types/api'

export interface AssetVO {
  id: number
  assetCode: string
  assetName: string
  category: string
  assetStatus: string
  location: string
  currentUserId: number
  deptId: number
}

export function pageAssets(params: PageQuery & Partial<AssetVO>) {
  return request.get<PageResult<AssetVO>>('/assets', { params })
}

export function submitReceiveApply(data: { assetId: number; reason: string }) {
  return request.post('/assets/receive-applications', data)
}
