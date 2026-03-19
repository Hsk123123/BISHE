<template>
  <van-popup
    v-model:show="showPopup"
    round
    closeable
    :close-on-click-overlay="canClose"
    :close-icon="canClose ? 'close' : ''"
  >
    <div class="popup-notice">
      <div class="popup-header">
        <van-icon name="volume-o" class="header-icon" />
        <span class="header-title">公告通知</span>
      </div>
      
      <div class="popup-content" v-if="notice">
        <div class="notice-type-tag">
          <van-tag :type="getTypeTag(notice.type)" size="small">
            {{ getTypeText(notice.type) }}
          </van-tag>
          <span v-if="notice.isTop" class="top-tag">置顶</span>
        </div>
        
        <h3 class="notice-title">{{ notice.title }}</h3>
        
        <div class="notice-meta">
          <span class="meta-item">
            <van-icon name="clock-o" size="12px" />
            {{ notice.createTime }}
          </span>
        </div>
        
        <div class="notice-body">
          {{ notice.content.length > 150 ? notice.content.substring(0, 150) + '...' : notice.content }}
        </div>
        
        <van-divider v-if="notice.content.length > 150" />
        
        <div v-if="notice.content.length > 150" class="notice-actions">
          <van-button type="primary" size="small" block @click="viewFullNotice">
            查看完整公告
          </van-button>
        </div>
      </div>
      
      <div v-else class="popup-empty">
        <van-icon name="check-circle-o" size="48px" color="#67c23a" />
        <p>暂无新公告</p>
      </div>
      
      <div class="popup-footer" v-if="canClose">
        <van-checkbox v-model="dontShowToday" shape="round">
          今日不再提醒
        </van-checkbox>
        <van-button type="primary" size="small" @click="closePopup">
          我知道了
        </van-button>
      </div>
    </div>
  </van-popup>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'

interface Notice {
  id: number
  title: string
  type: string
  content: string
  viewCount: number
  createTime: string
  endTime?: string
  isTop: boolean
}

const router = useRouter()
const showPopup = ref(false)
const dontShowToday = ref(false)
const currentNoticeId = ref<number | null>(null)

const notices = ref<Notice[]>([
  {
    id: 1,
    title: '技能链平台正式上线公告',
    type: 'system',
    content: '亲爱的用户们，技能链平台正式上线啦！我们致力于为您提供最优质的个人服务对接平台。在这里，您可以轻松找到各种专业技能人才，也可以展示自己的特长获得额外收入。平台涵盖家政、维修、陪练、教学等多种服务类别，让您的生活更加便捷。',
    viewCount: 12580,
    createTime: '2024-01-01 10:00:00',
    isTop: true
  },
  {
    id: 2,
    title: '新年特惠活动开启',
    type: 'activity',
    content: '即日起至1月31日，所有新用户首次下单立减20元，老用户推荐新用户下单可获得10元优惠券。',
    viewCount: 8560,
    createTime: '2024-01-10 09:00:00',
    endTime: '2024-01-31 23:59:59',
    isTop: true
  },
  {
    id: 4,
    title: '技能链 v2.0 新版本发布',
    type: 'update',
    content: '本次更新内容：\n1. 新增智能推荐功能\n2. 优化消息推送机制\n3. 修复已知bug\n4. 提升页面加载速度',
    viewCount: 5420,
    createTime: '2024-01-14 16:00:00',
    isTop: false
  }
])

const notice = computed(() => {
  if (notices.value.length === 0) return null
  return notices.value[0]
})

const canClose = computed(() => {
  return true
})

const getTypeTag = (type: string): string => {
  const tags: Record<string, string> = {
    system: 'primary',
    activity: 'success',
    rule: 'warning',
    update: 'info'
  }
  return tags[type] || 'default'
}

const getTypeText = (type: string): string => {
  const texts: Record<string, string> = {
    system: '系统公告',
    activity: '活动公告',
    rule: '规则公告',
    update: '更新日志'
  }
  return texts[type] || type
}

const closePopup = () => {
  if (dontShowToday.value && notice.value) {
    localStorage.setItem(`notice_${notice.value.id}_hidden`, new Date().toDateString())
  }
  showPopup.value = false
}

const viewFullNotice = () => {
  if (notice.value) {
    currentNoticeId.value = notice.value.id
  }
  closePopup()
  router.push('/notices')
}

const checkShouldShow = () => {
  if (!notice.value) return
  
  const hiddenDate = localStorage.getItem(`notice_${notice.value.id}_hidden`)
  const today = new Date().toDateString()
  
  if (hiddenDate !== today) {
    showPopup.value = true
  }
}

const show = () => {
  checkShouldShow()
}

const checkAndShow = () => {
  checkShouldShow()
}

defineExpose({
  show,
  checkAndShow
})

onMounted(() => {
  setTimeout(() => {
    checkShouldShow()
  }, 1500)
})
</script>

<style scoped>
.popup-notice {
  width: 320px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.popup-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px 12px 0 0;
}

.header-icon {
  font-size: 20px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
}

.popup-content {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
}

.notice-type-tag {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.top-tag {
  background: #ff4d4f;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
}

.notice-title {
  font-size: 17px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px 0;
  line-height: 1.4;
}

.notice-meta {
  display: flex;
  gap: 15px;
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

.notice-body {
  font-size: 14px;
  color: #666;
  line-height: 1.7;
  white-space: pre-wrap;
}

.notice-actions {
  margin-top: 10px;
}

.popup-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  gap: 10px;
  color: #999;
}

.popup-empty p {
  margin: 0;
  font-size: 14px;
}

.popup-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 15px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
  border-radius: 0 0 12px 12px;
}

.popup-footer .van-button {
  margin-left: 10px;
}
</style>
