import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo, MenuItem } from '@/types/user'
import { login, logout } from '@/api/auth'
import type { LoginParams, LoginResult } from '@/api/auth'
import { usePermissionStore } from '@/stores/permission'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  async function doLogin(params: LoginParams) {
    const result = await login(params) as unknown as LoginResult
    token.value = result.token
    localStorage.setItem('token', result.token)

    // Map backend LoginUserInfo (userId) to the frontend UserInfo shape (id)
    const raw = result.userInfo
    userInfo.value = {
      id: raw.userId,
      username: raw.username,
      realName: raw.realName,
    }

    const permissionStore = usePermissionStore()
    permissionStore.setMenus((result.menus || []) as MenuItem[])
    permissionStore.setPermissions(raw.permissions || [])
  }

  async function doLogout() {
    // Best-effort: always clear local state regardless of whether the backend call succeeds
    try {
      await logout()
    } catch {
      // ignore backend errors
    }
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
