<template>
  <div class="certification-review">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>实名认证审核</span>
          <div class="header-actions">
            <el-select v-model="filterStatus" placeholder="认证状态" clearable style="width: 140px; margin-right: 10px;">
              <el-option label="待审核" :value="1" />
              <el-option label="已通过" :value="2" />
              <el-option label="已拒绝" :value="3" />
            </el-select>
            <el-button type="primary" @click="handleFilter">筛选</el-button>
            <el-button @click="refresh">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="certifications" style="width: 100%" v-loading="loading">
        <el-table-column prop="userId" label="用户ID" width="90" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="身份证正面" width="120">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewIdCard(scope.row)">查看</el-button>
          </template>
        </el-table-column>
        <el-table-column label="身份证反面" width="120">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewIdCardBack(scope.row)">查看</el-button>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.realNameStatus)">{{ getStatusText(scope.row.realNameStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="240">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row)">详情</el-button>
            <el-button v-if="scope.row.realNameStatus === 1" type="success" size="small" @click="handleApprove(scope.row)">通过</el-button>
            <el-button v-if="scope.row.realNameStatus === 1" type="danger" size="small" @click="handleReject(scope.row)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="认证详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ currentCertification.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentCertification.username || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentCertification.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentCertification.realNameStatus)">{{ getStatusText(currentCertification.realNameStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="身份证正面地址" :span="2">{{ currentCertification.idCardFront || '-' }}</el-descriptions-item>
        <el-descriptions-item label="身份证反面地址" :span="2">{{ currentCertification.idCardBack || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="idCardDialogVisible" :title="idCardDialogTitle" width="600px">
      <el-image :src="idCardImageUrl" fit="contain" style="width: 100%; max-height: 420px;" />
      <template #footer>
        <el-button @click="idCardDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { approveCertification, getAdminCertificationList, rejectCertification } from '@/api/admin'

interface CertificationItem {
  userId: number
  username?: string
  phone?: string
  realNameStatus: number
  idCardFront?: string
  idCardBack?: string
  updateTime?: string
}

interface CertPageResponse {
  records?: CertificationItem[]
  total?: number
}

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref<number | null>(null)
const detailDialogVisible = ref(false)
const idCardDialogVisible = ref(false)
const idCardDialogTitle = ref('身份证图片')
const idCardImageUrl = ref('')

const currentCertification = ref<CertificationItem>({
  userId: 0,
  realNameStatus: 1
})

const certifications = ref<CertificationItem[]>([])

const loadCertifications = async () => {
  loading.value = true
  try {
    const data = await getAdminCertificationList({
      page: currentPage.value,
      size: pageSize.value,
      status: filterStatus.value ?? undefined
    }) as CertPageResponse
    certifications.value = data?.records ?? []
    total.value = Number(data?.total ?? 0)
  } catch {
    certifications.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

watch([currentPage, pageSize], () => loadCertifications())

const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    1: '待审核',
    2: '已通过',
    3: '已拒绝'
  }
  return map[status] || '未知'
}

const refresh = () => {
  loadCertifications()
  ElMessage.success('刷新成功')
}

const handleFilter = () => {
  currentPage.value = 1
  loadCertifications()
}

const handleView = (row: CertificationItem) => {
  currentCertification.value = { ...row }
  detailDialogVisible.value = true
}

const viewIdCard = (row: CertificationItem) => {
  idCardDialogTitle.value = '身份证正面'
  idCardImageUrl.value = row.idCardFront || ''
  idCardDialogVisible.value = true
}

const viewIdCardBack = (row: CertificationItem) => {
  idCardDialogTitle.value = '身份证反面'
  idCardImageUrl.value = row.idCardBack || ''
  idCardDialogVisible.value = true
}

const handleApprove = async (row: CertificationItem) => {
  try {
    await ElMessageBox.confirm('确定通过该实名认证吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    await approveCertification(row.userId)
    ElMessage.success('审核通过')
    loadCertifications()
  } catch {
    // cancelled
  }
}

const handleReject = async (row: CertificationItem) => {
  try {
    await ElMessageBox.confirm('确定拒绝该实名认证吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await rejectCertification(row.userId)
    ElMessage.success('已拒绝')
    loadCertifications()
  } catch {
    // cancelled
  }
}

onMounted(() => loadCertifications())
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}
</style>
