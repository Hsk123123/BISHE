<template>
  <div class="register-container">
    <div class="logo">
      <h1>{{ $t('common.platformName') }}</h1>
      <p>{{ $t('common.platformSlogan') }}</p>
    </div>

    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
          v-model="username"
          name="username"
          :label="$t('register.username')"
          :placeholder="$t('register.usernamePlaceholder')"
          :rules="[{ required: true, message: t('register.usernameRequired') }]"
        />
        <van-field
          v-model="phone"
          name="phone"
          :label="$t('register.phone')"
          :placeholder="$t('register.phonePlaceholder')"
          :rules="[{ required: true, message: t('register.phoneRequired') }]"
        />
        <van-field
          v-model="password"
          type="password"
          name="password"
          :label="$t('register.password')"
          :placeholder="$t('register.passwordPlaceholder')"
          :rules="[{ required: true, message: t('register.passwordRequired') }]"
        />
        <van-field
          v-model="confirmPassword"
          type="password"
          name="confirmPassword"
          :label="$t('register.confirmPassword')"
          :placeholder="$t('register.confirmPasswordPlaceholder')"
          :rules="[
            { required: true, message: t('register.confirmPasswordRequired') },
            { validator: validatePassword, message: t('register.passwordMismatch') }
          ]"
        />
      </van-cell-group>

      <div style="margin: 16px;">
        <van-button round block type="primary" native-type="submit" :loading="loading">
          {{ $t('register.submit') }}
        </van-button>
      </div>

      <div class="links">
        <van-button type="default" size="small" @click="goToLogin">{{ $t('register.backToLogin') }}</van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { showToast, showSuccessToast } from 'vant'
import { register } from '@/api/user'

const router = useRouter()
const { t } = useI18n()
const loading = ref(false)
const username = ref('')
const phone = ref('')
const password = ref('')
const confirmPassword = ref('')

const validatePassword = (val: string) => val === password.value

const onSubmit = async () => {
  loading.value = true
  try {
    await register({ username: username.value, password: password.value, phone: phone.value })
    showSuccessToast(t('register.success'))
    router.push('/login')
  } catch (error) {
    showToast(t('register.failed'))
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  padding: 60px 20px;
  min-height: 100vh;
  background: #f5f5f5;
}

.logo {
  text-align: center;
  margin-bottom: 60px;
}

.logo h1 {
  font-size: 32px;
  color: #667eea;
  margin-bottom: 10px;
}

.logo p {
  font-size: 14px;
  color: #999;
}

.links {
  display: flex;
  justify-content: center;
  padding: 0 20px;
  margin-top: 20px;
}
</style>