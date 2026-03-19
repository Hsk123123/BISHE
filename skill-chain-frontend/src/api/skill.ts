import request from '@/utils/request'

export interface Skill {
  skillId?: number
  id?: number
  providerId?: number
  categoryId?: number
  title?: string
  description?: string
  pricePerUnit?: number
  price?: number
  unitType?: number
  unit?: string
  serviceMode?: number
  mediaUrls?: string
  status?: number
}

export interface SkillListParams {
  page?: number
  size?: number
  categoryId?: number
  keyword?: string
  sort?: number
}

export interface PublishSkillPayload {
  categoryId: number
  title: string
  description: string
  pricePerUnit: number
  unitType: number
  serviceMode: number
  mediaUrls?: string
}

export const getSkillList = (params: SkillListParams = {}) => {
  return request.get('/skill/list', { params })
}

export const getSkillDetail = (id: number | string) => {
  return request.get(`/skill/${id}`)
}

export const getSkillSchedule = (skillId: number | string, date: string) => {
  return request.get(`/skill/${skillId}/schedule`, { params: { date } })
}

export const publishSkill = (payload: PublishSkillPayload) => {
  return request.post('/skill/publish', payload)
}
