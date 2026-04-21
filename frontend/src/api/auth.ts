import request from './request'

export interface LoginParams {
  username: string
  password: string
  captcha?: string
  captchaKey?: string
}

export interface CaptchaResult {
  captchaKey: string
  captchaImage: string
}

/** Mirrors backend {@code LoginResponse.UserInfo} */
export interface LoginUserInfo {
  userId: number
  username: string
  realName: string
  avatar?: string
  roles?: string[]
  permissions?: string[]
}

/** Mirrors backend {@code LoginResponse} */
export interface LoginResult {
  token: string
  userInfo: LoginUserInfo
  menus: unknown[]
}

export function login(params: LoginParams) {
  return request.post<LoginResult>('/auth/login', params)
}

export function logout() {
  return request.post<void>('/auth/logout')
}

export function getCaptcha() {
  return request.get<CaptchaResult>('/auth/captcha')
}
