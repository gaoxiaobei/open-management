import request from './request'
import type { PageQuery, PageResult } from '@/types/api'

export interface MessageVO {
  id: number
  title: string
  content?: string
  msgType: string
  receiverId: number
  senderId?: number
  isRead: number
  readTime?: string
  bizType?: string
  bizId?: number
  createdAt?: string
}

export function pageMyMessages(params: PageQuery) {
  return request.get<PageResult<MessageVO>>('/messages/my', { params })
}

export function markMessageRead(id: number) {
  return request.post<void>(`/messages/${id}/read`)
}

export function getUnreadCount() {
  return request.get<number>('/messages/unread-count')
}
