import request from './request'

export interface FileVO {
  id: number
  originalName: string
  fileSize: number
  contentType: string
  bizType?: string
  bizId?: number
  accessUrl?: string
  createdAt?: string
}

export function uploadFile(file: File, bizType?: string, bizId?: number) {
  const formData = new FormData()
  formData.append('file', file)
  if (bizType) formData.append('bizType', bizType)
  if (bizId !== undefined) formData.append('bizId', String(bizId))
  return request.post<string>('/files/upload', formData)
}

export function getFileAccessUrl(id: number) {
  return request.get<string>(`/files/${id}/url`)
}

export function deleteFile(id: number) {
  return request.delete<void>(`/files/${id}`)
}
