<template>
  <div class="application-container">
    <van-nav-bar title="申请成为技能提供者" left-arrow fixed @click-left="goBack" />

    <div class="application-content">
      <div v-if="applicationStatus === 'none'" class="application-form">
        <van-form @submit="submitApplication">
          <van-field v-model="form.realName" label="真实姓名" placeholder="请输入真实姓名" :rules="[{ required: true, message: '请输入真实姓名' }]" />
          <van-field v-model="form.idCard" label="身份证号" placeholder="请输入身份证号" :rules="[{ required: true, message: '请输入身份证号' }]" />
          <van-field v-model="form.phone" label="手机号" placeholder="请输入手机号" :rules="[{ required: true, message: '请输入手机号' }]" />
          <van-field v-model="form.skillTitle" label="技能标题" placeholder="请输入技能标题" :rules="[{ required: true, message: '请输入技能标题' }]" />
          <van-field v-model="form.category" label="技能分类" placeholder="请输入技能分类" :rules="[{ required: true, message: '请输入技能分类' }]" />
          <van-field v-model="form.description" label="技能描述" type="textarea" rows="4" placeholder="请输入技能描述" :rules="[{ required: true, message: '请输入技能描述' }]" />
          <van-field v-model="form.experience" label="从业经验" type="textarea" rows="3" placeholder="可选" />
          <van-field v-model.number="form.price" type="number" label="服务价格" placeholder="请输入价格" :rules="[{ required: true, message: '请输入价格' }]" />
          <van-field v-model="form.unit" label="计费单位" placeholder="如：小时/次" :rules="[{ required: true, message: '请输入计费单位' }]" />
          <van-field v-model="form.serviceArea" label="服务范围" placeholder="可选" />
          <van-field v-model="form.idCardFrontUrl" label="身份证正面URL" placeholder="可选" />
          <van-field v-model="form.idCardBackUrl" label="身份证反面URL" placeholder="可选" />
          <van-field v-model="form.certificateUrls" label="资质图片URL列表" type="textarea" rows="2" placeholder="可选，多个用逗号分隔" />

          <div class="submit-section">
            <van-button type="primary" block native-type="submit" :loading="submitting">提交申请</van-button>
          </div>
        </van-form>
      </div>

      <div v-else class="application-status">
        <h3>{{ getStatusTitle(applicationStatus) }}</h3>
        <p>{{ getStatusDesc(applicationStatus) }}</p>
        <div class="status-box">
          <p>申请ID：{{ applicationInfo?.applicationId }}</p>
          <p>提交时间：{{ applicationInfo?.submitTime || '-' }}</p>
          <p>技能标题：{{ applicationInfo?.skillTitle || '-' }}</p>
          <p v-if="applicationInfo?.rejectReason">拒绝原因：{{ applicationInfo.rejectReason }}</p>
        </div>

        <van-button v-if="applicationStatus === 'pending'" type="danger" plain block @click="cancelApplication">撤回申请</van-button>
        <van-button v-if="applicationStatus === 'approved'" type="primary" block @click="goToPublish">去发布技能</van-button>
        <van-button v-if="applicationStatus === 'rejected' || applicationStatus === 'revoked'" type="primary" block @click="resetToApply">重新申请</van-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showFailToast, showSuccessToast } from 'vant'
import { cancelMyWorkerApplication, getMyWorkerApplication, submitWorkerApplication } from '@/api/user'

type StatusKey = 'none' | 'pending' | 'approved' | 'rejected' | 'revoked'

const router = useRouter()
const submitting = ref(false)
const applicationStatus = ref<StatusKey>('none')
const applicationInfo = ref<any>(null)

const form = ref({
  realName: '',
  idCard: '',
  phone: '',
  skillTitle: '',
  category: '',
  description: '',
  experience: '',
  price: null as number | null,
  unit: '',
  serviceArea: '',
  idCardFrontUrl: '',
  idCardBackUrl: '',
  certificateUrls: ''
})

const goBack = () => router.back()

const mapStatus = (status?: number): StatusKey => {
  if (status === 0) return 'pending'
  if (status === 1) return 'approved'
  if (status === 2) return 'rejected'
  if (status === 3) return 'revoked'
  return 'none'
}

const loadMyApplication = async () => {
  try {
    const data = await getMyWorkerApplication() as any
    if (!data || !data.applicationId) {
      applicationStatus.value = 'none'
      applicationInfo.value = null
      return
    }
    applicationInfo.value = data
    applicationStatus.value = mapStatus(data.status)
  } catch {
    applicationStatus.value = 'none'
    applicationInfo.value = null
  }
}

const submitApplication = async () => {
  submitting.value = true
  try {
    await submitWorkerApplication({
      ...form.value,
      price: form.value.price ?? 0
    })
    showSuccessToast('申请提交成功')
    await loadMyApplication()
  } catch {
    showFailToast('提交失败，请重试')
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
  } catch {
    showFailToast('撤回失败')
  }
}

const resetToApply = () => {
  applicationStatus.value = 'none'
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
    none: '填写信息并提交申请',
    pending: '管理员正在审核你的申请，请耐心等待。',
    approved: '你已通过审核，可以开始发布技能。',
    rejected: '你的申请未通过，请根据原因修改后重试。',
    revoked: '你已撤回申请，可以重新提交。'
  }
  return map[status]
}

onMounted(() => loadMyApplication())
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
</style>
