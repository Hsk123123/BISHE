import request from '@/utils/request'

export interface CreateOrderParams {
  skillId: number | string
  scheduleDate: string
  timeSlot: string
}

/**
 * 后端 OrderVO 对应类型
 */
export interface OrderVO {
  orderId?: number
  buyerId?: number
  providerId?: number
  skillId?: number
  amount?: number
  currencyType?: number
  status?: number
  scheduleDate?: string
  timeSlot?: string
  location?: string
  verificationCode?: string
  createTime?: string
  updateTime?: string

  skillTitle?: string
  description?: string
  workerName?: string
  workerAvatar?: string
  buyerName?: string
  buyerAvatar?: string
}

/**
 * 分页返回结构（你当前后端返回 records）
 */
export interface PageResult<T> {
  records?: T[]
  total?: number
}

/**
 * 获取用户订单列表
 */
export const getOrderList = (
  params?: { page?: number; size?: number; status?: number }
): Promise<PageResult<OrderVO>> => {
  return request.get('/order/list', { params })
}

/**
 * 商家订单列表
 */
export const getProviderOrderList = (
  params?: { page?: number; size?: number; status?: number }
): Promise<PageResult<OrderVO>> => {
  return request.get('/order/provider/list', { params })
}

export const getOrderDetail = (id: number | string): Promise<OrderVO> => {
  return request.get(`/order/${id}`)
}

export const createOrder = (params: CreateOrderParams): Promise<null> => {
  return request.post('/order/create', params)
}

export const payOrder = (id: number | string): Promise<null> => {
  return request.post(`/order/${id}/pay`)
}

export const cancelOrder = (id: number | string): Promise<null> => {
  return request.post(`/order/${id}/cancel`)
}

export const acceptOrder = (id: number | string): Promise<null> => {
  return request.post(`/order/${id}/accept`)
}

export const startServiceOrder = (id: number | string): Promise<null> => {
  return request.post(`/order/${id}/start`)
}

export const completeServiceOrder = (id: number | string): Promise<null> => {
  return request.post(`/order/${id}/complete`)
}

export const refundOrder = (id: number | string): Promise<null> => {
  return request.post(`/order/${id}/refund`)
}