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
      showToast(message || 'Request failed')
      return Promise.reject(new Error(message))
    }
  },
  (error) => {
    if (error.response?.status === 401) {
      useUserStore().clearToken()
      router.push('/login')
    }
    showToast(error.response?.data?.message || 'Network error')
    return Promise.reject(error)
  }
)

export default request