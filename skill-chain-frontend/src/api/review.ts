import request from '@/utils/request'

export interface CreateReviewParams {
  orderId: number | string
  rating: number
  content?: string
}

export const createReview = (params: CreateReviewParams): Promise<null> => {
  return request.post('/review/create', params)
}