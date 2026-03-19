<template>
  <div class="password-container">
    <van-nav-bar title="修改密码" left-arrow fixed @click-left="goBack" />

    <div class="form-content">
      <van-form @submit="onSubmit">
        <van-cell-group inset>
          <van-field
            v-model="form.oldPassword"
            type="password"
            name="oldPassword"
            label="原密码"
            placeholder="请输入原密码"
            :rules="[{ required: true, message: '请输入原密码' }]"
          />
          <van-field
            v-model="form.newPassword"
            type="password"
            name="newPassword"
            label="新密码"
            placeholder="请输入新密码"
            :rules="[
              { required: true, message: '请输入新密码' },
              { min: 6, message: '密码至少6位' }
            ]"
          />
          <van-field
            v-model="form.confirmPassword"
            type="password"
            name="confirmPassword"
            label="确认密码"
            placeholder="请再次输入新密码"
            :rules="[
              { required: true, message: '请确认密码' },
              { validator: validateConfirm, message: '两次密码不一致' }
            ]"
          />
        </van-cell-group>

        <div class="tips">
          <p>密码要求：</p>
          <ul>
            <li>至少6个字符</li>
            <li>建议包含字母和数字</li>
          </ul>
        </div>

        <div class="submit-area">
          <van-button type="primary" native-type="submit" block :loading="isSubmitting">
            确认修改
          </van-button>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'

const router = useRouter()
const isSubmitting = ref(false)

const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirm = (val: string) => {
  return val === form.newPassword
}

const goBack = () => {
  router.back()
}

const onSubmit = async () => {
  if (form.newPassword !== form.confirmPassword) {
    showToast('两次密码不一致')
    return
  }

  isSubmitting.value = true
  await new Promise(resolve => setTimeout(resolve, 1000))
  isSubmitting.value = false

  showSuccessToast('密码修改成功')
  router.back()
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
  padding: 15px;
  color: #999;
  font-size: 13px;
}

.tips p {
  margin: 0 0 8px 0;
}

.tips ul {
  margin: 0;
  padding-left: 20px;
}

.tips li {
  margin-bottom: 4px;
}

.submit-area {
  margin: 30px 15px;
}

:deep(.van-cell__title) {
  width: 80px;
}
</style>
