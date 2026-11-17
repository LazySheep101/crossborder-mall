import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, clearAuth } from '@/utils/storage'
import router from '@/router'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 15000
})

request.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && typeof body.code === 'number' && body.code !== 200) {
      if (body.code === 401) {
        clearAuth()
        router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
      }
      ElMessage.error(body.message || '请求失败')
      return Promise.reject(body)
    }
    return body?.data
  },
  (error) => {
    const msg = error.response?.data?.message || error.message || '网络异常'
    if (error.response?.status === 401) {
      clearAuth()
      router.push({ name: 'login' })
    }
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default request
