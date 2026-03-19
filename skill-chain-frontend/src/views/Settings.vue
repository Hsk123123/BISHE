<template>
  <div class="settings-container">
    <van-nav-bar title="设置" left-arrow fixed @click-left="goBack" />

    <div class="settings-list">
      <van-cell-group>
        <van-cell title="账号安全" is-link @click="goToPassword">
          <template #icon>
            <van-icon name="lock" class="cell-icon" />
          </template>
        </van-cell>
        <van-cell title="消息通知" is-link>
          <template #icon>
            <van-icon name="bell" class="cell-icon" />
          </template>
        </van-cell>
        <van-cell title="隐私设置" is-link>
          <template #icon>
            <van-icon name="eye" class="cell-icon" />
          </template>
        </van-cell>
        <van-cell title="语言" is-link value="简体中文">
          <template #icon>
            <van-icon name="guide-o" class="cell-icon" />
          </template>
        </van-cell>
      </van-cell-group>

      <van-cell-group>
        <van-cell title="关于我们" is-link>
          <template #icon>
            <van-icon name="info-o" class="cell-icon" />
          </template>
        </van-cell>
        <van-cell title="帮助中心" is-link>
          <template #icon>
            <van-icon name="question-o" class="cell-icon" />
          </template>
        </van-cell>
        <van-cell title="意见反馈" is-link>
          <template #icon>
            <van-icon name="chat-o" class="cell-icon" />
          </template>
        </van-cell>
      </van-cell-group>

      <van-cell-group>
        <van-cell title="清除缓存" is-link @click="clearCache">
          <template #icon>
            <van-icon name="delete" class="cell-icon" />
          </template>
        </van-cell>
        <van-cell title="当前版本" value="v1.0.0">
          <template #icon>
            <van-icon name="label-o" class="cell-icon" />
          </template>
        </van-cell>
      </van-cell-group>

      <div class="logout-btn">
        <van-button type="danger" block @click="logout">退出登录</van-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const goBack = () => {
  router.back()
}

const goToPassword = () => {
  router.push('/password-change')
}

const clearCache = () => {
  showSuccessToast('清除成功')
}

const logout = () => {
  userStore.clearToken()
  showToast('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.settings-container {
  padding-top: 46px;
  min-height: 100vh;
  background: #f5f5f5;
}

.settings-list {
  padding: 10px;
}

.cell-icon {
  margin-right: 10px;
  color: #667eea;
}

.logout-btn {
  margin: 30px 10px;
}
</style>
