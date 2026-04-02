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

export interface LoginResponse {
  token: string
  user: {
    role: number
    [key: string]: unknown
  }
}

export interface RegisterResponse {
  userId?: number
  username?: string
  [key: string]: unknown
}

export interface UserInfoResponse {
  userId?: number
  username?: string
  role?: number
  phone?: string
  avatar?: string
  nickname?: string
  [key: string]: unknown
}

export interface WorkerApplicationResponse {
  applicationId?: number
  status?: number
  reason?: string
  createdAt?: string
  [key: string]: unknown
}

export const login = (params: LoginParams): Promise<LoginResponse> => {
  return request.post('/auth/login', params)
}

export const register = (params: RegisterParams): Promise<RegisterResponse> => {
  return request.post('/auth/register', params)
}

export const getUserInfo = (): Promise<UserInfoResponse> => {
  return request.get('/auth/info')
}

export const updateProfile = (params: Record<string, unknown>): Promise<UserInfoResponse> => {
  return request.put('/auth/profile', params)
}

export const submitWorkerApplication = (
  params: Record<string, unknown>
): Promise<WorkerApplicationResponse> => {
  return request.post('/user/worker-application/submit', params)
}

export const getMyWorkerApplication = (): Promise<WorkerApplicationResponse> => {
  return request.get('/user/worker-application/my')
}

export const cancelMyWorkerApplication = (
  applicationId: number | string
): Promise<WorkerApplicationResponse> => {
  return request.put(`/user/worker-application/${applicationId}/cancel`)
}