<template>
  <div class="finance-management">
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>今日收入</template>
          <div class="stat-value">¥{{ todayRevenue.toLocaleString() }}</div>
          <div class="stat-label">较昨日 +12.5%</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>本月收入</template>
          <div class="stat-value">¥{{ monthRevenue.toLocaleString() }}</div>
          <div class="stat-label">共 {{ monthOrders }} 笔订单</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>待处理提现</template>
          <div class="stat-value">¥{{ pendingWithdrawals.toLocaleString() }}</div>
          <div class="stat-label">{{ pendingCount }} 笔申请</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>平台总营收</template>
          <div class="stat-value">¥{{ totalRevenue.toLocaleString() }}</div>
          <div class="stat-label">累计 {{ totalOrders }} 笔订单</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>交易记录</span>
          <div class="header-actions">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              size="default"
              style="width: 240px; margin-right: 10px;"
            />
            <el-select v-model="filterType" placeholder="交易类型" clearable style="width: 120px; margin-right: 10px;">
              <el-option label="全部" value="" />
              <el-option label="订单收入" value="order" />
              <el-option label="提现支出" value="withdrawal" />
              <el-option label="退款" value="refund" />
            </el-select>
            <el-button type="primary" @click="handleFilter">筛选</el-button>
            <el-button @click="exportData">导出</el-button>
          </div>
        </div>
      </template>

      <el-table :data="transactions" style="width: 100%">
        <el-table-column prop="transactionId" label="交易ID" width="100" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="getTypeTag(scope.row.type)">
              {{ getTypeText(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderId" label="关联订单" width="120">
          <template #default="scope">
            <span v-if="scope.row.orderId">{{ scope.row.orderId }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="providerName" label="服务者" width="100">
          <template #default="scope">
            <span v-if="scope.row.providerName">{{ scope.row.providerName }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="scope">
            <span :class="getAmountClass(scope.row)">
              {{ scope.row.type === 'withdrawal' || scope.row.type === 'refund' ? '-' : '+' }}¥{{ scope.row.amount.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="platformFee" label="平台服务费" width="120">
          <template #default="scope">
            <span v-if="scope.row.type === 'order'" class="fee-amount">-¥{{ scope.row.platformFee.toFixed(2) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="实际金额" width="120">
          <template #default="scope">
            <span v-if="scope.row.actualAmount !== undefined" :class="getAmountClass(scope.row)">
              {{ scope.row.type === 'withdrawal' ? '-' : '+' }}¥{{ scope.row.actualAmount.toFixed(2) }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'completed' ? 'success' : scope.row.status === 'rejected' ? 'danger' : 'warning'">
              {{ scope.row.status === 'completed' ? '已完成' : scope.row.status === 'rejected' ? '已拒绝' : '处理中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="交易时间" width="180" />
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

    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>提现申请</span>
          <el-button type="primary" @click="refreshWithdrawals">刷新</el-button>
        </div>
      </template>

      <el-table :data="withdrawalRequests" style="width: 100%">
        <el-table-column prop="withdrawalId" label="申请ID" width="100" />
        <el-table-column prop="providerName" label="服务者" width="120" />
        <el-table-column prop="bankName" label="银行" width="120" />
        <el-table-column prop="accountLast4" label="卡号后四位" width="120" />
        <el-table-column prop="amount" label="提现金额" width="120">
          <template #default="scope">
            <span class="amount-negative">-¥{{ scope.row.amount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="fee" label="手续费" width="100">
          <template #default="scope">
            -¥{{ scope.row.fee.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="实际到账" width="120">
          <template #default="scope">
            ¥{{ scope.row.actualAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getWithdrawalStatusType(scope.row.status)">
              {{ getWithdrawalStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column prop="processTime" label="处理时间" width="180">
          <template #default="scope">
            <span v-if="scope.row.processTime">{{ scope.row.processTime }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleViewWithdrawal(scope.row)">查看</el-button>
            <el-button 
              v-if="scope.row.status === 'pending'" 
              type="success" 
              size="small" 
              @click="handleApproveWithdrawal(scope.row)"
            >
              通过
            </el-button>
            <el-button 
              v-if="scope.row.status === 'pending'" 
              type="danger" 
              size="small" 
              @click="handleRejectWithdrawal(scope.row)"
            >
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="withdrawalDialogVisible" title="提现详情" width="500px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请ID">{{ currentWithdrawal.withdrawalId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getWithdrawalStatusType(currentWithdrawal.status)">
            {{ getWithdrawalStatusText(currentWithdrawal.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="服务者">{{ currentWithdrawal.providerName }}</el-descriptions-item>
        <el-descriptions-item label="银行">{{ currentWithdrawal.bankName }}</el-descriptions-item>
        <el-descriptions-item label="银行卡号">{{ currentWithdrawal.bankCardNumber }}</el-descriptions-item>
        <el-descriptions-item label="提现金额">¥{{ currentWithdrawal.amount?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="手续费">¥{{ currentWithdrawal.fee?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="实际到账">¥{{ currentWithdrawal.actualAmount?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="可提现余额">¥{{ currentWithdrawal.availableBalance?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentWithdrawal.createTime }}</el-descriptions-item>
        <el-descriptions-item label="处理时间" :span="2">
          {{ currentWithdrawal.processTime || '待处理' }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          {{ currentWithdrawal.remark || '无' }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="withdrawalDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminFinanceOverview, getAdminFinanceTransactions } from '@/api/admin'
import { getAdminWithdrawalList, approveWithdrawal, rejectWithdrawal } from '@/api/withdrawal'

interface Transaction {
  transactionId: string
  type: string
  orderId?: number
  userName: string
  providerName?: string
  amount: number
  platformFee: number
  actualAmount?: number
  status: string
  createTime: string
}

interface WithdrawalRequest {
  withdrawalId: number
  providerName: string
  bankName: string
  accountLast4: string
  bankCardNumber?: string
  amount: number
  fee: number
  actualAmount: number
  availableBalance?: number
  status: string
  createTime: string
  processTime?: string
  remark?: string
}

const todayRevenue = ref(0)
const monthRevenue = ref(0)
const monthOrders = ref(0)
const pendingWithdrawals = ref(0)
const pendingCount = ref(0)
const totalRevenue = ref(0)
const totalOrders = ref(0)

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dateRange = ref<[Date, Date] | null>(null)
const filterType = ref('')
const withdrawalDialogVisible = ref(false)

const currentWithdrawal = ref<WithdrawalRequest>({
  withdrawalId: 0,
  providerName: '',
  bankName: '',
  accountLast4: '',
  amount: 0,
  fee: 0,
  actualAmount: 0,
  status: '',
  createTime: ''
})

const transactions = ref<Transaction[]>([])

const withdrawalRequests = ref<WithdrawalRequest[]>([])
watch([currentPage, pageSize], () => {
  loadTransactions()
})

const getTypeTag = (type: string) => {
  const tags: Record<string, string> = {
    order: 'success',
    withdrawal: 'danger',
    refund: 'warning'
  }
  return tags[type] || 'info'
}

const getTypeText = (type: string) => {
  const texts: Record<string, string> = {
    order: '订单收入',
    withdrawal: '提现支出',
    refund: '退款'
  }
  return texts[type] || type
}

const getAmountClass = (row: Transaction) => {
  return row.type === 'order' ? 'amount-positive' : 'amount-negative'
}

const getWithdrawalStatusType = (status: string) => {
  const types: Record<string, string> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    completed: 'success'
  }
  return types[status] || 'info'
}

const getWithdrawalStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝',
    completed: '已完成'
  }
  return texts[status] || status
}

const handleFilter = () => {
  currentPage.value = 1
  loadTransactions()
  ElMessage.success('筛选成功')
}

const exportData = () => {
  if (!transactions.value.length) {
    ElMessage.warning('暂无可导出数据')
    return
  }
  const headers = ['交易ID', '类型', '关联订单', '用户', '服务者', '金额', '平台服务费', '实际金额', '状态', '交易时间']
  const lines = transactions.value.map((row) => [
    row.transactionId,
    getTypeText(row.type),
    row.orderId ?? '',
    row.userName ?? '',
    row.providerName ?? '',
    row.amount ?? 0,
    row.platformFee ?? 0,
    row.actualAmount ?? 0,
    row.status === 'completed' ? '已完成' : '处理中',
    row.createTime ?? ''
  ])
  const csv = [headers, ...lines].map((line) => line.map((v) => `"${String(v).replace(/"/g, '""')}"`).join(',')).join('\n')
  const blob = new Blob([`\ufeff${csv}`], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `finance-transactions-${new Date().toISOString().slice(0, 10)}.csv`
  link.click()
  URL.revokeObjectURL(url)
  ElMessage.success('导出成功')
}

const refreshWithdrawals = () => {
  loadWithdrawals()
  ElMessage.success('刷新成功')
}

const toNumber = (value: unknown) => {
  const n = Number(value ?? 0)
  return Number.isNaN(n) ? 0 : n
}

const formatDateToYmd = (date: Date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const formatDateTime = (value: unknown) => {
  if (!value) return ''
  const text = String(value)
  if (text.includes('T')) {
    return text.replace('T', ' ').slice(0, 19)
  }
  return text
}

const loadOverview = async () => {
  try {
    const data = await getAdminFinanceOverview() as any
    todayRevenue.value = toNumber(data?.todayRevenue)
    monthRevenue.value = toNumber(data?.monthRevenue)
    monthOrders.value = toNumber(data?.monthOrders)
    pendingWithdrawals.value = toNumber(data?.pendingWithdrawals)
    pendingCount.value = toNumber(data?.pendingCount)
    totalRevenue.value = toNumber(data?.totalRevenue)
    totalOrders.value = toNumber(data?.totalOrders)
  } catch {
    todayRevenue.value = 0
    monthRevenue.value = 0
    monthOrders.value = 0
    pendingWithdrawals.value = 0
    pendingCount.value = 0
    totalRevenue.value = 0
    totalOrders.value = 0
  }
}

const loadTransactions = async () => {
  try {
    const params: any = {
      page: currentPage.value,
      size: pageSize.value,
      type: filterType.value || undefined
    }
    if (dateRange.value) {
      params.startDate = formatDateToYmd(dateRange.value[0])
      params.endDate = formatDateToYmd(dateRange.value[1])
    }
    const data = await getAdminFinanceTransactions(params) as { records?: any[]; total?: number }
    transactions.value = (data?.records ?? []).map((r: any) => ({
      transactionId: String(r.transactionId ?? ''),
      type: r.type ?? 'order',
      orderId: r.orderId ?? undefined,
      userName: r.userName ?? '-',
      providerName: r.providerName ?? '-',
      amount: toNumber(r.amount),
      platformFee: toNumber(r.platformFee),
      actualAmount: toNumber(r.actualAmount),
      status: r.status ?? 'pending',
      createTime: formatDateTime(r.createTime)
    }))
    total.value = toNumber(data?.total)
  } catch {
    transactions.value = []
    total.value = 0
  }
}

const loadWithdrawals = async () => {
  try {
    const data = await getAdminWithdrawalList({ page: 1, size: 50 }) as { records?: any[]; total?: number }
    const list = data?.records ?? []
    withdrawalRequests.value = list.map((r: any) => ({
      withdrawalId: r.id,
      providerName: r.providerName ?? `用户${r.userId}`,
      bankName: r.bankName ?? '银行卡',
      accountLast4: (r.bankCard || '').slice(-4),
      bankCardNumber: (r.bankCard || '').replace(/(.{4}).*(.{4})/, '$1********$2'),
      amount: Number(r.amount ?? 0),
      fee: Number(r.fee ?? 0),
      actualAmount: Number(r.actualAmount ?? 0),
      availableBalance: Number(r.amount ?? 0),
      status: r.status === 0 ? 'pending' : r.status === 1 ? 'completed' : 'rejected',
      createTime: r.createTime ?? '',
      processTime: r.processTime ?? '',
      remark: r.remark ?? ''
    }))
    pendingWithdrawals.value = withdrawalRequests.value
      .filter((r: any) => r.status === 'pending')
      .reduce((s: number, r: any) => s + r.amount, 0)
    pendingCount.value = withdrawalRequests.value.filter((r: any) => r.status === 'pending').length
  } catch {
    withdrawalRequests.value = []
  }
}

const handleViewWithdrawal = (row: WithdrawalRequest) => {
  currentWithdrawal.value = { ...row }
  withdrawalDialogVisible.value = true
}

const handleApproveWithdrawal = (row: WithdrawalRequest) => {
  ElMessageBox.confirm('确定要通过该提现申请吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    try {
      await approveWithdrawal(row.withdrawalId)
      row.status = 'completed'
      row.processTime = new Date().toLocaleString('zh-CN')
      pendingWithdrawals.value -= row.amount
      pendingCount.value--
      ElMessage.success('提现申请已通过')
      loadWithdrawals()
    } catch (err) {
      ElMessage.error((err as Error)?.message || '操作失败')
    }
  }).catch(() => ElMessage.info('已取消'))
}

const handleRejectWithdrawal = (row: WithdrawalRequest) => {
  ElMessageBox.confirm('确定要拒绝该提现申请吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await rejectWithdrawal(row.withdrawalId)
      row.status = 'rejected'
      row.processTime = new Date().toLocaleString('zh-CN')
      pendingWithdrawals.value -= row.amount
      pendingCount.value--
      ElMessage.success('提现申请已拒绝')
      loadWithdrawals()
    } catch (err) {
      ElMessage.error((err as Error)?.message || '操作失败')
    }
  }).catch(() => ElMessage.info('已取消'))
}

onMounted(() => {
  loadOverview()
  loadTransactions()
  loadWithdrawals()
})
</script>

<style scoped>
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.amount-positive {
  color: #67c23a;
  font-weight: bold;
}

.amount-negative {
  color: #f56c6c;
  font-weight: bold;
}

.fee-amount {
  color: #909399;
}
</style>
