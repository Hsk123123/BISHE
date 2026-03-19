import request from '@/utils/request'

export interface Wallet {
  walletId?: number
  userId?: number
  cnyCoinBalance?: number
  pointBalance?: number
}

export const getWallet = () => {
  return request.get<Wallet>('/wallet')
}

export const checkIn = () => {
  return request.post<{ success: boolean; points: number }>('/wallet/checkin')
}

export const recharge = (amount: number) => {
  return request.post('/wallet/recharge', { amount })
}

export interface TransactionRecord {
  logId?: number
  type?: number
  amount?: number
  currency?: number
  description?: string
  createTime?: string
}

export const getTransactionList = (params?: { page?: number; size?: number }) => {
  return request.get('/transaction/list', { params: params || {} })
}
