import request from '@/utils/request'

export interface CreateOrderParams {
  skillId: number | string
  scheduleDate: string
  timeSlot: string
}

export const getOrderList = (params?: { page?: number; size?: number; status?: number }) => {
  return request.get('/order/list', { params })
}

export const getProviderOrderList = (params?: { page?: number; size?: number; status?: number }) => {
  return request.get('/order/provider/list', { params })
}

export const getOrderDetail = (id: number | string) => {
  return request.get(`/order/${id}`)
}

export const createOrder = (params: CreateOrderParams) => {
  return request.post('/order/create', params)
}

export const payOrder = (id: number | string) => {
  return request.post(`/order/${id}/pay`)
}

export const cancelOrder = (id: number | string) => {
  return request.post(`/order/${id}/cancel`)
}

export const acceptOrder = (id: number | string) => {
  return request.post(`/order/${id}/accept`)
}

export const startServiceOrder = (id: number | string) => {
  return request.post(`/order/${id}/start`)
}

export const completeServiceOrder = (id: number | string) => {
  return request.post(`/order/${id}/complete`)
}
