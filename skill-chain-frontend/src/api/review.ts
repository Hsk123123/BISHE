import request from '@/utils/request'

export const createReview = (params: { orderId: number | string; rating: number; content?: string }) => {
  return request.post('/review/create', params)
}

export const getSkillReviews = (skillId: number | string, params?: { page?: number; size?: number }) => {
  return request.get(`/review/skill/${skillId}`, { params })
}
