import request from './request'

export interface LoginParams {
  username: string
  password: string
  captcha?: string
  captchaKey?: string
}

export interface LoginResult {
  token: string
  userInfo: {
    id: number
    username: string
    realName: string
    deptId: number
  }
  menus: unknown[]
}

export function login(params: LoginParams) {
  return request.post<LoginResult>('/auth/login', params)
}

export function logout() {
  return request.post('/auth/logout')
}
