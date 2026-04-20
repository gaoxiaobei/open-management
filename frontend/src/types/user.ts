export interface UserInfo {
  id: number
  username: string
  realName: string
  deptId: number
  deptName?: string
  positionId?: number
  positionName?: string
  roles?: string[]
  permissions?: string[]
}

export interface MenuItem {
  id: number
  parentId: number
  menuName: string
  menuType: string
  path: string
  component: string
  icon: string
  permissionCode: string
  sortOrder: number
  children?: MenuItem[]
}
