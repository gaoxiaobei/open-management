import axios, { type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import type { R } from '@/types/api'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

request.interceptors.response.use(
  (response: AxiosResponse<R>) => {
    const { code, message, data } = response.data
    if (code === 0) {
      return data as unknown as AxiosResponse
    }
    if (code === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    ElMessage.error(message || '请求失败')
    return Promise.reject(new Error(message))
  },
  (error) => {
    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  },
)

export default request
