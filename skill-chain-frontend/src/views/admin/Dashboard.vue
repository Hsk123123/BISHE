<template>
  <div class="dashboard-page">
    <el-row :gutter="16">
      <el-col :xs="24" :sm="12" :md="12" :lg="6">
        <el-card class="metric-card" shadow="hover">
          <div class="metric-label">用户总数</div>
          <div class="metric-value">{{ metrics.totalUsers }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="12" :lg="6">
        <el-card class="metric-card" shadow="hover">
          <div class="metric-label">技能总数</div>
          <div class="metric-value">{{ metrics.totalSkills }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="12" :lg="6">
        <el-card class="metric-card" shadow="hover">
          <div class="metric-label">订单总数</div>
          <div class="metric-value">{{ metrics.totalOrders }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="12" :lg="6">
        <el-card class="metric-card" shadow="hover">
          <div class="metric-label">待审核技能</div>
          <div class="metric-value warning">{{ metrics.pendingSkills }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="row-gap">
      <el-col :xs="24" :md="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>管理待办</span>
            </div>
          </template>
          <div class="todo-list">
            <div class="todo-item">
              <span>退款中订单</span>
              <el-tag type="danger">{{ metrics.refundingOrders }}</el-tag>
            </div>
            <div class="todo-item">
              <span>待审核技能</span>
              <el-tag type="warning">{{ metrics.pendingSkills }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>快捷入口</span>
            </div>
          </template>
          <div class="action-list">
            <el-button @click="go('/admin/skills')">技能审核</el-button>
            <el-button @click="go('/admin/orders')">订单管理</el-button>
            <el-button @click="go('/admin/users')">用户管理</el-button>
            <el-button @click="go('/admin/worker-approvals')">工作者审核</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="row-gap">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>最新订单</span>
              <el-button text type="primary" @click="go('/admin/orders')">查看更多</el-button>
            </div>
          </template>
          <el-table :data="recentOrders" empty-text="暂无数据">
            <el-table-column prop="orderId" label="订单ID" width="100" />
            <el-table-column label="买家ID" width="100">
              <template #default="scope">
                {{ scope.row.buyerId ?? '-' }}
              </template>
            </el-table-column>
            <el-table-column label="金额" width="120">
              <template #default="scope">
                {{ formatMoney(scope.row.amount) }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getOrderStatusType(scope.row.status)">
                  {{ getOrderStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>待审核技能列表</span>
              <el-button text type="primary" @click="go('/admin/skills')">去审核</el-button>
            </div>
          </template>
          <el-table :data="pendingSkillList" empty-text="暂无待审核技能">
            <el-table-column prop="skillId" label="技能ID" width="100" />
            <el-table-column prop="title" label="技能名称" />
            <el-table-column label="发布者ID" width="120">
              <template #default="scope">
                {{ scope.row.providerId ?? '-' }}
              </template>
            </el-table-column>
            <el-table-column label="价格" width="120">
              <template #default="scope">
                {{ formatMoney(scope.row.pricePerUnit) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getAdminOrderList, getAdminSkillList, getAdminUserList } from '@/api/admin'

interface AdminUser {
  userId: number
  username?: string
}

interface AdminSkill {
  skillId: number
  title?: string
  providerId?: number
  pricePerUnit?: number | string
  status?: number
}

interface AdminOrder {
  orderId: number
  buyerId?: number
  amount?: number | string
  status?: number
}

interface AdminPageResponse<T> {
  records?: T[]
  total?: number
}

const router = useRouter()

const metrics = reactive({
  totalUsers: 0,
  totalSkills: 0,
  totalOrders: 0,
  pendingSkills: 0,
  refundingOrders: 0
})

const recentOrders = ref<AdminOrder[]>([])
const pendingSkillList = ref<AdminSkill[]>([])

const getTotal = async <T>(
  fetcher: (params: { page: number; size: number; status?: number }) => Promise<unknown>,
  params?: { status?: number }
) => {
  const data = await fetcher({
    page: 1,
    size: 1,
    ...params
  }) as AdminPageResponse<T>
  return Number(data?.total ?? 0)
}

const loadDashboardData = async () => {
  const [
    userTotal,
    skillTotal,
    orderTotal,
    pendingSkillTotal,
    refundingOrderTotal,
    orderData,
    pendingSkillData
  ] = await Promise.all([
    getTotal<AdminUser>((params) => getAdminUserList(params)),
    getTotal<AdminSkill>((params) => getAdminSkillList(params)),
    getTotal<AdminOrder>((params) => getAdminOrderList(params)),
    getTotal<AdminSkill>((params) => getAdminSkillList(params), { status: 0 }),
    getTotal<AdminOrder>((params) => getAdminOrderList(params), { status: 6 }),
    getAdminOrderList({ page: 1, size: 6 }) as Promise<AdminPageResponse<AdminOrder>>,
    getAdminSkillList({ page: 1, size: 6, status: 0 }) as Promise<AdminPageResponse<AdminSkill>>
  ])

  metrics.totalUsers = userTotal
  metrics.totalSkills = skillTotal
  metrics.totalOrders = orderTotal
  metrics.pendingSkills = pendingSkillTotal
  metrics.refundingOrders = refundingOrderTotal
  recentOrders.value = orderData?.records ?? []
  pendingSkillList.value = pendingSkillData?.records ?? []
}

const go = (path: string) => {
  router.push(path)
}

const formatMoney = (value: number | string | undefined) => {
  if (value === undefined || value === null || value === '') return '-'
  const num = Number(value)
  if (Number.isNaN(num)) return String(value)
  return `¥${num.toFixed(2)}`
}

const getOrderStatusType = (status: number | undefined) => {
  const statusMap: Record<number, string> = {
    0: 'warning',
    1: 'primary',
    2: 'primary',
    3: 'primary',
    4: 'primary',
    5: 'success',
    6: 'danger',
    7: 'danger',
    8: 'info'
  }
  if (status === undefined) return 'info'
  return statusMap[status] || 'info'
}

const getOrderStatusText = (status: number | undefined) => {
  const statusMap: Record<number, string> = {
    0: '待支付',
    1: '待接单',
    2: '已接单',
    3: '服务中',
    4: '待评价',
    5: '已完成',
    6: '退款中',
    7: '已退款',
    8: '已取消'
  }
  if (status === undefined) return '未知'
  return statusMap[status] || '未知'
}

onMounted(() => {
  loadDashboardData()
})
</script>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.metric-card {
  min-height: 108px;
}

.metric-label {
  color: #909399;
  font-size: 14px;
}

.metric-value {
  margin-top: 10px;
  font-size: 30px;
  color: #303133;
  font-weight: 600;
}

.metric-value.warning {
  color: #e6a23c;
}

.row-gap {
  margin-top: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.todo-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.todo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f2f2f2;
}

.todo-item:last-child {
  border-bottom: none;
}

.action-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

@media (max-width: 992px) {
  .metric-value {
    font-size: 26px;
  }
}
</style>
