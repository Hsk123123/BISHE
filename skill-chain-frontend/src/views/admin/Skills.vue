<template>
  <div class="skills-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>技能审核</span>
          <el-button type="primary" @click="refresh">刷新</el-button>
        </div>
      </template>

      <el-table :data="skills" style="width: 100%" v-loading="loading">
        <el-table-column prop="skillId" label="技能ID" width="90" />
        <el-table-column prop="title" label="技能名称" min-width="180" />
        <el-table-column prop="providerId" label="发布者ID" width="110" />
        <el-table-column label="价格" width="120">
          <template #default="scope">
            {{ formatMoney(scope.row.pricePerUnit) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="110">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button type="success" size="small" :disabled="scope.row.status !== 0" @click="handleApprove(scope.row)">通过</el-button>
            <el-button type="danger" size="small" :disabled="scope.row.status !== 0" @click="handleReject(scope.row)">拒绝</el-button>
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

    <el-dialog v-model="viewDialogVisible" title="技能详情" width="50%">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="技能ID">{{ currentSkill.skillId }}</el-descriptions-item>
        <el-descriptions-item label="技能名称">{{ currentSkill.title || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发布者ID">{{ currentSkill.providerId ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="分类ID">{{ currentSkill.categoryId ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="价格">{{ formatMoney(currentSkill.pricePerUnit) }}</el-descriptions-item>
        <el-descriptions-item label="服务方式">{{ currentSkill.serviceMode === 1 ? '线上' : '线下' }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ currentSkill.description || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { approveSkill, getAdminSkillList, rejectSkill } from '@/api/admin'

interface SkillItem {
  skillId: number
  providerId?: number
  categoryId?: number
  title?: string
  description?: string
  pricePerUnit?: number | string
  serviceMode?: number
  status?: number
}

interface SkillPageResponse {
  records?: SkillItem[]
  total?: number
}

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const viewDialogVisible = ref(false)

const currentSkill = ref<SkillItem>({
  skillId: 0
})

const skills = ref<SkillItem[]>([])

const loadSkills = async () => {
  loading.value = true
  try {
    const data = await getAdminSkillList({
      page: currentPage.value,
      size: pageSize.value
    }) as SkillPageResponse
    skills.value = data?.records ?? []
    total.value = Number(data?.total ?? 0)
  } catch {
    skills.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

watch([currentPage, pageSize], () => {
  loadSkills()
})

const getStatusType = (status: number | undefined) => {
  const types: Record<number, string> = {
    0: 'warning',
    1: 'success',
    2: 'info'
  }
  if (status === undefined) return 'info'
  return types[status] || 'info'
}

const getStatusText = (status: number | undefined) => {
  const texts: Record<number, string> = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  if (status === undefined) return '未知'
  return texts[status] || '未知'
}

const formatMoney = (value: number | string | undefined) => {
  if (value === undefined || value === null || value === '') return '-'
  const num = Number(value)
  if (Number.isNaN(num)) return String(value)
  return `¥${num.toFixed(2)}`
}

const refresh = () => {
  loadSkills()
  ElMessage.success('刷新成功')
}

const handleView = (row: SkillItem) => {
  currentSkill.value = { ...row }
  viewDialogVisible.value = true
}

const handleApprove = async (row: SkillItem) => {
  if (!row.skillId) return
  try {
    await ElMessageBox.confirm('确定通过该技能吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    await approveSkill(row.skillId)
    ElMessage.success('审核通过')
    loadSkills()
  } catch {
    // cancel or error already handled globally
  }
}

const handleReject = async (row: SkillItem) => {
  if (!row.skillId) return
  try {
    await ElMessageBox.confirm('确定拒绝该技能吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await rejectSkill(row.skillId)
    ElMessage.success('已拒绝')
    loadSkills()
  } catch {
    // cancel or error already handled globally
  }
}

onMounted(() => {
  loadSkills()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
