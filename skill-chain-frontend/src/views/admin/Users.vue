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
            <el-option label="商家" :value="1" />
            <el-option label="管理员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="users" style="width: 100%">
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="role" label="角色">
          <template #default="scope">
            <el-tag :type="getRoleType(scope.row.role)">
              {{ getRoleText(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="realNameStatus" label="实名状态">
          <template #default="scope">
            <el-tag :type="getRealNameType(scope.row.realNameStatus)">
              {{ getRealNameText(scope.row.realNameStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column prop="status" label="账户状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
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
          <el-tag :type="getRoleType(currentUser.role)">
            {{ getRoleText(currentUser.role) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="实名状态">
          <el-tag :type="getRealNameType(currentUser.realNameStatus)">
            {{ getRealNameText(currentUser.realNameStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="账户状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
            {{ currentUser.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ currentUser.createTime }}</el-descriptions-item>
        <el-descriptions-item label="最后登录" :span="2">{{ currentUser.lastLoginTime || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="currentUser" label-width="80px" :rules="userRules" ref="userFormRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="currentUser.username" placeholder="请输入用户名" :disabled="isEdit" />
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
            <el-option label="商家" :value="1" />
            <el-option label="管理员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="账户状态" prop="status">
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
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getAdminUserList, updateUserRole } from '@/api/admin'

interface User {
  userId: number
  username: string
  phone: string
  email?: string
  role: number
  realNameStatus: number
  createTime: string
  lastLoginTime?: string
  status?: number
}

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

const userRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在2-20个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const currentUser = ref<User>({
  userId: 0,
  username: '',
  phone: '',
  email: '',
  role: 0,
  realNameStatus: 0,
  createTime: '',
  status: 1
})

const users = ref<User[]>([])

const loadUsers = async () => {
  try {
    const data = await getAdminUserList({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchForm.username || undefined,
      role: searchForm.role ?? undefined
    }) as { records?: User[]; total?: number }
    users.value = (data?.records ?? []).map((r: any) => ({
      userId: r.userId,
      username: r.username ?? '',
      phone: r.phone ?? '',
      email: r.email,
      role: r.role ?? 0,
      realNameStatus: r.realNameStatus ?? 0,
      createTime: r.createTime ?? '',
      lastLoginTime: r.lastLoginTime,
      status: r.deleted === 1 ? 0 : 1
    }))
    total.value = (data as any)?.total ?? users.value.length
  } catch {
    users.value = []
  }
}

watch([currentPage, pageSize], () => loadUsers())

const getRoleType = (role: number) => {
  const types: Record<number, string> = {
    0: '',
    1: 'success',
    2: 'danger'
  }
  return types[role] || ''
}

const getRoleText = (role: number) => {
  const texts: Record<number, string> = {
    0: '用户',
    1: '商家',
    2: '管理员'
  }
  return texts[role] || '未知'
}

const getRealNameType = (status: number) => {
  const types: Record<number, string> = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return types[status] || 'info'
}

const getRealNameText = (status: number) => {
  const texts: Record<number, string> = {
    0: '未认证',
    1: '待审核',
    2: '已认证',
    3: '审核失败'
  }
  return texts[status] || '未知'
}

const refresh = () => {
  loadUsers()
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
  ElMessage.success('重置成功')
}

const handleAdd = () => {
  isEdit.value = false
  currentUser.value = {
    userId: 0,
    username: '',
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
    if (!currentUser.value.username || !currentUser.value.phone) {
      ElMessage.warning('请填写必填项')
      return
    }

    if (isEdit.value) {
      try {
        await updateUserRole(currentUser.value.userId, currentUser.value.role)
        ElMessage.success('用户更新成功')
        loadUsers()
        dialogVisible.value = false
      } catch (err) {
        ElMessage.error((err as Error)?.message || '更新失败')
      }
    } else {
      ElMessage.info('新增用户请使用注册功能')
      dialogVisible.value = false
    }
  })
}

const handleDelete = (row: User) => {
  ElMessageBox.confirm('确定要删除该用户吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.info('删除功能需调用后端API，暂未实现')
  }).catch(() => {
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
</style>