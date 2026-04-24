import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo, MenuItem } from '@/types/user'
import { login, logout } from '@/api/auth'
import type { LoginParams, LoginResult } from '@/api/auth'
import { usePermissionStore } from '@/stores/permission'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const storedUserInfo = localStorage.getItem('userInfo')
  const userInfo = ref<UserInfo | null>(storedUserInfo ? JSON.parse(storedUserInfo) : null)

  async function doLogin(params: LoginParams) {
    const result = await login(params) as unknown as LoginResult
    token.value = result.token
    localStorage.setItem('token', result.token)

    const raw = result.userInfo
    userInfo.value = {
      id: raw.userId,
      username: raw.username,
      realName: raw.realName,
    }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))

    const permissionStore = usePermissionStore()
    permissionStore.setMenus((result.menus || []) as MenuItem[])
    permissionStore.setPermissions(raw.permissions || [])
  }

  async function doLogout() {
    try {
      await logout()
    } catch {
      // ignore backend errors
    }
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')

    const permissionStore = usePermissionStore()
    permissionStore.setMenus([])
    permissionStore.setPermissions([])
  }

  function isLoggedIn() {
    return !!token.value
  }

  return { token, userInfo, doLogin, doLogout, isLoggedIn }
})
