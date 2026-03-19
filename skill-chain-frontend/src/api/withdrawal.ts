import request from '@/utils/request'

export const applyWithdrawal = (params: { amount: number; bankName?: string; bankCard?: string }) => {
  return request.post('/withdrawal/apply', params)
}

export const getMyWithdrawals = (params?: { page?: number; size?: number }) => {
  return request.get('/withdrawal/list', { params })
}

export const getEarningsStats = () => {
  return request.get('/withdrawal/earnings')
}

export const getAdminWithdrawalList = (params?: { page?: number; size?: number }) => {
  return request.get('/admin/withdrawal/list', { params })
}

export const approveWithdrawal = (id: number | string) => {
  return request.put(`/admin/withdrawal/${id}/approve`)
}

export const rejectWithdrawal = (id: number | string) => {
  return request.put(`/admin/withdrawal/${id}/reject`)
}
