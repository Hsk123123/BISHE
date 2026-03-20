import request from '@/utils/request'

export const getAdminUserList = (params?: { page?: number; size?: number; keyword?: string; role?: number }) => {
  return request.get('/admin/user/list', { params })
}

export const createAdminUser = (params: {
  username: string
  password: string
  phone: string
  email?: string
  role?: number
  status?: number
}) => {
  return request.post('/admin/user/create', params)
}

export const updateUserRole = (userId: number | string, role: number) => {
  return request.put(`/admin/user/${userId}/role`, { role })
}

export const deleteAdminUser = (userId: number | string) => {
  return request.delete(`/admin/user/${userId}`)
}

export const approveRealname = (userId: number | string) => {
  return request.put(`/admin/user/${userId}/approve-realname`)
}

export const rejectRealname = (userId: number | string) => {
  return request.put(`/admin/user/${userId}/reject-realname`)
}

export const getAdminSkillList = (params?: { page?: number; size?: number; status?: number }) => {
  return request.get('/admin/skill/list', { params })
}

export const approveSkill = (skillId: number | string) => {
  return request.put(`/admin/skill/${skillId}/approve`)
}

export const rejectSkill = (skillId: number | string) => {
  return request.put(`/admin/skill/${skillId}/reject`)
}

export const getAdminOrderList = (params?: { page?: number; size?: number; status?: number; keyword?: string }) => {
  return request.get('/admin/order/list', { params })
}

export const getAdminOrderDetail = (orderId: number | string) => {
  return request.get(`/admin/order/${orderId}`)
}

export const arbitrateOrder = (orderId: number | string, params: { result: number; remark?: string }) => {
  return request.post(`/admin/order/${orderId}/arbitrate`, params)
}

export const getAdminWorkerApplicationList = (params?: { page?: number; size?: number; status?: number }) => {
  return request.get('/admin/worker-application/list', { params })
}

export const approveWorkerApplication = (applicationId: number | string) => {
  return request.put(`/admin/worker-application/${applicationId}/approve`)
}

export const rejectWorkerApplication = (applicationId: number | string, reason?: string) => {
  return request.put(`/admin/worker-application/${applicationId}/reject`, { reason })
}

export const revertWorkerApplication = (applicationId: number | string) => {
  return request.put(`/admin/worker-application/${applicationId}/revert`)
}

export const getAdminCertificationList = (params?: { page?: number; size?: number; status?: number }) => {
  return request.get('/admin/certification/list', { params })
}

export const approveCertification = (userId: number | string) => {
  return request.put(`/admin/certification/${userId}/approve`)
}

export const rejectCertification = (userId: number | string) => {
  return request.put(`/admin/certification/${userId}/reject`)
}

export const getAdminReviewList = (params?: { page?: number; size?: number; status?: number }) => {
  return request.get('/admin/review/list', { params })
}

export const hideReview = (reviewId: number | string) => {
  return request.put(`/admin/review/${reviewId}/hide`)
}

export const publishReview = (reviewId: number | string) => {
  return request.put(`/admin/review/${reviewId}/publish`)
}

export const deleteReviewByAdmin = (reviewId: number | string) => {
  return request.delete(`/admin/review/${reviewId}`)
}

export const getAdminAppealList = (params?: { page?: number; size?: number; status?: number }) => {
  return request.get('/admin/review/appeal/list', { params })
}

export const resolveAppeal = (appealId: number | string, handleResult?: string) => {
  return request.put(`/admin/review/appeal/${appealId}/resolve`, { handleResult })
}

export const getAdminNoticeList = (params?: { page?: number; size?: number }) => {
  return request.get('/admin/notice/list', { params })
}

export const createNotice = (params: any) => {
  return request.post('/admin/notice/create', params)
}

export const updateNotice = (noticeId: number | string, params: any) => {
  return request.put(`/admin/notice/${noticeId}`, params)
}

export const publishNotice = (noticeId: number | string) => {
  return request.put(`/admin/notice/${noticeId}/publish`)
}

export const deleteNoticeByAdmin = (noticeId: number | string) => {
  return request.delete(`/admin/notice/${noticeId}`)
}
