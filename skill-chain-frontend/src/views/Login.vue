<template>
  <div class="login-container">
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="login-content">
      <div class="logo-section">
        <div class="logo-icon">
          <svg viewBox="0 0 100 100" class="logo-svg">
            <circle cx="50" cy="50" r="45" fill="none" stroke="currentColor" stroke-width="2" />
            <circle cx="50" cy="50" r="35" fill="none" stroke="currentColor" stroke-width="2" />
            <circle cx="50" cy="50" r="25" fill="none" stroke="currentColor" stroke-width="2" />
            <circle cx="50" cy="50" r="8" fill="currentColor" />
          </svg>
        </div>
        <h1 class="platform-name">{{ $t('common.platformName') }}</h1>
        <p class="platform-slogan">{{ $t('common.platformSlogan') }}</p>
      </div>

      <van-form class="login-form">
        <van-cell-group inset class="form-group">
          <van-field
            v-model.trim="username"
            name="username"
            :label="$t('login.username')"
            :placeholder="$t('login.usernamePlaceholder')"
            left-icon="user-o"
            size="large"
            :rules="[{ required: true, message: t('login.usernameRequired') }]"
            class="custom-field"
          />
          <van-field
            v-model="password"
            type="password"
            name="password"
            :label="$t('login.password')"
            :placeholder="$t('login.passwordPlaceholder')"
            left-icon="lock"
            size="large"
            :rules="[{ required: true, message: t('login.passwordRequired') }]"
            class="custom-field"
            @keyup.enter="onSubmit"
          />
        </van-cell-group>

        <div class="form-options">
          <van-checkbox v-model="rememberPassword" shape="round" icon-size="16px">
            {{ $t('login.rememberPassword') }}
          </van-checkbox>
          <span class="forgot-password" @click="goToForgotPassword">
            {{ $t('login.forgotPassword') }}
          </span>
        </div>

        <div class="submit-section">
          <van-button
            round
            block
            type="primary"
            :loading="loading"
            class="submit-button"
            @click="onSubmit"
          >
            {{ $t('login.submit') }}
          </van-button>
        </div>

        <div class="register-section">
          <span class="register-text">{{ $t('login.noAccount') }}</span>
          <span class="register-link" @click="goToRegister">
            {{ $t('login.registerNow') }}
          </span>
        </div>
      </van-form>

      <div class="footer-info">
        <p>{{ $t('login.agreeTerms') }}</p>
        <p>
          <span class="link">{{ $t('login.userAgreement') }}</span>
          <span class="divider">{{ $t('login.and') }}</span>
          <span class="link">{{ $t('login.privacyPolicy') }}</span>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { showToast, showSuccessToast } from 'vant'
import { useUserStore } from '@/store/user'
import { login } from '@/api/user'

interface LoginResponse {
  token: string
  user: {
    role: number
    [key: string]: unknown
  }
}

interface ApiError {
  response?: {
    data?: {
      message?: string
    }
  }
  message?: string
}

const ROLE_ADMIN = 2

const router = useRouter()
const userStore = useUserStore()
const { t } = useI18n()

const loading = ref(false)
const username = ref('')
const password = ref('')
const rememberPassword = ref(false)

onMounted(() => {
  const savedUsername = localStorage.getItem('rememberedUsername')
  if (savedUsername) {
    username.value = savedUsername
    rememberPassword.value = true
  }
})

const onSubmit = async () => {
  const trimmedUsername = username.value.trim()

  if (!trimmedUsername) {
    showToast(t('login.usernameRequired'))
    return
  }

  if (!password.value) {
    showToast(t('login.passwordRequired'))
    return
  }

  if (loading.value) {
    return
  }

  loading.value = true
  try {
    const data = await login({
      username: trimmedUsername,
      password: password.value
    }) as LoginResponse

    userStore.setToken(data.token)
    userStore.setUserInfo(data.user)

    if (rememberPassword.value) {
      localStorage.setItem('rememberedUsername', trimmedUsername)
    } else {
      localStorage.removeItem('rememberedUsername')
    }

    showSuccessToast(t('login.success'))

    const roleNum = data.user?.role ?? 0
    if (roleNum === ROLE_ADMIN) {
      router.replace('/admin/dashboard')
    } else {
      router.replace('/home')
    }
  } catch (error) {
    const err = error as ApiError
    showToast(err?.response?.data?.message || err?.message || t('login.failed'))
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/register')
}

const goToForgotPassword = () => {
  showToast(t('login.forgotPasswordDev'))
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  right: -100px;
  animation: float 8s ease-in-out infinite;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  left: -50px;
  animation: float 6s ease-in-out infinite reverse;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  left: 10%;
  animation: float 10s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.login-content {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
  padding: 20px;
}

.logo-section {
  text-align: center;
  margin-bottom: 40px;
  animation: slideDown 0.8s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 16px;
  color: white;
}

.logo-svg {
  width: 100%;
  height: 100%;
  animation: rotate 20s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.platform-name {
  font-size: 36px;
  font-weight: 700;
  color: white;
  margin: 0 0 8px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.platform-slogan {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  margin: 0;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-form {
  animation: slideUp 0.6s ease-out 0.4s both;
}

.form-group {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  background: white;
}

.custom-field {
  padding: 16px;
}

:deep(.van-cell) {
  padding: 16px;
}

:deep(.van-cell__title) {
  font-weight: 500;
  color: #333;
  min-width: 60px;
}

:deep(.van-field__control) {
  font-size: 15px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 16px 8px;
}

.forgot-password {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  cursor: pointer;
  transition: color 0.3s;
}

.forgot-password:hover {
  color: white;
  text-decoration: underline;
}

.submit-section {
  margin-top: 24px;
}

.submit-button {
  height: 48px;
  font-size: 17px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.submit-button:active {
  transform: translateY(0);
}

.register-section {
  text-align: center;
  margin-top: 24px;
}

.register-text {
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
}

.register-link {
  color: white;
  font-size: 14px;
  font-weight: 600;
  margin-left: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.register-link:hover {
  text-decoration: underline;
}

.footer-info {
  text-align: center;
  margin-top: 32px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
  line-height: 1.8;
}

.footer-info .link {
  color: rgba(255, 255, 255, 0.9);
  cursor: pointer;
}

.footer-info .link:hover {
  text-decoration: underline;
}

.footer-info .divider {
  margin: 0 4px;
}

@media (max-width: 480px) {
  .login-content {
    padding: 16px;
  }

  .platform-name {
    font-size: 28px;
  }
}
</style>