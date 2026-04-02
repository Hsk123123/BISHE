<template>
  <div class="worker-approvals">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>工作者申请审核</span>
          <div class="header-actions">
            <el-select
              v-model="filterStatus"
              placeholder="申请状态"
              clearable
              style="width: 140px; margin-right: 10px;"
            >
              <el-option label="待审核" :value="0" />
              <el-option label="已通过" :value="1" />
              <el-option label="已拒绝" :value="2" />
              <el-option label="已撤回" :value="3" />
            </el-select>
            <el-button type="primary" @click="handleFilter">筛选</el-button>
            <el-button @click="refresh">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="applications" style="width: 100%" v-loading="loading">
        <el-table-column prop="applicationId" label="申请ID" width="90" />
        <el-table-column prop="userId" label="用户ID" width="90" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="category" label="申请方向" width="140" />
        <el-table-column label="申请说明" min-width="220">
          <template #default="scope">
            {{ scope.row.description || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button
              v-if="scope.row.status === 0"
              type="success"
              size="small"
              @click="handleApprove(scope.row)"
            >
              通过
            </el-button>
            <el-button
              v-if="scope.row.status === 0"
              type="danger"
              size="small"
              @click="handleReject(scope.row)"
            >
              拒绝
            </el-button>
            <el-button
              v-if="scope.row.status !== 0"
              type="warning"
              size="small"
              @click="handleRevert(scope.row)"
            >
              撤回
            </el-button>
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

    <el-dialog v-model="detailDialogVisible" title="申请详情" width="760px">
      <el-descriptions :column="2" border v-if="currentApplication">
        <el-descriptions-item label="申请ID">
          {{ currentApplication.applicationId }}
        </el-descriptions-item>
        <el-descriptions-item label="用户ID">
          {{ currentApplication.userId }}
        </el-descriptions-item>
        <el-descriptions-item label="真实姓名">
          {{ currentApplication.realName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="手机号">
          {{ currentApplication.phone || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="身份证号">
          {{ currentApplication.idCard || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentApplication.status)">
            {{ getStatusText(currentApplication.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请方向">
          {{ currentApplication.category || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="服务范围">
          {{ currentApplication.serviceArea || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="申请说明" :span="2">
          {{ currentApplication.description || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="从业经验" :span="2">
          {{ currentApplication.experience || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="拒绝原因" :span="2">
          {{ currentApplication.rejectReason || '-' }}
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  approveWorkerApplication,
  getAdminWorkerApplicationList,
  rejectWorkerApplication,
  revertWorkerApplication
} from '@/api/admin'

type ElTagType = 'success' | 'warning' | 'info' | 'danger' | 'primary'

interface WorkerApplicationItem {
  applicationId: number
  userId: number
  realName?: string
  idCard?: string
  phone?: string
  category?: string
  description?: string
  experience?: string
  serviceArea?: string
  status: number
  rejectReason?: string
  submitTime?: string
}

interface WorkerPageResponse {
  records?: WorkerApplicationItem[]
  total?: number
}

const loading = ref(false)
const applications = ref<WorkerApplicationItem[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref<number | null>(null)
const detailDialogVisible = ref(false)
const currentApplication = ref<WorkerApplicationItem | null>(null)

const loadApplications = async () => {
  loading.value = true
  try {
    const data = (await getAdminWorkerApplicationList({
      page: currentPage.value,
      size: pageSize.value,
      status: filterStatus.value ?? undefined
    })) as WorkerPageResponse

    applications.value = data?.records ?? []
    total.value = Number(data?.total ?? 0)
  } catch {
    applications.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

watch([currentPage, pageSize], () => loadApplications())

const getStatusType = (status: number): ElTagType => {
  const map: Record<number, ElTagType> = {
    0: 'warning',
    1: 'success',
    2: 'danger',
    3: 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝',
    3: '已撤回'
  }
  return map[status] || '未知'
}

const handleFilter = () => {
  currentPage.value = 1
  loadApplications()
}

const refresh = () => {
  loadApplications()
  ElMessage.success('刷新成功')
}

const handleView = (row: WorkerApplicationItem) => {
  currentApplication.value = row
  detailDialogVisible.value = true
}

const handleApprove = async (row: WorkerApplicationItem) => {
  try {
    await ElMessageBox.confirm('确定通过该申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    await approveWorkerApplication(row.applicationId)
    ElMessage.success('审核通过')
    loadApplications()
  } catch {
    // cancelled
  }
}

const handleReject = async (row: WorkerApplicationItem) => {
  try {
    await ElMessageBox.prompt('请输入拒绝原因', '审核拒绝', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '可选'
    }).then(async ({ value }) => {
      await rejectWorkerApplication(row.applicationId, value)
      ElMessage.success('已拒绝')
      loadApplications()
    })
  } catch {
    // cancelled
  }
}

const handleRevert = async (row: WorkerApplicationItem) => {
  try {
    await ElMessageBox.confirm('确定撤回当前审核结果，恢复为待审核吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await revertWorkerApplication(row.applicationId)
    ElMessage.success('已撤回')
    loadApplications()
  } catch {
    // cancelled
  }
}

onMounted(() => loadApplications())
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