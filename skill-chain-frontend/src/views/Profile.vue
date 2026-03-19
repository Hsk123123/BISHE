<template>
  <div class="profile-container">
    <van-nav-bar title="我的" fixed />

    <div class="user-info" @click="goToEditProfile">
      <van-image
        round
        width="80"
        height="80"
        :src="userInfo.avatar || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'"
      />
      <div class="info">
        <h3>{{ userInfo.nickname || '未登录' }}</h3>
        <p>{{ userInfo.bio || '这个人很懒，什么都没写' }}</p>
        <div class="user-tags" v-if="isWorker">
          <span class="worker-tag">
            <van-icon name="medal" size="12" />
            商家
          </span>
        </div>
        <div class="user-tags" v-else-if="applicationStatus === 'pending'">
          <span class="pending-tag">
            <van-icon name="clock" size="12" />
            申请审核中
          </span>
        </div>
      </div>
      <van-icon name="arrow" class="edit-arrow" />
    </div>

    <div class="stats" v-if="!isWorker">
      <van-grid :column-num="4" :border="false">
        <van-grid-item @click="goToOrders">
          <template #icon>
            <div class="stat-num">{{ userInfo.orders || 0 }}</div>
          </template>
          <template #text>订单</template>
        </van-grid-item>
        <van-grid-item @click="goToFavorites">
          <template #icon>
            <div class="stat-num">{{ userInfo.favorites || 0 }}</div>
          </template>
          <template #text>收藏</template>
        </van-grid-item>
        <van-grid-item @click="goToCoupons">
          <template #icon>
            <div class="stat-num">{{ userInfo.coupons || 0 }}</div>
          </template>
          <template #text>优惠券</template>
        </van-grid-item>
        <van-grid-item @click="goToPoints">
          <template #icon>
            <div class="stat-num">{{ userInfo.points || 0 }}</div>
          </template>
          <template #text>积分</template>
        </van-grid-item>
      </van-grid>
    </div>

    <div class="stats worker-stats" v-else>
      <van-grid :column-num="4" :border="false">
        <van-grid-item @click="goToMySkills">
          <template #icon>
            <div class="stat-num">{{ skillCount }}</div>
          </template>
          <template #text>技能</template>
        </van-grid-item>
        <van-grid-item @click="goToSkillOrders">
          <template #icon>
            <div class="stat-num">{{ pendingOrders }}</div>
          </template>
          <template #text>待服务</template>
        </van-grid-item>
        <van-grid-item @click="goToEarnings">
          <template #icon>
            <div class="stat-num">¥{{ earnings }}</div>
          </template>
          <template #text>收益</template>
        </van-grid-item>
        <van-grid-item>
          <template #icon>
            <div class="stat-num">{{ rating }}</div>
          </template>
          <template #text>评分</template>
        </van-grid-item>
      </van-grid>
    </div>

    <div class="wallet-card" @click="goToWallet" v-if="!isWorker">
      <div class="wallet-left">
        <van-icon name="wallet" class="wallet-icon" color="#1989fa" />
        <span class="wallet-text">我的钱包</span>
      </div>
      <div class="wallet-right">
        <span class="balance">¥{{ userInfo.balance || 0 }}</span>
        <van-icon name="arrow" />
      </div>
    </div>

    <div class="wallet-card worker-wallet" @click="goToEarnings" v-else>
      <div class="wallet-left">
        <van-icon name="gold-coin" class="wallet-icon" color="#ff976a" />
        <span class="wallet-text">我的收益</span>
      </div>
      <div class="wallet-right">
        <span class="balance">¥{{ earnings }}</span>
        <van-icon name="arrow" />
      </div>
    </div>

    <div class="quick-services" v-if="!isWorker">
      <div class="section-title">常用服务</div>
      <van-grid :column-num="4" :border="false" square>
        <van-grid-item @click="goToOrders">
          <template #icon>
            <div class="quick-icon orders-icon">
              <van-icon name="orders" size="24" />
            </div>
          </template>
          <template #text>我的订单</template>
        </van-grid-item>
        <van-grid-item @click="goToBookings">
          <template #icon>
            <div class="quick-icon booking-icon">
              <van-icon name="calendar" size="24" />
            </div>
          </template>
          <template #text>我的预约</template>
        </van-grid-item>
        <van-grid-item @click="goToFavorites">
          <template #icon>
            <div class="quick-icon favor-icon">
              <van-icon name="star" size="24" />
            </div>
          </template>
          <template #text>我的收藏</template>
        </van-grid-item>
        <van-grid-item @click="goToHistory">
          <template #icon>
            <div class="quick-icon history-icon">
              <van-icon name="clock" size="24" />
            </div>
          </template>
          <template #text>浏览记录</template>
        </van-grid-item>
        <van-grid-item @click="goToCoupons">
          <template #icon>
            <div class="quick-icon coupon-icon">
              <van-icon name="coupon" size="24" />
            </div>
          </template>
          <template #text>优惠券</template>
        </van-grid-item>
        <van-grid-item @click="checkIn">
          <template #icon>
            <div class="checkin-icon">
              <van-icon name="point-gift" size="24" />
            </div>
          </template>
          <template #text>每日签到</template>
        </van-grid-item>
        <van-grid-item @click="goToHelp">
          <template #icon>
            <div class="quick-icon help-icon">
              <van-icon name="question" size="24" />
            </div>
          </template>
          <template #text>帮助中心</template>
        </van-grid-item>
        <van-grid-item @click="goToFeedback">
          <template #icon>
            <div class="quick-icon feedback-icon">
              <van-icon name="chat" size="24" />
            </div>
          </template>
          <template #text>意见反馈</template>
        </van-grid-item>
      </van-grid>
    </div>

    <div class="menu-list" v-if="!isWorker">
      <van-cell-group>
        <van-cell title="公告通知" is-link @click="goToNotices">
          <template #icon>
            <van-icon name="volume-o" class="menu-icon notice-icon" />
          </template>
          <template #right-icon v-if="unreadNotices > 0">
            <van-badge :content="unreadNotices" max="99" />
          </template>
        </van-cell>
        <van-cell title="收货地址" is-link @click="goToAddress">
          <template #icon>
            <van-icon name="location" class="menu-icon address-icon" />
          </template>
        </van-cell>
        <van-cell title="实名认证" is-link :value="realNameStatus" @click="goToAuth">
          <template #icon>
            <van-icon name="certificate" class="menu-icon auth-icon" />
          </template>
        </van-cell>
        <van-cell title="账号安全" is-link @click="goToPassword">
          <template #icon>
            <van-icon name="lock" class="menu-icon security-icon" />
          </template>
        </van-cell>
        <van-cell title="设置" is-link @click="goToSettings">
          <template #icon>
            <van-icon name="setting" class="menu-icon setting-icon" />
          </template>
        </van-cell>
      </van-cell-group>
    </div>

    <div class="menu-list" v-if="!isWorker">
      <van-cell-group>
        <van-cell title="成为商家" is-link @click="applyWorker" v-if="applicationStatus === 'none'">
          <template #icon>
            <van-icon name="star" class="menu-icon worker-icon" color="#ff976a" />
          </template>
        </van-cell>
        <van-cell title="申请进度查询" is-link @click="checkApplicationStatus" v-else-if="applicationStatus === 'pending'">
          <template #icon>
            <van-icon name="clock" class="menu-icon" color="#1989fa" />
          </template>
        </van-cell>
        <van-cell title="客服热线" is-link value="400-888-8888" @click="callService">
          <template #icon>
            <van-icon name="phone" class="menu-icon service-icon" />
          </template>
        </van-cell>
      </van-cell-group>
    </div>

    <div v-if="isWorker" class="worker-section">
      <div class="section-header">
        <van-icon name="user-o" />
        <span>商家中心</span>
      </div>
      <div class="worker-stats-row">
        <div class="worker-stat-item">
          <span class="stat-value">{{ skillCount }}</span>
          <span class="stat-label">技能</span>
        </div>
        <div class="worker-stat-item">
          <span class="stat-value">{{ pendingOrders }}</span>
          <span class="stat-label">待服务</span>
        </div>
        <div class="worker-stat-item">
          <span class="stat-value">{{ completedOrders }}</span>
          <span class="stat-label">已完成</span>
        </div>
        <div class="worker-stat-item">
          <span class="stat-value">{{ rating }}</span>
          <span class="stat-label">评分</span>
        </div>
      </div>
      <div class="worker-menu">
        <div class="worker-item" @click="goToPublish">
          <div class="worker-item-left">
            <van-icon name="plus" class="worker-icon publish-icon" />
            <span>发布技能</span>
          </div>
          <van-icon name="arrow" class="arrow-icon" />
        </div>
        <div class="worker-item" @click="goToMySkills">
          <div class="worker-item-left">
            <van-icon name="service" class="worker-icon skill-icon" />
            <span>我的技能</span>
          </div>
          <span class="badge" v-if="skillCount > 0">{{ skillCount }}</span>
          <van-icon name="arrow" class="arrow-icon" />
        </div>
        <div class="worker-item" @click="goToSkillOrders">
          <div class="worker-item-left">
            <van-icon name="orders" class="worker-icon order-icon" />
            <span>技能订单</span>
          </div>
          <span class="badge" v-if="pendingOrders > 0">{{ pendingOrders }}</span>
          <van-icon name="arrow" class="arrow-icon" />
        </div>
        <div class="worker-item" @click="goToEarnings">
          <div class="worker-item-left">
            <van-icon name="gold-coin" class="worker-icon earn-icon" />
            <span>我的收益</span>
          </div>
          <span class="amount">¥{{ earnings }}</span>
          <van-icon name="arrow" class="arrow-icon" />
        </div>
        <div class="worker-item" @click="goToWorkerSchedule">
          <div class="worker-item-left">
            <van-icon name="calendar" class="worker-icon schedule-icon" />
            <span>日程管理</span>
          </div>
          <van-icon name="arrow" class="arrow-icon" />
        </div>
        <div class="worker-item" @click="goToWorkerStatistics">
          <div class="worker-item-left">
            <van-icon name="chart-trending" class="worker-icon stat-icon" />
            <span>数据统计</span>
          </div>
          <van-icon name="arrow" class="arrow-icon" />
        </div>
        <div class="worker-item" @click="goToWorkerReviews">
          <div class="worker-item-left">
            <van-icon name="comment" class="worker-icon review-icon" />
            <span>评价管理</span>
          </div>
          <span class="review-badge" v-if="pendingReviews > 0">{{ pendingReviews }}条待回复</span>
          <van-icon name="arrow" class="arrow-icon" />
        </div>
        <div class="worker-item" @click="goToWorkerCertification">
          <div class="worker-item-left">
            <van-icon name="medal" class="worker-icon cert-icon" />
            <span>资质认证</span>
          </div>
          <span class="cert-status" :class="certStatus">{{ certStatusText }}</span>
          <van-icon name="arrow" class="arrow-icon" />
        </div>
      </div>
    </div>

    <div class="become-worker" v-else-if="applicationStatus === 'none'">
      <div class="worker-promo">
        <div class="promo-icon">
          <van-icon name="star-o" size="40" color="#ff976a" />
        </div>
        <div class="promo-text">
          <h4>成为商家</h4>
          <p>分享您的专业技能，开启副业收入</p>
          <div class="promo-features">
            <span><van-icon name="check" /> 时间自由</span>
            <span><van-icon name="check" /> 收入可观</span>
            <span><van-icon name="check" /> 技能变现</span>
          </div>
        </div>
        <van-button type="warning" @click="applyWorker">立即申请</van-button>
      </div>
    </div>

    <div class="application-status" v-else-if="applicationStatus === 'pending'">
      <div class="status-card pending">
        <van-icon name="clock" size="48" color="#1989fa" />
        <h4>申请审核中</h4>
        <p>您的申请正在审核中，预计1-3个工作日完成</p>
        <div class="application-info">
          <span>申请时间：{{ applicationTime }}</span>
          <span>申请类型：{{ applicationType }}</span>
        </div>
        <van-button plain type="primary" size="small" @click="checkApplicationStatus">查看详情</van-button>
      </div>
    </div>

    <div class="application-status" v-else-if="applicationStatus === 'rejected'">
      <div class="status-card rejected">
        <van-icon name="close" size="48" color="#ff4d4f" />
        <h4>申请未通过</h4>
        <p class="reject-reason">{{ rejectReason }}</p>
        <van-button type="warning" @click="reApplyWorker">重新申请</van-button>
      </div>
    </div>

    <div class="logout-btn">
      <van-button type="danger" block @click="logout">退出登录</van-button>
    </div>

    <van-tabbar v-model="activeTab" fixed>
      <van-tabbar-item icon="home-o" to="/home">首页</van-tabbar-item>
      <van-tabbar-item icon="search" to="/discover">发现</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/orders">订单</van-tabbar-item>
      <van-tabbar-item icon="user-o" to="/profile">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast, showConfirmDialog } from 'vant'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref(3)

// 根据后端返回的用户信息中的 role 字段判断是否为商家
// role: 0-用户, 1-商家, 2-管理员
const isWorker = computed(() => {
  const role = (userStore.userInfo as any)?.role ?? 0
  return role === 1
})
const applicationStatus = ref<'none' | 'pending' | 'approved' | 'rejected'>('none')
const applicationTime = ref('2024-01-15 10:30')
const applicationType = ref('个人商家')
const rejectReason = ref('资质证明材料不清晰，请重新上传身份证正面照')

const skillCount = ref(3)
const pendingOrders = ref(2)
const completedOrders = ref(15)
const earnings = ref(1280.00)
const rating = ref(4.8)
const pendingReviews = ref(3)

const unreadNotices = ref(3)

const certStatus = ref<'verified' | 'pending' | 'none'>('verified')
const certStatusText = computed(() => {
  const statusMap = {
    verified: '已认证',
    pending: '审核中',
    none: '未认证'
  }
  return statusMap[certStatus.value]
})

// 默认展示占位信息；真实数据从 userStore.userInfo 中读取
const defaultUserInfo = reactive<any>({
  nickname: '用户昵称',
  bio: '这是个人简介',
  avatar: '',
  favorites: 0,
  history: 0,
  balance: 0,
  orders: 0,
  coupons: 0,
  points: 0,
  realNameVerified: false
})

const userInfo = computed(() => {
  return (userStore.userInfo as any) || defaultUserInfo
})

const realNameStatus = computed(() => {
  return userInfo.value.realNameVerified ? '已认证' : '未认证'
})

const checkIn = () => {
  showSuccessToast('签到成功！获得10积分')
}

const goToEditProfile = () => {
  router.push('/profile-edit')
}

const goToOrders = () => {
  router.push('/orders')
}

const goToBookings = () => {
  showToast('我的预约功能开发中')
}

const goToFavorites = () => {
  showToast('收藏功能开发中')
}

const goToHistory = () => {
  showToast('浏览记录功能开发中')
}

const goToCoupons = () => {
  showToast('优惠券功能开发中')
}

const goToPoints = () => {
  showToast('积分功能开发中')
}

const goToHelp = () => {
  showToast('帮助中心功能开发中')
}

const goToFeedback = () => {
  showToast('意见反馈功能开发中')
}

const goToWallet = () => {
  router.push('/wallet')
}

const goToAddress = () => {
  router.push('/address')
}

const goToAuth = () => {
  router.push('/real-name-auth')
}

const goToPassword = () => {
  router.push('/password-change')
}

const goToSettings = () => {
  router.push('/settings')
}

const goToNotices = () => {
  router.push('/notices')
}

const goToPublish = () => {
  router.push('/publish-skill')
}

const goToMySkills = () => {
  router.push('/my-skills')
}

const goToSkillOrders = () => {
  router.push('/skill-orders')
}

const goToEarnings = () => {
  router.push('/earnings')
}

const goToWorkerSchedule = () => {
  showToast('日程管理功能开发中')
}

const goToWorkerStatistics = () => {
  showToast('数据统计功能开发中')
}

const goToWorkerReviews = () => {
  showToast('评价管理功能开发中')
}

const goToWorkerCertification = () => {
  router.push('/real-name-auth')
}

const applyWorker = () => {
  router.push('/worker-application')
}

const reApplyWorker = () => {
  router.push('/worker-application')
}

const checkApplicationStatus = () => {
  showToast('查看申请详情功能开发中')
}

const callService = () => {
  showConfirmDialog({
    title: '客服热线',
    message: '拨打 400-888-8888？',
  }).then(() => {
    showToast('正在拨打...')
  }).catch(() => {})
}

const logout = () => {
  showConfirmDialog({
    title: '确认退出',
    message: '确定要退出登录吗？',
  }).then(() => {
    userStore.clearToken()
    showToast('已退出登录')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.profile-container {
  padding-top: 46px;
  padding-bottom: 50px;
  min-height: 100vh;
  background: #f7f8fa;
}

.user-info {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px 20px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info h3 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 6px;
}

.user-info p {
  font-size: 13px;
  opacity: 0.9;
}

.user-tags {
  margin-top: 8px;
}

.worker-tag, .pending-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  background: rgba(255, 255, 255, 0.2);
}

.edit-arrow {
  color: rgba(255, 255, 255, 0.7);
  font-size: 16px;
}

.stats {
  background: white;
  margin: 12px;
  padding: 15px 10px;
  border-radius: 12px;
}

.worker-stats {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stat-num {
  font-size: 18px;
  font-weight: bold;
  color: #1989fa;
  margin-bottom: 4px;
}

.worker-stats .stat-num {
  color: white;
}

.quick-services {
  background: white;
  margin: 12px;
  border-radius: 12px;
  padding: 15px 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  padding: 0 15px 15px;
}

.quick-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}

.orders-icon { background: #e3f2fd; color: #1976d2; }
.booking-icon { background: #e8f5e9; color: #4caf50; }
.favor-icon { background: #fff3e0; color: #ff9800; }
.history-icon { background: #fce4ec; color: #e91e63; }
.coupon-icon { background: #f3e5f5; color: #9c27b0; }
.checkin-icon { background: #e0f7fa; color: #00bcd4; }
.help-icon { background: #efebe9; color: #795548; }
.feedback-icon { background: #e1f5fe; color: #03a9f4; }

.wallet-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 12px;
  padding: 18px 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.worker-wallet {
  background: linear-gradient(135deg, #ff976a 0%, #ff7a45 100%);
  color: white;
}

.wallet-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.wallet-icon {
  font-size: 24px;
}

.wallet-text {
  font-size: 15px;
  font-weight: 500;
}

.balance {
  font-size: 18px;
  font-weight: 600;
  color: #ff976a;
}

.worker-wallet .balance {
  color: white;
}

.menu-list {
  background: white;
  margin: 12px;
  border-radius: 12px;
  overflow: hidden;
}

.menu-icon {
  margin-right: 10px;
  font-size: 18px;
}

.notice-icon { color: #ff976a; }
.address-icon { color: #1989fa; }
.auth-icon { color: #07c160; }
.security-icon { color: #7232dd; }
.setting-icon { color: #646566; }
.service-icon { color: #1989fa; }
.worker-icon { color: #ff976a; }

.logout-btn {
  margin: 20px 12px 40px;
}

.worker-section {
  margin: 12px;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.worker-stats-row {
  display: flex;
  padding: 16px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.worker-stat-item {
  flex: 1;
  text-align: center;
  color: white;
}

.stat-value {
  display: block;
  font-size: 20px;
  font-weight: 600;
}

.stat-label {
  font-size: 12px;
  opacity: 0.8;
}

.worker-menu {
  padding: 8px 0;
}

.worker-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f5f5f5;
  transition: background 0.2s;
}

.worker-item:last-child {
  border-bottom: none;
}

.worker-item:active {
  background: #f9f9f9;
}

.worker-item-left {
  display: flex;
  align-items: center;
  flex: 1;
}

.worker-icon {
  font-size: 20px;
  margin-right: 12px;
}

.publish-icon { color: #07c160; }
.skill-icon { color: #1989fa; }
.order-icon { color: #ff976a; }
.earn-icon { color: #ffc107; }
.schedule-icon { color: #9c27b0; }
.stat-icon { color: #00bcd4; }
.review-icon { color: #e91e63; }
.cert-icon { color: #795548; }

.worker-item span:not(.arrow-icon):not(.badge):not(.amount):not(.review-badge):not(.cert-status) {
  font-size: 15px;
  color: #333;
}

.badge {
  background: #ff4d4f;
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  margin-right: 10px;
}

.amount {
  color: #ff976a !important;
  font-weight: 600;
  margin-right: 10px;
}

.review-badge {
  color: #1989fa !important;
  font-size: 12px;
  margin-right: 10px;
}

.cert-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  margin-right: 10px;
}

.cert-status.verified {
  color: #07c160;
  background: #e8f5e9;
}

.cert-status.pending {
  color: #ff976a;
  background: #fff7e6;
}

.cert-status.none {
  color: #999;
  background: #f5f5f5;
}

.arrow-icon {
  color: #ccc;
  font-size: 14px;
}

.become-worker {
  margin: 12px;
}

.worker-promo {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.promo-icon {
  width: 70px;
  height: 70px;
  border-radius: 16px;
  background: linear-gradient(135deg, #fff7e6 0%, #ffe4c4 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.promo-text {
  flex: 1;
}

.promo-text h4 {
  font-size: 17px;
  font-weight: 600;
  color: #333;
  margin: 0 0 6px 0;
}

.promo-text p {
  font-size: 13px;
  color: #999;
  margin: 0 0 10px 0;
}

.promo-features {
  display: flex;
  gap: 12px;
}

.promo-features span {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #07c160;
}

.application-status {
  margin: 12px;
}

.status-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.status-card h4 {
  font-size: 18px;
  font-weight: 600;
  margin: 16px 0 8px;
}

.status-card p {
  font-size: 14px;
  color: #999;
  margin-bottom: 16px;
}

.reject-reason {
  color: #ff4d4f !important;
}

.application-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
  font-size: 13px;
  color: #666;
}

.status-card.pending h4 { color: #1989fa; }
.status-card.rejected h4 { color: #ff4d4f; }
</style>
