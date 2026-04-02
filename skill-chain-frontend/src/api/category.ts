import request from '@/utils/request'

export interface Category {
  categoryId: number
  name: string
  icon: string
  sortOrder: number
  createTime?: string
}

export const getCategoryList = () => {
  return request.get('/category/list')
}

export const getCategoryPage = (params?: { page?: number; size?: number }) => {
  return request.get('/category/page', { params })
}

export const getCategoryById = (id: number | string) => {
  return request.get(`/category/${id}`)
}

export const createCategory = (data: Partial<Category>) => {
  return request.post('/category', data)
}

export const updateCategory = (id: number | string, data: Partial<Category>) => {
  return request.put(`/category/${id}`, data)
}

export const deleteCategory = (id: number | string) => {
  return request.delete(`/category/${id}`)
}
