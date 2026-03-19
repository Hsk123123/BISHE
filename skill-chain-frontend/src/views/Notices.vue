<template>
  <div class="notices-container">
    <van-nav-bar title="公告列表" left-arrow fixed @click-left="goBack">
      <template #right>
        <van-icon name="refresh" size="20px" @click="loadNotices" />
      </template>
    </van-nav-bar>
    
    <div class="notice-content">
      <div v-if="loading" class="loading-state">
        <van-loading type="spinner" color="#667eea" />
        <span>加载中...</span>
      </div>
      
      <div v-else-if="notices.length === 0" class="empty-state">
        <van-icon name="volume-o" size="64px" color="#ccc" />
        <p>暂无公告</p>
      </div>
      
      <div v-else class="notice-list">
        <div 
          v-for="notice in notices" 
          :key="notice.id" 
          class="notice-card"
          @click="showNoticeDetail(notice)"
        >
          <div class="notice-header">
            <div class="notice-type">
              <van-tag :type="getTypeTag(notice.type)" size="small">
                {{ getTypeText(notice.type) }}
              </van-tag>
              <span v-if="notice.isTop" class="top-tag">置顶</span>
            </div>
            <span class="notice-time">{{ formatTime(notice.createTime) }}</span>
          </div>
          
          <h3 class="notice-title">{{ notice.title }}</h3>
          
          <div class="notice-preview">
            {{ notice.content.substring(0, 80) }}{{ notice.content.length > 80 ? '...' : '' }}
          </div>
          
          <div class="notice-footer">
            <span class="view-count">
              <van-icon name="eye-o" />
              {{ notice.viewCount }}
            </span>
            <span class="read-more">
              阅读全文
              <van-icon name="arrow" />
            </span>
          </div>
        </div>
      </div>
    </div>
    
    <van-popup 
      v-model:show="showDetailPopup" 
      position="bottom" 
      :style="{ height: '90%' }"
      round
      closeable
    >
      <div class="notice-detail-popup" v-if="selectedNotice">
        <div class="detail-header">
          <div class="detail-type">
            <van-tag :type="getTypeTag(selectedNotice.type)" size="medium">
              {{ getTypeText(selectedNotice.type) }}
            </van-tag>
            <span v-if="selectedNotice.isTop" class="top-tag">置顶</span>
          </div>
        </div>
        
        <h2 class="detail-title">{{ selectedNotice.title }}</h2>
        
        <div class="detail-meta">
          <span class="meta-item">
            <van-icon name="clock-o" />
            {{ selectedNotice.createTime }}
          </span>
          <span class="meta-item">
            <van-icon name="eye-o" />
            {{ selectedNotice.viewCount }} 次浏览
          </span>
          <span v-if="selectedNotice.endTime" class="meta-item">
            <van-icon name="calendar" />
            有效期至 {{ selectedNotice.endTime }}
          </span>
        </div>
        
        <van-divider />
        
        <div class="detail-content">
          {{ selectedNotice.content }}
        </div>
        
        <div class="detail-footer">
          <van-button type="primary" block @click="showDetailPopup = false">
            我知道了
          </van-button>
        </div>
      </div>
    </van-popup>
    
    <van-tabbar v-model="activeTab" fixed>
      <van-tabbar-item icon="home-o" to="/home">首页</van-tabbar-item>
      <van-tabbar-item icon="search" to="/browse">发现</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/orders">订单</van-tabbar-item>
      <van-tabbar-item icon="user-o" to="/profile">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

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
const activeTab = ref(3)
const loading = ref(false)
const showDetailPopup = ref(false)
const selectedNotice = ref<Notice | null>(null)

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
    content: '即日起至1月31日，所有新用户首次下单立减20元，老用户推荐新用户下单可获得10元优惠券。\n\n活动规则：\n1. 新用户注册即享首单立减\n2. 推荐好友下单，双方各得10元优惠券\n3. 优惠券有效期为30天\n4. 本活动最终解释权归技能链平台所有',
    viewCount: 8560,
    createTime: '2024-01-10 09:00:00',
    endTime: '2024-01-31 23:59:59',
    isTop: true
  },
  {
    id: 3,
    title: '平台服务协议更新通知',
    type: 'rule',
    content: '为更好地保障用户权益，我们对平台服务协议进行了更新，主要变更内容包括：\n\n1. 订单取消政策调整\n   - 服务开始前24小时取消，全额退款\n   - 服务开始前12-24小时取消，扣除20%违约金\n   - 服务开始前12小时内取消，扣除50%违约金\n\n2. 服务者接单规则优化\n   - 新增接单响应时间要求\n   - 连续拒单将影响推荐权重\n\n3. 评价体系升级\n   - 新增服务者信誉评分\n   - 恶意差评举报机制上线',
    viewCount: 3200,
    createTime: '2024-01-12 14:30:00',
    isTop: false
  },
  {
    id: 4,
    title: '技能链 v2.0 新版本发布',
    type: 'update',
    content: '本次更新内容：\n\n1. 新增智能推荐功能\n   - 根据您的浏览历史推荐相关技能\n   - 智能匹配最适合的服务者\n\n2. 优化消息推送机制\n   - 实时推送订单状态更新\n   - 新增消息免打扰设置\n\n3. 修复已知bug\n   - 修复部分机型显示异常问题\n   - 优化页面加载速度\n\n4. 性能提升\n   - 页面加载速度提升50%\n   - 降低应用内存占用',
    viewCount: 5420,
    createTime: '2024-01-14 16:00:00',
    isTop: false
  },
  {
    id: 5,
    title: '关于规范服务描述的公告',
    type: 'rule',
    content: '为提升平台服务质量，请各位服务者在发布技能时确保服务描述准确、清晰。具体要求如下：\n\n1. 服务内容描述需详细列出包含和不包含的项目\n2. 价格说明需明确是否有额外费用\n3. 服务时长和适用范围需清晰标注\n4. 上传图片需真实反映服务场景\n\n平台将对不符合规范的技能进行下架处理，感谢您的配合。',
    viewCount: 1890,
    createTime: '2024-01-15 11:00:00',
    isTop: false
  },
  {
    id: 6,
    title: '元宵节特别活动预告',
    type: 'activity',
    content: '元宵节特别活动即将开启！\n\n活动时间：2月10日-2月12日\n活动内容：\n- 猜灯谜赢积分\n- 限时折扣专区\n- 积分翻倍任务\n\n敬请期待！',
    viewCount: 2100,
    createTime: '2024-01-20 10:00:00',
    endTime: '2024-02-12 23:59:59',
    isTop: false
  }
])

const goBack = () => {
  router.back()
}

const loadNotices = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    showSuccessToast('刷新成功')
  }, 1000)
}

const showNoticeDetail = (notice: Notice) => {
  selectedNotice.value = notice
  notice.viewCount++
  showDetailPopup.value = true
}

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

const formatTime = (time: string): string => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) {
    return '今天'
  } else if (days === 1) {
    return '昨天'
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return time.split(' ')[0]
  }
}

const showSuccessToast = (message: string) => {
  (window as any).showSuccessToast?.(message) || console.log(message)
}
</script>

<style scoped>
.notices-container {
  padding-top: 46px;
  padding-bottom: 50px;
  min-height: 100vh;
  background: #f5f5f5;
}

.notice-content {
  padding: 10px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  gap: 15px;
  color: #667eea;
  font-size: 14px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  gap: 15px;
  color: #999;
}

.empty-state p {
  font-size: 14px;
  margin: 0;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notice-card {
  background: white;
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s, box-shadow 0.2s;
}

.notice-card:active {
  transform: scale(0.98);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.notice-type {
  display: flex;
  align-items: center;
  gap: 8px;
}

.top-tag {
  background: #ff4d4f;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
}

.notice-time {
  font-size: 12px;
  color: #999;
}

.notice-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.notice-preview {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
}

.notice-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.view-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

.read-more {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #667eea;
}

.notice-detail-popup {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.detail-header {
  margin-bottom: 15px;
}

.detail-type {
  display: flex;
  align-items: center;
  gap: 10px;
}

.detail-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0 0 15px 0;
  line-height: 1.4;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  font-size: 13px;
  color: #999;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-content {
  flex: 1;
  font-size: 15px;
  color: #333;
  line-height: 1.8;
  white-space: pre-wrap;
  overflow-y: auto;
}

.detail-footer {
  margin-top: 20px;
}
</style>
