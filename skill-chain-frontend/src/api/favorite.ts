import request from '@/utils/request'
import type { Skill } from '@/api/skill'

export const addFavorite = (skillId: number) =>
  request.post('/favorite/add', { skillId })

export const removeFavorite = (skillId: number) =>
  request.delete('/favorite/remove', { params: { skillId } })

export const getFavoriteList = (params?: { page?: number; size?: number }) =>
  request.get('/favorite/list', { params: params || {} })

export const getFavoriteCount = () =>
  request.get<number>('/favorite/count')

export const addHistory = (skillId: number) =>
  request.post('/history/add', { skillId })

export const getHistoryList = (params?: { page?: number; size?: number }) =>
  request.get('/history/list', { params: params || {} })

export const clearHistory = () =>
  request.delete('/history/clear')

export type { Skill }
