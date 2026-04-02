<template>
  <div class="categories-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">新增分类</el-button>
            <el-button type="primary" @click="refresh">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="categories" style="width: 100%" v-loading="loading">
        <el-table-column prop="categoryId" label="分类ID" width="100" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="icon" label="图标" width="120">
          <template #default="scope">
            <el-icon v-if="scope.row.icon" size="20">
              <component :is="scope.row.icon" />
            </el-icon>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="500px">
      <el-form :model="currentCategory" label-width="90px" :rules="categoryRules" ref="categoryFormRef">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="currentCategory.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-select v-model="currentCategory.icon" placeholder="请选择图标" clearable>
            <el-option v-for="icon in iconOptions" :key="icon.value" :label="icon.label" :value="icon.value">
              <el-icon><component :is="icon.value" /></el-icon>
              <span style="margin-left: 8px;">{{ icon.label }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="currentCategory.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { HomeFilled, Gamepad, Picture, ChatDotRound, Reading, Tools, Music, Camera, Laptop, Bike, Football, Coffee } from '@element-plus/icons-vue'
import { getCategoryPage, createCategory, updateCategory, deleteCategory, type Category } from '@/api/category'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const categoryFormRef = ref<FormInstance>()

const iconOptions = [
  { label: '家政服务', value: 'HomeFilled' },
  { label: '游戏陪练', value: 'Gamepad' },
  { label: '设计服务', value: 'Picture' },
  { label: '咨询服务', value: 'ChatDotRound' },
  { label: '教育培训', value: 'Reading' },
  { label: '维修服务', value: 'Tools' },
  { label: '音乐教学', value: 'Music' },
  { label: '摄影服务', value: 'Camera' },
  { label: 'IT服务', value: 'Laptop' },
  { label: '运动陪练', value: 'Football' },
  { label: '跑腿代办', value: 'Bike' },
  { label: '餐饮服务', value: 'Coffee' }
]

const currentCategory = ref<Partial<Category>>({
  categoryId: 0,
  name: '',
  icon: '',
  sortOrder: 0
})

const categories = ref<Category[]>([])

const categoryRules: FormRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 20, message: '分类名称长度在2-20个字符', trigger: 'blur' }
  ]
}

const loadCategories = async () => {
  loading.value = true
  try {
    const data = (await getCategoryPage({
      page: currentPage.value,
      size: pageSize.value
    })) as { records?: Category[]; total?: number }

    categories.value = data?.records ?? []
    total.value = Number((data as any)?.total ?? categories.value.length)
  } catch (err) {
    categories.value = []
    total.value = 0
    ElMessage.error((err as Error)?.message || '加载分类列表失败')
  } finally {
    loading.value = false
  }
}

watch([currentPage, pageSize], () => loadCategories())

const refresh = async () => {
  await loadCategories()
  ElMessage.success('刷新成功')
}

const handleAdd = () => {
  isEdit.value = false
  currentCategory.value = {
    categoryId: 0,
    name: '',
    icon: '',
    sortOrder: 0
  }
  dialogVisible.value = true
}

const handleEdit = (row: Category) => {
  isEdit.value = true
  currentCategory.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!categoryFormRef.value) return

  await categoryFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      if (isEdit.value && currentCategory.value.categoryId) {
        await updateCategory(currentCategory.value.categoryId, currentCategory.value)
        ElMessage.success('分类更新成功')
      } else {
        await createCategory(currentCategory.value)
        ElMessage.success('分类创建成功')
      }
      dialogVisible.value = false
      await loadCategories()
    } catch (err) {
      ElMessage.error((err as Error)?.message || '操作失败')
    }
  })
}

const handleDelete = (row: Category) => {
  ElMessageBox.confirm('确定要删除该分类吗？删除后无法恢复', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteCategory(row.categoryId)
        ElMessage.success('删除成功')
        if (categories.value.length === 1 && currentPage.value > 1) {
          currentPage.value -= 1
        }
        await loadCategories()
      } catch (err) {
        ElMessage.error((err as Error)?.message || '删除失败')
      }
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}

onMounted(() => {
  loadCategories()
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
  gap: 8px;
}
</style>
