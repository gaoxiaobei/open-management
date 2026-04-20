import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo, MenuItem } from '@/types/user'
import { login, logout } from '@/api/auth'
import type { LoginParams } from '@/api/auth'
import { usePermissionStore } from '@/stores/permission'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  async function doLogin(params: LoginParams) {
    const result = await login(params) as unknown as {
      token: string
      userInfo: UserInfo & { roles?: string[]; permissions?: string[] }
      menus: MenuItem[]
    }
    token.value = result.token
    userInfo.value = result.userInfo
    localStorage.setItem('token', result.token)

    const permissionStore = usePermissionStore()
    permissionStore.setMenus(result.menus || [])
    permissionStore.setPermissions(result.userInfo?.permissions || [])
  }

  async function doLogout() {
    await logout()
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')

    const permissionStore = usePermissionStore()
    permissionStore.setMenus([])
    permissionStore.setPermissions([])
  }

  function isLoggedIn() {
    return !!token.value
  }

  return { token, userInfo, doLogin, doLogout, isLoggedIn }
})
