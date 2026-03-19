import request from '@/utils/request'

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  password: string
  phone: string
}

export const login = (params: LoginParams) => {
  return request.post('/auth/login', params)
}

export const register = (params: RegisterParams) => {
  return request.post('/auth/register', params)
}

export const getUserInfo = () => {
  return request.get('/auth/info')
}

export const updateProfile = (params: any) => {
  return request.put('/auth/profile', params)
}

export const submitWorkerApplication = (params: any) => {
  return request.post('/user/worker-application/submit', params)
}

export const getMyWorkerApplication = () => {
  return request.get('/user/worker-application/my')
}

export const cancelMyWorkerApplication = (applicationId: number | string) => {
  return request.put(`/user/worker-application/${applicationId}/cancel`)
}
