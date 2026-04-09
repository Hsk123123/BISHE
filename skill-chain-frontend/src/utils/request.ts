import axios from 'axios'
import { showToast } from 'vant'
import router from '@/router'
import { useUserStore } from '@/store/user'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data
    if (code === 200) {
      return data
    } else {
      showToast(message || '请求失败')
      const error = new Error(message)
      ;(error as any).response = response
      return Promise.reject(error)
    }
  },
  (error) => {
    const status = error?.response?.status
    const serverMessage =
      error?.response?.data?.message ||
      error?.response?.data?.error ||
      error?.message ||
      '网络错误'

    if (status === 401) {
      useUserStore().clearToken()
      router.push('/login')
      showToast('登录已过期，请重新登录')
    } else if (status === 403) {
      showToast('无权限执行此操作')
    } else if (status === 404) {
      showToast(serverMessage || '资源不存在')
    } else if (status === 400) {
      showToast(serverMessage || '请求参数错误')
    } else {
      showToast(serverMessage)
    }

    return Promise.reject(error)
  }
)

export default request
