<template>
  <div class="profile-container">
    <van-nav-bar title="我的" fixed />

    <section class="profile-hero" @click="goToEditProfile">
      <div class="hero-glow hero-glow-1"></div>
      <div class="hero-glow hero-glow-2"></div>

      <div class="user-info-card">
        <van-image
          round
          width="80"
          height="80"
          class="avatar"
          :src="userInfo.avatar || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'"
        />
        <div class="info">
          <div class="name-row">
            <h3>{{ userInfo.nickname || '未登录' }}</h3>
            <van-icon name="arrow" class="edit-arrow" />
          </div>
          <p>{{ userInfo.bio || '这个人很懒，什么都没写' }}</p>

          <div class="user-tags" v-if="isWorker">
            <span class="user-tag worker-tag">
              <van-icon name="medal" size="12" />
              商家
            </span>
          </div>

          <div class="user-tags" v-else-if="applicationStatus === 'pending'">
            <span class="user-tag pending-tag">
              <van-icon name="clock" size="12" />
              申请审核中
            </span>
          </div>
        </div>
      </div>
    </section>

    <section class="content-section">
      <div class="section-card stats-card" v-if="!isWorker">
        <div class="card-title-row">
          <div>
            <h3>我的概览</h3>
            <p>常用数据一目了然</p>
          </div>
        </div>

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

      <div class="section-card stats-card worker-overview-card" v-else>
        <div class="card-title-row light">
          <div>
            <h3>商家概览</h3>
            <p>当前经营状态与核心数据</p>
          </div>
        </div>

        <van-grid :column-num="4" :border="false">
          <van-grid-item @click="goToMySkills">
            <template #icon>
              <div class="stat-num light">{{ skillCount }}</div>
            </template>
            <template #text>技能</template>
          </van-grid-item>
          <van-grid-item @click="goToSkillOrders">
            <template #icon>
              <div class="stat-num light">{{ pendingOrders }}</div>
            </template>
            <template #text>待服务</template>
          </van-grid-item>
          <van-grid-item @click="goToEarnings">
            <template #icon>
              <div class="stat-num light">¥{{ earnings }}</div>
            </template>
            <template #text>收益</template>
          </van-grid-item>
          <van-grid-item>
            <template #icon>
              <div class="stat-num light">{{ rating }}</div>
            </template>
            <template #text>评分</template>
          </van-grid-item>
        </van-grid>
      </div>

      <div class="section-card wallet-card" @click="goToWallet" v-if="!isWorker">
        <div class="wallet-left">
          <div class="wallet-icon-wrap icon-blue">
            <van-icon name="wallet" class="wallet-icon" />
          </div>
          <div class="wallet-copy">
            <span class="wallet-text">我的钱包</span>
            <small>余额、积分与资金相关服务</small>
          </div>
        </div>
        <div class="wallet-right">
          <span class="balance">¥{{ userInfo.balance || 0 }}</span>
          <van-icon name="arrow" />
        </div>
      </div>

      <div class="section-card wallet-card worker-wallet" @click="goToEarnings" v-else>
        <div class="wallet-left">
          <div class="wallet-icon-wrap icon-orange soft">
            <van-icon name="gold-coin" class="wallet-icon" />
          </div>
          <div class="wallet-copy">
            <span class="wallet-text">我的收益</span>
            <small>查看经营数据与提现记录</small>
          </div>
        </div>
        <div class="wallet-right">
          <span class="balance light">¥{{ earnings }}</span>
          <van-icon name="arrow" />
        </div>
      </div>

      <div class="section-card quick-services" v-if="!isWorker">
        <div class="card-title-row">
          <div>
            <h3>常用服务</h3>
            <p>你常用的个人功能入口</p>
          </div>
        </div>

        <van-grid :column-num="4" :border="false" square>
          <van-grid-item @click="goToOrders">
            <template #icon>
              <div class="quick-icon orders-icon">
                <van-icon name="orders-o" size="22" />
              </div>
            </template>
            <template #text>我的订单</template>
          </van-grid-item>

          <van-grid-item @click="goToBookings">
            <template #icon>
              <div class="quick-icon booking-icon">
                <van-icon name="calendar-o" size="22" />
              </div>
            </template>
            <template #text>我的预约</template>
          </van-grid-item>

          <van-grid-item @click="goToFavorites">
            <template #icon>
              <div class="quick-icon favor-icon">
                <van-icon name="star-o" size="22" />
              </div>
            </template>
            <template #text>我的收藏</template>
          </van-grid-item>

          <van-grid-item @click="goToHistory">
            <template #icon>
              <div class="quick-icon history-icon">
                <van-icon name="underway-o" size="22" />
              </div>
            </template>
            <template #text>浏览记录</template>
          </van-grid-item>

          <van-grid-item @click="goToCoupons">
            <template #icon>
              <div class="quick-icon coupon-icon">
                <van-icon name="coupon-o" size="22" />
              </div>
            </template>
            <template #text>优惠券</template>
          </van-grid-item>

          <van-grid-item @click="checkIn">
            <template #icon>
              <div class="quick-icon checkin-icon">
                <van-icon name="gift-o" size="22" />
              </div>
            </template>
            <template #text>每日签到</template>
          </van-grid-item>

          <van-grid-item @click="goToHelp">
            <template #icon>
              <div class="quick-icon help-icon">
                <van-icon name="question-o" size="22" />
              </div>
            </template>
            <template #text>帮助中心</template>
          </van-grid-item>

          <van-grid-item @click="goToFeedback">
            <template #icon>
              <div class="quick-icon feedback-icon">
                <van-icon name="chat-o" size="22" />
              </div>
            </template>
            <template #text>意见反馈</template>
          </van-grid-item>
        </van-grid>
      </div>

      <div class="section-card menu-card" v-if="!isWorker">
        <div class="card-title-row">
          <div>
            <h3>账户服务</h3>
            <p>账号、认证与设置管理</p>
          </div>
        </div>

        <div class="menu-list-inner">
          <div class="menu-item" @click="goToNotices">
            <div class="menu-left">
              <div class="menu-icon-wrap notice-icon">
                <van-icon name="volume-o" class="menu-icon" />
              </div>
              <span>公告通知</span>
            </div>
            <div class="menu-right">
              <van-badge v-if="unreadNotices > 0" :content="unreadNotices" max="99" />
              <van-icon name="arrow" class="arrow-icon" />
            </div>
          </div>

          <div class="menu-item" @click="goToAddress">
            <div class="menu-left">
              <div class="menu-icon-wrap address-icon">
                <van-icon name="location-o" class="menu-icon" />
              </div>
              <span>收货地址</span>
            </div>
            <van-icon name="arrow" class="arrow-icon" />
          </div>

          <div class="menu-item" @click="goToAuth">
            <div class="menu-left">
              <div class="menu-icon-wrap auth-icon">
                <van-icon name="certificate" class="menu-icon" />
              </div>
              <span>实名认证</span>
            </div>
            <div class="menu-right">
              <span class="menu-value">{{ realNameStatus }}</span>
              <van-icon name="arrow" class="arrow-icon" />
            </div>
          </div>

          <div class="menu-item" @click="goToPassword">
            <div class="menu-left">
              <div class="menu-icon-wrap security-icon">
                <van-icon name="shield-o" class="menu-icon" />
              </div>
              <span>账号安全</span>
            </div>
            <van-icon name="arrow" class="arrow-icon" />
          </div>

          <div class="menu-item" @click="goToSettings">
            <div class="menu-left">
              <div class="menu-icon-wrap setting-icon">
                <van-icon name="setting-o" class="menu-icon" />
              </div>
              <span>设置</span>
            </div>
            <van-icon name="arrow" class="arrow-icon" />
          </div>
        </div>
      </div>

      <div class="section-card menu-card" v-if="!isWorker">
        <div class="card-title-row">
          <div>
            <h3>其他服务</h3>
            <p>申请商家与平台联系入口</p>
          </div>
        </div>

        <div class="menu-list-inner">
          <div
            class="menu-item"
            @click="applyWorker"
            v-if="isNormalUser && applicationStatus === 'none'"
          >
            <div class="menu-left">
              <div class="menu-icon-wrap worker-icon">
                <van-icon name="star-o" class="menu-icon" />
              </div>
              <span>成为商家</span>
            </div>
            <van-icon name="arrow" class="arrow-icon" />
          </div>

          <div
            class="menu-item"
            @click="checkApplicationStatus"
            v-else-if="isNormalUser && applicationStatus === 'pending'"
          >
            <div class="menu-left">
              <div class="menu-icon-wrap pending-mini-icon">
                <van-icon name="clock-o" class="menu-icon" />
              </div>
              <span>申请进度查询</span>
            </div>
            <van-icon name="arrow" class="arrow-icon" />
          </div>

          <div class="menu-item" @click="callService">
            <div class="menu-left">
              <div class="menu-icon-wrap service-icon">
                <van-icon name="phone-o" class="menu-icon" />
              </div>
              <span>客服热线</span>
            </div>
            <div class="menu-right">
              <span class="menu-value">400-888-8888</span>
              <van-icon name="arrow" class="arrow-icon" />
            </div>
          </div>
        </div>
      </div>

      <div v-if="isWorker" class="section-card worker-section">
        <div class="card-title-row">
          <div>
            <h3>商家中心</h3>
            <p>技能、订单、收益与经营管理</p>
          </div>
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
              <div class="worker-icon-wrap publish-icon">
                <van-icon name="plus" class="worker-icon" />
              </div>
              <span>发布技能</span>
            </div>
            <van-icon name="arrow" class="arrow-icon" />
          </div>

          <div class="worker-item" @click="goToMySkills">
            <div class="worker-item-left">
              <div class="worker-icon-wrap skill-icon">
                <van-icon name="service-o" class="worker-icon" />
              </div>
              <span>我的技能</span>
            </div>
            <span class="badge" v-if="skillCount > 0">{{ skillCount }}</span>
            <van-icon name="arrow" class="arrow-icon" />
          </div>

          <div class="worker-item" @click="goToSkillOrders">
            <div class="worker-item-left">
              <div class="worker-icon-wrap order-icon">
                <van-icon name="orders-o" class="worker-icon" />
              </div>
              <span>技能订单</span>
            </div>
            <span class="badge" v-if="pendingOrders > 0">{{ pendingOrders }}</span>
            <van-icon name="arrow" class="arrow-icon" />
          </div>

          <div class="worker-item" @click="goToEarnings">
            <div class="worker-item-left">
              <div class="worker-icon-wrap earn-icon">
                <van-icon name="gold-coin-o" class="worker-icon" />
              </div>
              <span>我的收益</span>
            </div>
            <span class="amount">¥{{ earnings }}</span>
            <van-icon name="arrow" class="arrow-icon" />
          </div>

          <div class="worker-item" @click="goToWorkerSchedule">
            <div class="worker-item-left">
              <div class="worker-icon-wrap schedule-icon">
                <van-icon name="calendar-o" class="worker-icon" />
              </div>
              <span>日程管理</span>
            </div>
            <van-icon name="arrow" class="arrow-icon" />
          </div>

          <div class="worker-item" @click="goToWorkerStatistics">
            <div class="worker-item-left">
              <div class="worker-icon-wrap stat-icon">
                <van-icon name="bar-chart-o" class="worker-icon" />
              </div>
              <span>数据统计</span>
            </div>
            <van-icon name="arrow" class="arrow-icon" />
          </div>

          <div class="worker-item" @click="goToWorkerReviews">
            <div class="worker-item-left">
              <div class="worker-icon-wrap review-icon">
                <van-icon name="comment-o" class="worker-icon" />
              </div>
              <span>评价管理</span>
            </div>
            <span class="review-badge" v-if="pendingReviews > 0">{{ pendingReviews }}条待回复</span>
            <van-icon name="arrow" class="arrow-icon" />
          </div>

          <div class="worker-item" @click="goToWorkerCertification">
            <div class="worker-item-left">
              <div class="worker-icon-wrap cert-icon">
                <van-icon name="medal-o" class="worker-icon" />
              </div>
              <span>资质认证</span>
            </div>
            <span class="cert-status" :class="certStatus">{{ certStatusText }}</span>
            <van-icon name="arrow" class="arrow-icon" />
          </div>
        </div>
      </div>

      <div class="section-card become-worker" v-else-if="isNormalUser && applicationStatus === 'none'">
        <div class="worker-promo">
          <div class="promo-icon">
            <van-icon name="star-o" size="36" color="#ff976a" />
          </div>
          <div class="promo-text">
            <h4>成为商家</h4>
            <p>分享您的专业技能，开启副业收入</p>
            <div class="promo-features">
              <span><van-icon name="success" /> 时间自由</span>
              <span><van-icon name="success" /> 收入可观</span>
              <span><van-icon name="success" /> 技能变现</span>
            </div>
          </div>
          <van-button type="warning" round @click="applyWorker">立即申请</van-button>
        </div>
      </div>

      <div class="section-card application-status" v-else-if="isNormalUser && applicationStatus === 'pending'">
        <div class="status-card pending">
          <van-icon name="clock-o" size="46" color="#1989fa" />
          <h4>申请审核中</h4>
          <p>您的申请正在审核中，预计 1-3 个工作日完成</p>
          <div class="application-info">
            <span>申请时间：{{ applicationTime }}</span>
            <span>申请类型：{{ applicationType }}</span>
          </div>
          <van-button plain type="primary" round size="small" @click="checkApplicationStatus">
            查看详情
          </van-button>
        </div>
      </div>

      <div class="section-card application-status" v-else-if="isNormalUser && applicationStatus === 'rejected'">
        <div class="status-card rejected">
          <van-icon name="close" size="46" color="#ff4d4f" />
          <h4>申请未通过</h4>
          <p class="reject-reason">{{ rejectReason }}</p>
          <van-button type="warning" round @click="reApplyWorker">重新申请</van-button>
        </div>
      </div>

      <div class="logout-btn">
        <van-button type="danger" block round @click="logout">退出登录</van-button>
      </div>
    </section>

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
const isNormalUser = computed(() => {
  const role = (userStore.userInfo as any)?.role ?? 0
  return role === 0
})
const applicationStatus = ref<'none' | 'pending' | 'approved' | 'rejected'>('none')
const applicationTime = ref('2024-01-15 10:30')
const applicationType = ref('个人商家')
const rejectReason = ref('资质证明材料不清晰，请重新上传身份证正面照')

const skillCount = ref(3)
const pendingOrders = ref(2)
const completedOrders = ref(15)
const earnings = ref(1280.0)
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
    message: '拨打 400-888-8888？'
  }).then(() => {
    showToast('正在拨打...')
  }).catch(() => {})
}

const logout = () => {
  showConfirmDialog({
    title: '确认退出',
    message: '确定要退出登录吗？'
  }).then(() => {
    userStore.clearToken()
    showToast('已退出登录')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  padding-top: 46px;
  padding-bottom: 62px;
  background:
    radial-gradient(circle at top right, rgba(102, 126, 234, 0.12), transparent 28%),
    linear-gradient(180deg, #f6f8fc 0%, #eef2f7 100%);
}

.profile-hero {
  position: relative;
  overflow: hidden;
  margin-bottom: 14px;
  padding: 22px 14px 0;
  background: linear-gradient(135deg, #667eea 0%, #7b61ff 55%, #8f6bff 100%);
  border-bottom-left-radius: 24px;
  border-bottom-right-radius: 24px;
}

.hero-glow {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
  filter: blur(4px);
}

.hero-glow-1 {
  top: -10px;
  right: -8px;
  width: 120px;
  height: 120px;
}

.hero-glow-2 {
  right: 80px;
  bottom: 40px;
  width: 88px;
  height: 88px;
}

.user-info-card {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 16px;
  margin-bottom: -24px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 12px 28px rgba(31, 45, 61, 0.1);
  backdrop-filter: blur(8px);
}

.avatar {
  flex-shrink: 0;
  border: 3px solid rgba(255, 255, 255, 0.8);
}

.info {
  flex: 1;
  min-width: 0;
}

.name-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.info h3 {
  margin: 0 0 6px;
  font-size: 20px;
  font-weight: 700;
  color: #1f2330;
  line-height: 1.3;
}

.info p {
  margin: 0;
  font-size: 13px;
  line-height: 1.6;
  color: #7a8193;
}

.user-tags {
  margin-top: 10px;
}

.user-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 500;
}

.worker-tag {
  color: #ff976a;
  background: rgba(255, 151, 106, 0.14);
}

.pending-tag {
  color: #1989fa;
  background: rgba(25, 137, 250, 0.1);
}

.edit-arrow {
  color: #98a0b3;
  font-size: 16px;
}

.content-section {
  padding: 36px 14px 8px;
}

.section-card {
  margin-bottom: 14px;
  padding: 16px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 10px 24px rgba(31, 45, 61, 0.05);
}

.card-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.card-title-row h3 {
  margin: 0 0 4px;
  font-size: 18px;
  font-weight: 700;
  color: #1f2330;
}

.card-title-row p {
  margin: 0;
  font-size: 12px;
  line-height: 1.5;
  color: #8a90a2;
}

.card-title-row.light h3,
.card-title-row.light p {
  color: #fff;
}

.stats-card :deep(.van-grid-item__content) {
  background: transparent;
  padding: 10px 4px;
}

.stats-card :deep(.van-grid-item__text) {
  margin-top: 6px;
  font-size: 12px;
  color: #7b8295;
}

.worker-overview-card {
  background: linear-gradient(135deg, #667eea 0%, #7b61ff 55%, #8f6bff 100%);
}

.worker-overview-card :deep(.van-grid-item__text) {
  color: rgba(255, 255, 255, 0.88);
}

.stat-num {
  font-size: 20px;
  font-weight: 700;
  color: #667eea;
  line-height: 1.2;
}

.stat-num.light {
  color: #fff;
}

.wallet-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.wallet-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.wallet-icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 14px;
}

.icon-blue {
  background: rgba(102, 126, 234, 0.12);
  color: #667eea;
}

.icon-orange {
  background: rgba(255, 151, 106, 0.14);
  color: #ff976a;
}

.icon-orange.soft {
  background: rgba(255, 151, 106, 0.16);
}

.wallet-icon {
  font-size: 22px;
}

.wallet-copy {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.wallet-text {
  font-size: 15px;
  font-weight: 600;
  color: #1f2330;
}

.wallet-copy small {
  font-size: 12px;
  color: #8a90a2;
}

.wallet-right {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #a0a7b7;
}

.balance {
  font-size: 20px;
  font-weight: 700;
  color: #ff976a;
}

.balance.light {
  color: #ff976a;
}

.quick-services :deep(.van-grid-item__content) {
  padding: 12px 4px;
  background: transparent;
}

.quick-services :deep(.van-grid-item__text) {
  margin-top: 6px;
  font-size: 12px;
  color: #4c5365;
}

.quick-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  border-radius: 15px;
}

.orders-icon { background: #e8f0ff; color: #1976d2; }
.booking-icon { background: #e8f7ef; color: #35b36b; }
.favor-icon { background: #fff4e8; color: #ff9800; }
.history-icon { background: #fdebf2; color: #e85b93; }
.coupon-icon { background: #f5ecff; color: #8f55dd; }
.checkin-icon { background: #e6fbfb; color: #00b8c9; }
.help-icon { background: #f3efe9; color: #8a6b57; }
.feedback-icon { background: #eaf7ff; color: #1ea7ff; }

.menu-card {
  padding-bottom: 6px;
}

.menu-list-inner {
  display: flex;
  flex-direction: column;
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid #f2f4f8;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #1f2330;
  font-size: 15px;
  font-weight: 500;
}

.menu-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.menu-icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 12px;
}

.menu-icon {
  font-size: 18px;
}

.notice-icon { background: #fff1e9; color: #ff976a; }
.address-icon { background: #eaf4ff; color: #1989fa; }
.auth-icon { background: #e9f9ef; color: #07c160; }
.security-icon { background: #f2eaff; color: #7232dd; }
.setting-icon { background: #eff2f6; color: #646566; }
.service-icon { background: #eaf4ff; color: #1989fa; }
.worker-icon { background: #fff4e8; color: #ff976a; }
.pending-mini-icon { background: #eef6ff; color: #1989fa; }

.menu-value {
  font-size: 12px;
  color: #8a90a2;
}

.arrow-icon {
  color: #b6bdcb;
  font-size: 14px;
}

.worker-section {
  padding-bottom: 6px;
}

.worker-stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  margin-bottom: 12px;
}

.worker-stat-item {
  padding: 12px 8px;
  text-align: center;
  border-radius: 16px;
  background: linear-gradient(180deg, #fafbff 0%, #f4f6fb 100%);
  border: 1px solid rgba(102, 126, 234, 0.08);
}

.stat-value {
  display: block;
  margin-bottom: 4px;
  font-size: 20px;
  font-weight: 700;
  color: #667eea;
}

.stat-label {
  font-size: 12px;
  color: #7b8295;
}

.worker-menu {
  display: flex;
  flex-direction: column;
}

.worker-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 0;
  border-bottom: 1px solid #f2f4f8;
}

.worker-item:last-child {
  border-bottom: none;
}

.worker-item-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.worker-icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 12px;
}

.worker-icon {
  font-size: 18px;
}

.publish-icon { background: #eaf9ef; color: #07c160; }
.skill-icon { background: #eaf4ff; color: #1989fa; }
.order-icon { background: #fff2ea; color: #ff976a; }
.earn-icon { background: #fff8e6; color: #f5a623; }
.schedule-icon { background: #f5ecff; color: #9c27b0; }
.stat-icon { background: #e7fbfc; color: #00bcd4; }
.review-icon { background: #fdebf2; color: #e91e63; }
.cert-icon { background: #f3efe9; color: #795548; }

.worker-item span:not(.arrow-icon):not(.badge):not(.amount):not(.review-badge):not(.cert-status) {
  font-size: 15px;
  color: #1f2330;
  font-weight: 500;
}

.badge {
  padding: 2px 8px;
  font-size: 12px;
  color: #fff;
  background: #ff4d4f;
  border-radius: 999px;
}

.amount {
  margin-right: 2px;
  font-size: 14px;
  font-weight: 700;
  color: #ff976a !important;
}

.review-badge {
  font-size: 12px;
  color: #1989fa !important;
}

.cert-status {
  padding: 3px 8px;
  font-size: 12px;
  border-radius: 999px;
}

.cert-status.verified {
  color: #07c160;
  background: #e8f7ef;
}

.cert-status.pending {
  color: #ff976a;
  background: #fff4e8;
}

.cert-status.none {
  color: #999;
  background: #f3f4f6;
}

.become-worker {
  padding: 16px;
}

.worker-promo {
  display: flex;
  align-items: center;
  gap: 14px;
}

.promo-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  border-radius: 18px;
  background: linear-gradient(135deg, #fff7e6 0%, #ffe7cb 100%);
  flex-shrink: 0;
}

.promo-text {
  flex: 1;
  min-width: 0;
}

.promo-text h4 {
  margin: 0 0 6px;
  font-size: 17px;
  font-weight: 700;
  color: #1f2330;
}

.promo-text p {
  margin: 0 0 10px;
  font-size: 13px;
  color: #8a90a2;
}

.promo-features {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.promo-features span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #07c160;
}

.application-status {
  padding: 18px;
}

.status-card {
  text-align: center;
}

.status-card h4 {
  margin: 14px 0 8px;
  font-size: 18px;
  font-weight: 700;
}

.status-card p {
  margin: 0 0 16px;
  font-size: 14px;
  line-height: 1.6;
  color: #8a90a2;
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
  color: #667085;
}

.status-card.pending h4 {
  color: #1989fa;
}

.status-card.rejected h4 {
  color: #ff4d4f;
}

.logout-btn {
  margin: 8px 0 34px;
}

:deep(.van-nav-bar) {
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(12px);
}

:deep(.van-nav-bar__title) {
  font-weight: 700;
  color: #1f2330;
}

:deep(.van-grid-item__content) {
  background: transparent;
}
</style>