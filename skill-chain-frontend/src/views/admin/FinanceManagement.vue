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
            <el-tag :type="scope.row.status === 'completed' ? 'success' : 'warning'">
              {{ scope.row.status === 'completed' ? '已完成' : '处理中' }}
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminWithdrawalList, approveWithdrawal, rejectWithdrawal } from '@/api/withdrawal'

interface Transaction {
  transactionId: number
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

const todayRevenue = ref(2580.00)
const monthRevenue = ref(85600.00)
const monthOrders = ref(342)
const pendingWithdrawals = ref(12800.00)
const pendingCount = ref(8)
const totalRevenue = ref(1256800.00)
const totalOrders = ref(4521)

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(200)
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

const transactions = ref<Transaction[]>([
  { transactionId: 1001, type: 'order', orderId: 2001, userName: '张三', providerName: '张阿姨', amount: 100.00, platformFee: 10.00, actualAmount: 90.00, status: 'completed', createTime: '2024-01-15 14:30:25' },
  { transactionId: 1002, type: 'order', orderId: 2002, userName: '李四', providerName: '李师傅', amount: 50.00, platformFee: 5.00, actualAmount: 45.00, status: 'completed', createTime: '2024-01-15 13:20:15' },
  { transactionId: 1003, type: 'withdrawal', userName: '张阿姨', amount: 500.00, platformFee: 0, status: 'completed', createTime: '2024-01-15 12:00:00' },
  { transactionId: 1004, type: 'order', orderId: 2003, userName: '王五', providerName: '王设计师', amount: 200.00, platformFee: 20.00, actualAmount: 180.00, status: 'completed', createTime: '2024-01-15 11:45:30' },
  { transactionId: 1005, type: 'refund', orderId: 2004, userName: '赵六', providerName: '赵老师', amount: 150.00, platformFee: 0, status: 'completed', createTime: '2024-01-15 10:30:00' },
  { transactionId: 1006, type: 'order', orderId: 2005, userName: '钱七', providerName: '孙老师', amount: 80.00, platformFee: 8.00, actualAmount: 72.00, status: 'pending', createTime: '2024-01-15 09:15:45' },
  { transactionId: 1007, type: 'order', orderId: 2006, userName: '周八', providerName: '周师傅', amount: 120.00, platformFee: 12.00, actualAmount: 108.00, status: 'completed', createTime: '2024-01-15 08:00:20' }
])

const withdrawalRequests = ref<WithdrawalRequest[]>([])

onMounted(() => loadWithdrawals())

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
  ElMessage.success('筛选成功')
}

const exportData = () => {
  ElMessage.success('导出成功')
}

const refreshWithdrawals = () => {
  loadWithdrawals()
  ElMessage.success('刷新成功')
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
