<template>
  <div class="password-container">
    <van-nav-bar title="修改密码" left-arrow fixed @click-left="goBack" />

    <div class="form-content">
      <van-cell-group inset>
        <van-field
          v-model="form.oldPassword"
          type="password"
          label="原密码"
          placeholder="请输入原密码"
        />
        <van-field
          v-model="form.newPassword"
          type="password"
          label="新密码"
          placeholder="请输入新密码"
        />
        <van-field
          v-model="form.confirmPassword"
          type="password"
          label="确认密码"
          placeholder="请再次输入新密码"
        />
      </van-cell-group>

      <div class="tips">
        <p>⚠️ 当前为演示环境</p>
        <p>密码修改功能暂未接入后端，仅用于界面展示。</p>
      </div>

      <div class="submit-area">
        <van-button
          type="primary"
          block
          :loading="loading"
          @click="handleSubmit"
        >
          确认修改
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'

const router = useRouter()
const loading = ref(false)

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const goBack = () => {
  router.back()
}

const handleSubmit = async () => {
  if (!form.oldPassword) {
    showToast('请输入原密码')
    return
  }

  if (!form.newPassword) {
    showToast('请输入新密码')
    return
  }

  if (!form.confirmPassword) {
    showToast('请确认密码')
    return
  }

  if (form.newPassword !== form.confirmPassword) {
    showToast('两次密码不一致')
    return
  }

  loading.value = true

  // 模拟一点点延迟，让体验更自然
  setTimeout(() => {
    loading.value = false
    showSuccessToast('演示环境：未实际修改密码')
    router.back()
  }, 500)
}
</script>

<style scoped>
.password-container {
  padding-top: 46px;
  min-height: 100vh;
  background: #f5f5f5;
}

.form-content {
  padding: 15px;
}

.tips {
  margin-top: 16px;
  padding: 12px;
  background: #fff7e6;
  border-radius: 8px;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

.submit-area {
  margin-top: 24px;
}

:deep(.van-cell__title) {
  width: 80px;
}
</style>