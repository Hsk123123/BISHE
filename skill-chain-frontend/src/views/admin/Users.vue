<template>
  <div class="users-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">新增用户</el-button>
            <el-button type="primary" @click="refresh">刷新</el-button>
          </div>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" style="margin-bottom: 20px;">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="请选择角色" clearable style="width: 120px;">
            <el-option label="用户" :value="0" />
            <el-option label="工作者" :value="1" />
            <el-option label="管理员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="users" style="width: 100%" v-loading="loading">
        <el-table-column prop="userId" label="用户ID" width="90" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            <el-tag :type="getRoleType(scope.row.role)">{{ getRoleText(scope.row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="realNameStatus" label="实名状态" width="110">
          <template #default="scope">
            <el-tag :type="getRealNameType(scope.row.realNameStatus)">{{ getRealNameText(scope.row.realNameStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column prop="status" label="账号状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row)">查看</el-button>
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

    <el-dialog v-model="viewDialogVisible" title="用户详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ currentUser.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="getRoleType(currentUser.role)">{{ getRoleText(currentUser.role) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="实名状态">
          <el-tag :type="getRealNameType(currentUser.realNameStatus)">{{ getRealNameText(currentUser.realNameStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="账号状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
            {{ currentUser.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ currentUser.createTime || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="currentUser" label-width="90px" :rules="userRules" ref="userFormRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="currentUser.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="currentUser.password" type="password" show-password placeholder="请输入密码(至少6位)" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="currentUser.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="currentUser.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="currentUser.role" placeholder="请选择角色">
            <el-option label="用户" :value="0" />
            <el-option label="工作者" :value="1" />
            <el-option label="管理员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="账号状态" prop="status">
          <el-switch
            v-model="currentUser.status"
            :active-value="1"
            :inactive-value="0"
            active-text="正常"
            inactive-text="禁用"
          />
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
import { onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { createAdminUser, deleteAdminUser, getAdminUserList, updateUserRole } from '@/api/admin'

interface User {
  userId: number
  username: string
  password?: string
  phone: string
  email?: string
  role: number
  realNameStatus: number
  createTime: string
  status?: number
}

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const isEdit = ref(false)
const userFormRef = ref<FormInstance>()

const searchForm = reactive({
  username: '',
  phone: '',
  role: null as number | null
})

const currentUser = ref<User>({
  userId: 0,
  username: '',
  password: '',
  phone: '',
  email: '',
  role: 0,
  realNameStatus: 0,
  createTime: '',
  status: 1
})

const users = ref<User[]>([])

const userRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在2-20个字符', trigger: 'blur' }
  ],
  password: [
    {
      validator: (_rule: any, value: string, callback: (error?: Error) => void) => {
        if (isEdit.value) {
          callback()
          return
        }
        if (!value) {
          callback(new Error('请输入密码'))
          return
        }
        if (value.length < 6) {
          callback(new Error('密码至少6位'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
}

const loadUsers = async () => {
  loading.value = true
  try {
    const keyword = (searchForm.username || searchForm.phone || '').trim()
    const data = (await getAdminUserList({
      page: currentPage.value,
      size: pageSize.value,
      keyword: keyword || undefined,
      role: searchForm.role ?? undefined
    })) as { records?: User[]; total?: number }

    users.value = (data?.records ?? []).map((r: any) => ({
      userId: r.userId,
      username: r.username ?? '',
      phone: r.phone ?? '',
      email: r.email,
      role: r.role ?? 0,
      realNameStatus: r.realNameStatus ?? 0,
      createTime: r.createTime ?? '',
      status: r.deleted === 1 ? 0 : 1
    }))
    total.value = Number((data as any)?.total ?? users.value.length)
  } catch (err) {
    users.value = []
    total.value = 0
    ElMessage.error((err as Error)?.message || '加载用户列表失败')
  } finally {
    loading.value = false
  }
}

watch([currentPage, pageSize], () => loadUsers())

const getRoleType = (role: number) => {
  const types: Record<number, string> = { 0: '', 1: 'success', 2: 'danger' }
  return types[role] || ''
}

const getRoleText = (role: number) => {
  const texts: Record<number, string> = { 0: '用户', 1: '工作者', 2: '管理员' }
  return texts[role] || '未知'
}

const getRealNameType = (status: number) => {
  const types: Record<number, string> = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }
  return types[status] || 'info'
}

const getRealNameText = (status: number) => {
  const texts: Record<number, string> = { 0: '未认证', 1: '待审核', 2: '已认证', 3: '审核失败' }
  return texts[status] || '未知'
}

const refresh = async () => {
  await loadUsers()
  ElMessage.success('刷新成功')
}

const handleSearch = () => {
  currentPage.value = 1
  loadUsers()
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.phone = ''
  searchForm.role = null
  currentPage.value = 1
  loadUsers()
}

const handleAdd = () => {
  isEdit.value = false
  currentUser.value = {
    userId: 0,
    username: '',
    password: '',
    phone: '',
    email: '',
    role: 0,
    realNameStatus: 0,
    createTime: '',
    status: 1
  }
  dialogVisible.value = true
}

const handleView = (row: User) => {
  currentUser.value = { ...row }
  viewDialogVisible.value = true
}

const handleEdit = (row: User) => {
  isEdit.value = true
  currentUser.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!userFormRef.value) return

  await userFormRef.value.validate(async (valid) => {
    if (!valid) return

    if (isEdit.value) {
      try {
        await updateUserRole(currentUser.value.userId, currentUser.value.role)
        ElMessage.success('用户更新成功')
        dialogVisible.value = false
        await loadUsers()
      } catch (err) {
        ElMessage.error((err as Error)?.message || '更新失败')
      }
    } else {
      try {
        await createAdminUser({
          username: currentUser.value.username,
          password: currentUser.value.password || '',
          phone: currentUser.value.phone,
          email: currentUser.value.email,
          role: currentUser.value.role,
          status: currentUser.value.status ?? 1
        })
        ElMessage.success('用户创建成功')
        dialogVisible.value = false
        await loadUsers()
      } catch (err) {
        ElMessage.error((err as Error)?.message || '创建失败')
      }
    }
  })
}

const handleDelete = (row: User) => {
  ElMessageBox.confirm('确定要删除该用户吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await deleteAdminUser(row.userId)
        ElMessage.success('删除成功')
        if (users.value.length === 1 && currentPage.value > 1) {
          currentPage.value -= 1
        }
        await loadUsers()
      } catch (err) {
        ElMessage.error((err as Error)?.message || '删除失败')
      }
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}

onMounted(() => {
  loadUsers()
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
