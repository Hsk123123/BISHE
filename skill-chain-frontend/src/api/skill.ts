import request from '@/utils/request'

export interface Skill {
  skillId?: number
  id?: number
  providerId?: number
  providerName?: string
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
  orderCount?: number
  avgRating?: number
  rating?: number
  reviewCount?: number
}

export interface SkillListParams {
  page?: number
  size?: number
  categoryId?: number
  keyword?: string
  sort?: number
}

export interface SkillListResponse {
  records?: Skill[]
  total?: number
  size?: number
  current?: number
  pages?: number
}

export interface ScheduleItem {
  date: string
  timeSlot: string
  location?: string
}

export interface SkillScheduleItem {
  scheduleId?: number
  providerId?: number
  skillId?: number
  date?: string
  timeSlot?: string
  status?: number
  location?: string
  deleted?: number
}

export interface PublishSkillPayload {
  categoryId: number
  title: string
  description: string
  pricePerUnit: number
  unitType: number
  serviceMode: number
  mediaUrls?: string
  schedules?: ScheduleItem[]
}

export const getSkillList = (
  params: SkillListParams = {}
): Promise<SkillListResponse> => {
  return request.get('/skill/list', { params })
}

export const getSkillDetail = (
  id: number | string
): Promise<Skill> => {
  return request.get(`/skill/${id}`)
}

export const getSkillSchedule = (
  skillId: number | string,
  date: string
): Promise<SkillScheduleItem[]> => {
  return request.get(`/skill/${skillId}/schedule`, { params: { date } })
}

export const publishSkill = (
  payload: PublishSkillPayload
): Promise<null> => {
  return request.post('/skill/publish', payload)
}

export const getMySkills = (
  providerId: number | string
): Promise<Skill[]> => {
  return request.get(`/skill/provider/${providerId}`)
}

export const updateSkill = (
  id: number | string,
  payload: Partial<PublishSkillPayload>
): Promise<null> => {
  return request.put(`/skill/${id}`, payload)
}

export const deleteSkill = (
  id: number | string
): Promise<null> => {
  return request.delete(`/skill/${id}`)
}