import request from './request'

export interface WorkflowTaskVO {
  id: number
  processInstanceId: number
  flowableTaskId: string
  taskName: string
  assigneeId?: number
  claimTime?: string
  completeTime?: string
  action?: string
  comment?: string
  status: string
}

export interface ProcessInstanceVO {
  id: number
  processKey: string
  processName: string
  businessKey: string
  businessType?: string
  starterId?: number
  startTime?: string
  endTime?: string
  status: string
  flowableInstanceId?: string
}

export interface ProcessProgressVO {
  processKey: string
  businessKey: string
  status: string
  startTime?: string
  endTime?: string
  taskList: Array<Record<string, unknown>>
}

export interface ProcessDefinitionVO {
  id: string
  key: string
  name: string
  version: number
  category?: string
  deploymentId?: string
  resourceName?: string
  diagramResourceName?: string
  suspended: boolean
  tenantId?: string
  deploymentTime?: string
}

export function listPendingTasks(assigneeId?: number) {
  return request.get<WorkflowTaskVO[]>('/workflow/tasks/pending', { params: { assigneeId } })
}

export function completeTask(
  taskId: number,
  data: {
    action?: string
    comment?: string
    nextAssigneeIds?: number[]
  },
) {
  return request.post<void>(`/workflow/tasks/${taskId}/complete`, data)
}

export function getProcessByBusinessKey(businessKey: string) {
  return request.get<ProcessInstanceVO | null>('/workflow/process/by-business-key', {
    params: { businessKey },
  })
}

export function getProcessProgress(businessKey: string) {
  return request.get<ProcessProgressVO | null>('/workflow/process/progress', {
    params: { businessKey },
  })
}

export function listProcessDefinitions() {
  return request.get<ProcessDefinitionVO[]>('/workflow/process-definitions')
}

export function startProcess(data: {
  processKey: string
  businessKey: string
  businessType?: string
  variables?: Record<string, unknown>
}) {
  return request.post<void>('/workflow/process/start', data)
}
