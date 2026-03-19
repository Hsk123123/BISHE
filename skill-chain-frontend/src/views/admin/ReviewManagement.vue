<template>
  <div class="review-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>评价管理</span>
          <div class="header-actions">
            <el-select v-model="filterStatus" placeholder="评价状态" clearable style="width: 140px; margin-right: 10px;">
              <el-option label="已发布" :value="1" />
              <el-option label="已隐藏" :value="2" />
            </el-select>
            <el-button type="primary" @click="handleFilter">筛选</el-button>
            <el-button @click="refresh">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="reviews" style="width: 100%" v-loading="loading">
        <el-table-column prop="reviewId" label="评价ID" width="90" />
        <el-table-column prop="orderId" label="订单ID" width="90" />
        <el-table-column prop="reviewerId" label="评价人ID" width="100" />
        <el-table-column prop="providerId" label="服务者ID" width="100" />
        <el-table-column prop="rating" label="评分" width="120">
          <template #default="scope">
            <el-rate v-model="scope.row.rating" disabled show-score text-color="#ff9900" />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="240" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.deleted === 1 ? 'info' : 'success'">
              {{ scope.row.deleted === 1 ? '已隐藏' : '已发布' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="260">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button v-if="scope.row.deleted !== 1" type="warning" size="small" @click="handleHide(scope.row)">隐藏</el-button>
            <el-button v-if="scope.row.deleted === 1" type="success" size="small" @click="handlePublish(scope.row)">恢复</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
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

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>申诉处理</span>
      </template>
      <el-table :data="appeals" style="width: 100%" v-loading="appealLoading">
        <el-table-column prop="appealId" label="申诉ID" width="90" />
        <el-table-column prop="orderId" label="订单ID" width="90" />
        <el-table-column prop="appealerId" label="申诉人ID" width="100" />
        <el-table-column prop="reason" label="申诉原因" width="140" />
        <el-table-column prop="description" label="申诉描述" min-width="200" />
        <el-table-column label="处理状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 2 ? 'success' : 'warning'">
              {{ scope.row.status === 2 ? '已处理' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleViewAppeal(scope.row)">查看</el-button>
            <el-button v-if="scope.row.status !== 2" type="success" size="small" @click="handleResolveAppeal(scope.row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="viewDialogVisible" title="评价详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="评价ID">{{ currentReview.reviewId }}</el-descriptions-item>
        <el-descriptions-item label="订单ID">{{ currentReview.orderId }}</el-descriptions-item>
        <el-descriptions-item label="评价人ID">{{ currentReview.reviewerId }}</el-descriptions-item>
        <el-descriptions-item label="服务者ID">{{ currentReview.providerId }}</el-descriptions-item>
        <el-descriptions-item label="评分">
          <el-rate v-model="currentReview.rating" disabled />
        </el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentReview.deleted === 1 ? '已隐藏' : '已发布' }}</el-descriptions-item>
        <el-descriptions-item label="评价内容" :span="2">{{ currentReview.content || '-' }}</el-descriptions-item>
        <el-descriptions-item label="商家回复" :span="2">{{ currentReview.replyContent || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="appealDialogVisible" title="申诉详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="申诉ID">{{ currentAppeal.appealId }}</el-descriptions-item>
        <el-descriptions-item label="订单ID">{{ currentAppeal.orderId }}</el-descriptions-item>
        <el-descriptions-item label="申诉人ID">{{ currentAppeal.appealerId }}</el-descriptions-item>
        <el-descriptions-item label="申诉原因">{{ currentAppeal.reason }}</el-descriptions-item>
        <el-descriptions-item label="申诉描述">{{ currentAppeal.description }}</el-descriptions-item>
        <el-descriptions-item label="处理结果">{{ currentAppeal.handleResult || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="appealDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteReviewByAdmin,
  getAdminAppealList,
  getAdminReviewList,
  hideReview,
  publishReview,
  resolveAppeal
} from '@/api/admin'

interface ReviewItem {
  reviewId: number
  orderId?: number
  reviewerId?: number
  providerId?: number
  rating: number
  content?: string
  replyContent?: string
  deleted?: number
  createTime?: string
}

interface AppealItem {
  appealId: number
  orderId?: number
  appealerId?: number
  reason?: string
  description?: string
  status?: number
  handleResult?: string
}

interface PageResponse<T> {
  records?: T[]
  total?: number
}

const loading = ref(false)
const appealLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref<number | null>(null)
const viewDialogVisible = ref(false)
const appealDialogVisible = ref(false)

const currentReview = ref<ReviewItem>({ reviewId: 0, rating: 5 })
const currentAppeal = ref<AppealItem>({ appealId: 0 })

const reviews = ref<ReviewItem[]>([])
const appeals = ref<AppealItem[]>([])

const loadReviews = async () => {
  loading.value = true
  try {
    const data = await getAdminReviewList({
      page: currentPage.value,
      size: pageSize.value,
      status: filterStatus.value ?? undefined
    }) as PageResponse<ReviewItem>
    reviews.value = data?.records ?? []
    total.value = Number(data?.total ?? 0)
  } catch {
    reviews.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const loadAppeals = async () => {
  appealLoading.value = true
  try {
    const data = await getAdminAppealList({ page: 1, size: 20 }) as PageResponse<AppealItem>
    appeals.value = data?.records ?? []
  } catch {
    appeals.value = []
  } finally {
    appealLoading.value = false
  }
}

watch([currentPage, pageSize], () => loadReviews())

const refresh = () => {
  loadReviews()
  loadAppeals()
  ElMessage.success('刷新成功')
}

const handleFilter = () => {
  currentPage.value = 1
  loadReviews()
}

const handleView = (row: ReviewItem) => {
  currentReview.value = { ...row }
  viewDialogVisible.value = true
}

const handleHide = async (row: ReviewItem) => {
  try {
    await ElMessageBox.confirm('确定隐藏该评价吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await hideReview(row.reviewId)
    ElMessage.success('已隐藏')
    loadReviews()
  } catch {
    // cancelled
  }
}

const handlePublish = async (row: ReviewItem) => {
  try {
    await publishReview(row.reviewId)
    ElMessage.success('已恢复发布')
    loadReviews()
  } catch {
    // handled by interceptor
  }
}

const handleDelete = async (row: ReviewItem) => {
  try {
    await ElMessageBox.confirm('确定删除该评价吗？删除后不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'danger'
    })
    await deleteReviewByAdmin(row.reviewId)
    ElMessage.success('删除成功')
    loadReviews()
  } catch {
    // cancelled
  }
}

const handleViewAppeal = (row: AppealItem) => {
  currentAppeal.value = { ...row }
  appealDialogVisible.value = true
}

const handleResolveAppeal = async (row: AppealItem) => {
  try {
    await ElMessageBox.prompt('请输入处理结果', '处理申诉', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '例如：已核实并隐藏评价'
    }).then(async ({ value }) => {
      await resolveAppeal(row.appealId, value)
      ElMessage.success('申诉已处理')
      loadAppeals()
    })
  } catch {
    // cancelled
  }
}

onMounted(() => {
  loadReviews()
  loadAppeals()
})
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
