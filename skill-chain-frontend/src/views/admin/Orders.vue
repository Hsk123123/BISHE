<template>
  <div class="orders-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
          <el-button type="primary" @click="refresh">刷新</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" style="margin-bottom: 20px;">
        <el-form-item label="订单ID">
          <el-input v-model="searchForm.orderId" placeholder="请输入订单ID" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待支付" :value="0" />
            <el-option label="待接单" :value="1" />
            <el-option label="已接单" :value="2" />
            <el-option label="服务中" :value="3" />
            <el-option label="待评价" :value="4" />
            <el-option label="已完成" :value="5" />
            <el-option label="退款中" :value="6" />
            <el-option label="已退款" :value="7" />
            <el-option label="已取消" :value="8" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="orders" style="width: 100%">
        <el-table-column prop="orderId" label="订单ID" width="80" />
        <el-table-column prop="buyerName" label="买家" />
        <el-table-column prop="skillTitle" label="技能名称" />
        <el-table-column prop="providerName" label="提供者" />
        <el-table-column prop="amount" label="金额" />
        <el-table-column prop="scheduleDate" label="预约日期" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button type="warning" size="small" @click="handleArbitrate(scope.row)" v-if="scope.row.status === 6">仲裁</el-button>
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

    <el-dialog v-model="viewDialogVisible" title="订单详情" width="60%">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单ID">{{ currentOrder.orderId }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.amount }}</el-descriptions-item>
        <el-descriptions-item label="买家">{{ currentOrder.buyerName }}</el-descriptions-item>
        <el-descriptions-item label="提供者">{{ currentOrder.providerName }}</el-descriptions-item>
        <el-descriptions-item label="技能名称">{{ currentOrder.skillTitle }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{ currentOrder.scheduleDate }}</el-descriptions-item>
        <el-descriptions-item label="时间段">{{ currentOrder.timeSlot }}</el-descriptions-item>
        <el-descriptions-item label="服务地点">{{ currentOrder.location }}</el-descriptions-item>
        <el-descriptions-item label="状态" :span="2">
          <el-tag :type="getStatusType(currentOrder.status)">
            {{ getStatusText(currentOrder.status) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="arbitrateDialogVisible" title="订单仲裁" width="50%">
      <el-form :model="arbitrateForm" label-width="100px">
        <el-form-item label="仲裁结果">
          <el-radio-group v-model="arbitrateForm.result">
            <el-radio :value="1">退款给买家</el-radio>
            <el-radio :value="2">打款给卖家</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="仲裁说明">
          <el-input v-model="arbitrateForm.remark" type="textarea" :rows="4" placeholder="请输入仲裁说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="arbitrateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitArbitrate">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminOrderList, arbitrateOrder } from '@/api/admin'

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const viewDialogVisible = ref(false)
const arbitrateDialogVisible = ref(false)

const searchForm = ref({
  orderId: '',
  status: null
})

const currentOrder = ref({
  orderId: 0,
  buyerName: '',
  providerName: '',
  skillTitle: '',
  amount: '',
  scheduleDate: '',
  timeSlot: '',
  location: '',
  status: 0
})

const arbitrateForm = ref({
  result: 1,
  remark: ''
})

const orders = ref<any[]>([])

const loadOrders = async () => {
  try {
    const keyword = (searchForm.value.orderId || '').trim()
    const data = await getAdminOrderList({
      page: currentPage.value,
      size: pageSize.value,
      status: searchForm.value.status ?? undefined,
      keyword: keyword || undefined
    }) as { records?: any[]; total?: number }
    orders.value = (data?.records ?? []).map((r: any) => ({
      ...r,
      buyerName: r.buyerName ?? (r.buyerId ? `用户#${r.buyerId}` : '-'),
      providerName: r.providerName ?? (r.providerId ? `服务者#${r.providerId}` : '-'),
      skillTitle: r.skillTitle ?? (r.skillId ? `技能#${r.skillId}` : '-')
    }))
    total.value = (data as any)?.total ?? orders.value.length
  } catch (err) {
    orders.value = []
    total.value = 0
    ElMessage.error((err as Error)?.message || '加载订单列表失败')
  }
}

watch([currentPage, pageSize], () => loadOrders())

const getStatusType = (status: number) => {
  const types: Record<number, string> = {
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
  return types[status] || 'info'
}

const getStatusText = (status: number) => {
  const texts: Record<number, string> = {
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
  return texts[status] || '未知'
}

const refresh = () => {
  loadOrders()
  ElMessage.success('刷新成功')
}

const handleSearch = () => {
  currentPage.value = 1
  loadOrders()
}

const handleReset = () => {
  searchForm.value = {
    orderId: '',
    status: null
  }
  ElMessage.success('重置成功')
}

const handleView = (row: any) => {
  currentOrder.value = { ...row }
  viewDialogVisible.value = true
}

const handleArbitrate = (row: any) => {
  currentOrder.value = { ...row }
  arbitrateDialogVisible.value = true
}

const submitArbitrate = async () => {
  const order = currentOrder.value
  if (!order?.orderId) return
  try {
    await arbitrateOrder(order.orderId, {
      result: arbitrateForm.value.result,
      remark: arbitrateForm.value.remark
    })
    arbitrateDialogVisible.value = false
    ElMessage.success('仲裁成功')
    loadOrders()
  } catch (err) {
    ElMessage.error((err as Error)?.message || '仲裁失败')
  }
}

onMounted(() => loadOrders())
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
