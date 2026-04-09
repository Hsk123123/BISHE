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

export interface WithdrawalRecord {
  id?: number
  amount?: number
  fee?: number
  actualAmount?: number
  status?: number
  bankName?: string
  bankCard?: string
  createTime?: string
  processTime?: string
}

export const applyWithdrawal = (data: { amount: number; bankName: string; bankCard: string }) => {
  return request.post<WithdrawalRecord>('/withdrawal/apply', data)
}

export const getWithdrawalList = (params?: { page?: number; size?: number }) => {
  return request.get('/withdrawal/list', { params: params || {} })
}
