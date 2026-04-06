import request from '@/utils/request'

export interface CreateReviewParams {
  orderId: number | string
  rating: number
  content?: string
}

export const createReview = (params: CreateReviewParams): Promise<null> => {
  return request.post('/review/create', params)
}

export const getReviewsBySkill = (
  skillId: number | string,
  params?: { page?: number; size?: number }
): Promise<{ records?: Array<{ reviewId?: number; rating?: number; content?: string; createTime?: string }> }> => {
  return request.get(`/review/skill/${skillId}`, { params })
}