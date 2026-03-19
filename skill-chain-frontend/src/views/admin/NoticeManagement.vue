<template>
  <div class="notice-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公告管理</span>
          <el-button type="primary" @click="handleAdd">发布公告</el-button>
        </div>
      </template>

      <el-table :data="notices" style="width: 100%" v-loading="loading">
        <el-table-column prop="noticeId" label="公告ID" width="90" />
        <el-table-column prop="title" label="标题" min-width="220" />
        <el-table-column label="类型" width="110">
          <template #default="scope">
            <el-tag :type="getTypeTag(scope.row.type)">{{ getTypeText(scope.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column label="操作" width="260">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button type="success" size="small" :disabled="scope.row.status === 1" @click="handlePublish(scope.row)">发布</el-button>
            <el-button type="warning" size="small" @click="handleEdit(scope.row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '发布公告'" width="720px">
      <el-form :model="currentNotice" label-width="90px">
        <el-form-item label="标题" required>
          <el-input v-model="currentNotice.title" maxlength="120" show-word-limit placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="类型" required>
          <el-select v-model="currentNotice.type" placeholder="请选择类型">
            <el-option label="系统公告" value="system" />
            <el-option label="活动公告" value="activity" />
            <el-option label="规则公告" value="rule" />
            <el-option label="更新日志" value="update" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input v-model="currentNotice.content" type="textarea" :rows="8" maxlength="3000" show-word-limit />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="currentNotice.endTime" type="datetime" placeholder="可选，不填为长期有效" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch v-model="currentNotice.isTop" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="currentNotice.status" :active-value="1" :inactive-value="0" active-text="立即发布" inactive-text="草稿" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="viewDialogVisible" title="公告详情" width="720px">
      <div class="notice-detail">
        <h2>{{ currentNotice.title }}</h2>
        <div class="meta-line">
          <el-tag :type="getTypeTag(currentNotice.type)">{{ getTypeText(currentNotice.type) }}</el-tag>
          <span>发布时间：{{ currentNotice.publishTime || '-' }}</span>
          <span>浏览量：{{ currentNotice.viewCount || 0 }}</span>
        </div>
        <el-divider />
        <div class="content">{{ currentNotice.content }}</div>
      </div>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createNotice, deleteNoticeByAdmin, getAdminNoticeList, publishNotice, updateNotice } from '@/api/admin'

interface NoticeItem {
  noticeId: number
  title: string
  type: string
  content: string
  status: number
  isTop: number
  viewCount: number
  publishTime?: string
  endTime?: string | null
}

interface PageResponse<T> {
  records?: T[]
  total?: number
}

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const isEdit = ref(false)

const notices = ref<NoticeItem[]>([])

const currentNotice = ref<NoticeItem>({
  noticeId: 0,
  title: '',
  type: 'system',
  content: '',
  status: 0,
  isTop: 0,
  viewCount: 0,
  publishTime: '',
  endTime: null
})

const loadNotices = async () => {
  loading.value = true
  try {
    const data = await getAdminNoticeList({
      page: currentPage.value,
      size: pageSize.value
    }) as PageResponse<NoticeItem>
    notices.value = data?.records ?? []
    total.value = Number(data?.total ?? 0)
  } catch {
    notices.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

watch([currentPage, pageSize], () => loadNotices())

const getTypeTag = (type: string) => {
  const map: Record<string, string> = {
    system: 'primary',
    activity: 'success',
    rule: 'warning',
    update: 'info'
  }
  return map[type] || 'info'
}

const getTypeText = (type: string) => {
  const map: Record<string, string> = {
    system: '系统公告',
    activity: '活动公告',
    rule: '规则公告',
    update: '更新日志'
  }
  return map[type] || type
}

const handleAdd = () => {
  isEdit.value = false
  currentNotice.value = {
    noticeId: 0,
    title: '',
    type: 'system',
    content: '',
    status: 0,
    isTop: 0,
    viewCount: 0,
    publishTime: '',
    endTime: null
  }
  dialogVisible.value = true
}

const handleEdit = (row: NoticeItem) => {
  isEdit.value = true
  currentNotice.value = { ...row }
  dialogVisible.value = true
}

const handleView = (row: NoticeItem) => {
  currentNotice.value = { ...row }
  viewDialogVisible.value = true
}

const handleSave = async () => {
  if (!currentNotice.value.title.trim()) {
    ElMessage.warning('请输入公告标题')
    return
  }
  if (!currentNotice.value.content.trim()) {
    ElMessage.warning('请输入公告内容')
    return
  }

  const payload = {
    title: currentNotice.value.title,
    type: currentNotice.value.type,
    content: currentNotice.value.content,
    status: currentNotice.value.status,
    isTop: currentNotice.value.isTop,
    endTime: currentNotice.value.endTime || null
  }

  try {
    if (isEdit.value) {
      await updateNotice(currentNotice.value.noticeId, payload)
      ElMessage.success('更新成功')
    } else {
      await createNotice(payload)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadNotices()
  } catch {
    // handled globally
  }
}

const handlePublish = async (row: NoticeItem) => {
  try {
    await ElMessageBox.confirm('确定发布该公告吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    await publishNotice(row.noticeId)
    ElMessage.success('发布成功')
    loadNotices()
  } catch {
    // cancelled
  }
}

const handleDelete = async (row: NoticeItem) => {
  try {
    await ElMessageBox.confirm('确定删除该公告吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteNoticeByAdmin(row.noticeId)
    ElMessage.success('删除成功')
    loadNotices()
  } catch {
    // cancelled
  }
}

onMounted(() => loadNotices())
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notice-detail h2 {
  margin: 0 0 12px 0;
}

.meta-line {
  display: flex;
  gap: 14px;
  color: #666;
  font-size: 13px;
  align-items: center;
}

.content {
  white-space: pre-wrap;
  line-height: 1.8;
}
</style>
