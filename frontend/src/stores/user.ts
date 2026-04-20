import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo } from '@/types/user'
import { login, logout } from '@/api/auth'
import type { LoginParams } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  async function doLogin(params: LoginParams) {
    const result = await login(params) as unknown as { token: string; userInfo: UserInfo }
    token.value = result.token
    userInfo.value = result.userInfo
    localStorage.setItem('token', result.token)
  }

  async function doLogout() {
    await logout()
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  function isLoggedIn() {
    return !!token.value
  }

  return { token, userInfo, doLogin, doLogout, isLoggedIn }
})
