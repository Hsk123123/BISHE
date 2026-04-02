<template>
  <div class="application-container">
    <van-nav-bar title="申请成为技能提供者" left-arrow fixed @click-left="goBack" />

    <div class="application-content">
      <div v-if="loadFailed" class="application-status">
        <h3>加载失败</h3>
        <p>暂时无法获取申请信息，请稍后重试。</p>
        <van-button type="primary" block @click="loadMyApplication">重新加载</van-button>
      </div>

      <div v-else-if="applicationStatus === 'none'" class="application-form">
        <van-form>
          <van-field
            v-model.trim="form.realName"
            label="真实姓名"
            placeholder="请输入真实姓名"
          />
          <van-field
            v-model.trim="form.idCard"
            label="身份证号"
            placeholder="可随意填写"
          />
          <van-field
            v-model.trim="form.phone"
            label="手机号"
            placeholder="可随意填写"
          />
          <van-field
            v-model.trim="form.category"
            label="申请方向"
            placeholder="如：家政服务 / 教育培训 / 设计服务"
          />
          <van-field
            v-model.trim="form.description"
            label="申请说明"
            type="textarea"
            rows="4"
            placeholder="请简单介绍你的服务方向和个人情况"
          />
          <van-field
            v-model.trim="form.experience"
            label="从业经验"
            type="textarea"
            rows="3"
            placeholder="可选"
          />
          <van-field
            v-model.trim="form.serviceArea"
            label="服务范围"
            placeholder="可选"
          />
          <van-field
            v-model.trim="form.idCardFrontUrl"
            label="身份证正面URL"
            placeholder="可选"
          />
          <van-field
            v-model.trim="form.idCardBackUrl"
            label="身份证反面URL"
            placeholder="可选"
          />
          <van-field
            v-model.trim="form.certificateUrls"
            label="资质图片URL列表"
            type="textarea"
            rows="2"
            placeholder="可选，多个用逗号分隔"
          />

          <div class="submit-section">
            <van-button
              type="primary"
              block
              :loading="submitting"
              @click="submitApplication"
            >
              提交申请
            </van-button>
          </div>
        </van-form>
      </div>

      <div v-else class="application-status">
        <h3>{{ getStatusTitle(applicationStatus) }}</h3>
        <p>{{ getStatusDesc(applicationStatus) }}</p>

        <div class="status-box">
          <p>申请ID：{{ applicationInfo?.applicationId ?? '-' }}</p>
          <p>提交时间：{{ applicationInfo?.submitTime || applicationInfo?.createdAt || '-' }}</p>
          <p>申请方向：{{ applicationInfo?.category || '-' }}</p>
          <p>申请说明：{{ applicationInfo?.description || '-' }}</p>
          <p v-if="applicationInfo?.rejectReason">拒绝原因：{{ applicationInfo.rejectReason }}</p>
        </div>

        <van-button
          v-if="applicationStatus === 'pending'"
          type="danger"
          plain
          block
          @click="cancelApplication"
        >
          撤回申请
        </van-button>

        <van-button
          v-if="applicationStatus === 'approved'"
          type="primary"
          block
          @click="goToPublish"
        >
          去发布技能
        </van-button>

        <van-button
          v-if="applicationStatus === 'rejected' || applicationStatus === 'revoked'"
          type="primary"
          block
          @click="resetToApply"
        >
          重新申请
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showFailToast, showSuccessToast } from 'vant'
import {
  cancelMyWorkerApplication,
  getMyWorkerApplication,
  submitWorkerApplication
} from '@/api/user'

type StatusKey = 'none' | 'pending' | 'approved' | 'rejected' | 'revoked'

interface WorkerApplicationInfo {
  applicationId?: number
  status?: number
  submitTime?: string
  createdAt?: string
  category?: string
  description?: string
  rejectReason?: string
}

interface ApiError {
  response?: {
    data?: {
      message?: string
    }
  }
  message?: string
}

interface WorkerApplicationForm {
  realName: string
  idCard: string
  phone: string
  category: string
  description: string
  experience: string
  serviceArea: string
  idCardFrontUrl: string
  idCardBackUrl: string
  certificateUrls: string
}

const router = useRouter()
const submitting = ref(false)
const loadFailed = ref(false)
const applicationStatus = ref<StatusKey>('none')
const applicationInfo = ref<WorkerApplicationInfo | null>(null)

const createDefaultForm = (): WorkerApplicationForm => ({
  realName: '',
  idCard: '',
  phone: '',
  category: '',
  description: '',
  experience: '',
  serviceArea: '',
  idCardFrontUrl: '',
  idCardBackUrl: '',
  certificateUrls: ''
})

const form = ref<WorkerApplicationForm>(createDefaultForm())

const goBack = () => router.back()

const mapStatus = (status?: number): StatusKey => {
  if (status === 0) return 'pending'
  if (status === 1) return 'approved'
  if (status === 2) return 'rejected'
  if (status === 3) return 'revoked'
  return 'none'
}

const loadMyApplication = async () => {
  loadFailed.value = false
  try {
    const data = (await getMyWorkerApplication()) as unknown as WorkerApplicationInfo | null

    if (!data || !data.applicationId) {
      applicationStatus.value = 'none'
      applicationInfo.value = null
      return
    }

    applicationInfo.value = data
    applicationStatus.value = mapStatus(data.status)
  } catch (err) {
    const error = err as ApiError
    const message = error?.response?.data?.message || error?.message || ''

    if (message.includes('未找到') || message.includes('不存在')) {
      applicationStatus.value = 'none'
      applicationInfo.value = null
      loadFailed.value = false
      return
    }

    loadFailed.value = true
    applicationInfo.value = null
  }
}

const submitApplication = async () => {
  if (!form.value.realName.trim()) {
    showFailToast('请输入真实姓名')
    return
  }

  if (!form.value.category.trim()) {
    showFailToast('请输入申请方向')
    return
  }

  if (!form.value.description.trim()) {
    showFailToast('请输入申请说明')
    return
  }

  submitting.value = true
  try {
    await submitWorkerApplication({
      realName: form.value.realName.trim(),
      idCard: form.value.idCard.trim(),
      phone: form.value.phone.trim(),
      category: form.value.category.trim(),
      description: form.value.description.trim(),
      experience: form.value.experience.trim(),
      serviceArea: form.value.serviceArea.trim(),
      idCardFrontUrl: form.value.idCardFrontUrl.trim(),
      idCardBackUrl: form.value.idCardBackUrl.trim(),
      certificateUrls: form.value.certificateUrls.trim(),

      // 为兼容当前后端表结构补默认值
      skillTitle: '商家入驻申请',
      price: 0,
      unit: '次'
    })

    showSuccessToast('申请提交成功')
    await loadMyApplication()
  } catch (err) {
    const error = err as ApiError
    showFailToast(error?.response?.data?.message || error?.message || '提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

const cancelApplication = async () => {
  if (!applicationInfo.value?.applicationId) return

  try {
    await cancelMyWorkerApplication(applicationInfo.value.applicationId)
    showSuccessToast('已撤回申请')
    await loadMyApplication()
  } catch (err) {
    const error = err as ApiError
    showFailToast(error?.response?.data?.message || error?.message || '撤回失败')
  }
}

const resetToApply = () => {
  applicationStatus.value = 'none'
  applicationInfo.value = null
  form.value = createDefaultForm()
}

const goToPublish = () => router.push('/publish-skill')

const getStatusTitle = (status: StatusKey) => {
  const map: Record<StatusKey, string> = {
    none: '申请成为技能提供者',
    pending: '申请审核中',
    approved: '申请已通过',
    rejected: '申请未通过',
    revoked: '申请已撤回'
  }
  return map[status]
}

const getStatusDesc = (status: StatusKey) => {
  const map: Record<StatusKey, string> = {
    none: '填写基础信息并提交申请',
    pending: '管理员正在审核你的申请，请耐心等待。',
    approved: '你已通过审核，可以开始发布技能。',
    rejected: '你的申请未通过，请根据原因修改后重试。',
    revoked: '你已撤回申请，可以重新提交。'
  }
  return map[status]
}

onMounted(() => {
  loadMyApplication()
})
</script>

<style scoped>
.application-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-top: 46px;
}

.application-content {
  padding: 12px;
}

.application-form,
.application-status {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}

.submit-section {
  margin-top: 16px;
}

.status-box {
  margin: 12px 0 18px;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 8px;
  color: #555;
}

.status-box p {
  margin: 6px 0;
  line-height: 1.6;
}
</style>