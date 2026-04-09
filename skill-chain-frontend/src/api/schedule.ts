import request from '@/utils/request'

export interface ScheduleItem {
  scheduleId?: number
  providerId?: number
  skillId?: number | null
  date?: string
  timeSlot?: string
  status?: number   // 0=空闲 1=已预约 2=锁定
  location?: string
}

export const getMySchedules = (date?: string) =>
  request.get<ScheduleItem[]>('/schedule/my', { params: date ? { date } : {} })

export const addSchedule = (data: { date: string; timeSlot: string }) =>
  request.post('/schedule/add', data)

export const deleteSchedule = (id: number) =>
  request.delete(`/schedule/${id}`)
