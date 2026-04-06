<template>
  <div class="skill-orders-container">
    <van-nav-bar
      title="技能订单"
      left-arrow
      fixed
      @click-left="goBack"
    />

    <section class="orders-hero">
      <div class="hero-content">
        <div class="hero-badge">Skill Orders</div>
        <h1>商家订单</h1>
        <p>查看当前待接单、待服务、服务中和已完成的技能订单，及时处理服务进度</p>
      </div>
      <div class="hero-glow hero-glow-1"></div>
      <div class="hero-glow hero-glow-2"></div>
    </section>

    <section class="content-section">
      <div class="tabs-card">
        <div class="order-tabs">
          <div
            :class="['order-tab', { active: activeTab === 0 }]"
            @click="activeTab = 0"
          >
            <span class="tab-title">待接单</span>
            <span class="tab-count">{{ pendingAcceptCount }}</span>
          </div>
          <div
            :class="['order-tab', { active: activeTab === 1 }]"
            @click="activeTab = 1"
          >
            <span class="tab-title">待服务</span>
            <span class="tab-count">{{ acceptedCount }}</span>
          </div>
          <div
            :class="['order-tab', { active: activeTab === 2 }]"
            @click="activeTab = 2"
          >
            <span class="tab-title">服务中</span>
            <span class="tab-count">{{ servingCount }}</span>
          </div>
          <div
            :class="['order-tab', { active: activeTab === 3 }]"
            @click="activeTab = 3"
          >
            <span class="tab-title">已完成</span>
            <span class="tab-count">{{ completedCount }}</span>
          </div>
          <div
            :class="['order-tab', { active: activeTab === 4 }]"
            @click="activeTab = 4"
          >
            <span class="tab-title">全部</span>
            <span class="tab-count">{{ orders.length }}</span>
          </div>
        </div>
      </div>

      <div class="orders-list">
        <div v-if="loading" class="state-card">
          <van-loading type="spinner" color="#667eea" />
          <span>加载中...</span>
        </div>

        <template v-else>
          <div v-for="order in filteredOrders" :key="order.id" class="order-card">
            <div class="order-header">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <span :class="['order-status', getStatusClass(order.status)]">
                {{ getStatusText(order.status) }}
              </span>
            </div>

            <div class="order-content">
              <div class="service-info">
                <h3>{{ order.skillTitle }}</h3>
                <p class="service-time">
                  <van-icon name="clock-o" />
                  {{ order.date }} {{ order.timeSlot }}
                </p>
              </div>
              <div class="price-info">
                <span class="price">¥{{ order.amount }}</span>
              </div>
            </div>

            <div class="customer-info">
              <div class="customer-left">
                <van-image :src="order.customerAvatar" class="avatar" round />
                <div class="customer-detail">
                  <span class="name">{{ order.customerName }}</span>
                  <span class="sub-text">客户信息</span>
                </div>
              </div>

              <div class="primary-actions">
                <van-button
                  v-if="order.status === 0"
                  type="primary"
                  size="small"
                  round
                  @click="accept(order)"
                >
                  接单
                </van-button>

                <van-button
                  v-if="order.status === 1"
                  type="primary"
                  size="small"
                  round
                  @click="startService(order)"
                >
                  开始服务
                </van-button>

                <van-button
                  v-if="order.status === 2"
                  type="success"
                  size="small"
                  round
                  @click="completeService(order)"
                >
                  完成服务
                </van-button>
              </div>
            </div>

            <div class="order-actions" v-if="order.status < 3">
            </div>
          </div>

          <div v-if="filteredOrders.length === 0" class="empty-state">
            <div class="empty-icon-wrap">
              <van-icon name="orders-o" size="36" />
            </div>
            <p>暂无相关订单</p>
          </div>
        </template>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import {
  getProviderOrderList,
  acceptOrder,
  startServiceOrder,
  completeServiceOrder
} from '@/api/order'

interface ProviderOrderRecord {
  orderId?: number
  id?: number
  skillTitle?: string
  scheduleDate?: string
  timeSlot?: string
  amount?: number
  buyerAvatar?: string
  buyerName?: string
  status?: number
}

interface OrderCardItem {
  id: number
  orderNo: string
  skillTitle: string
  date: string
  timeSlot: string
  amount: number
  customerAvatar: string
  customerName: string
  status: number
}

const router = useRouter()
const activeTab = ref(4)
const loading = ref(true)

const orders = ref<OrderCardItem[]>([])

const defaultAvatar = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'

/**
 * 前端展示状态：
 * 0 = 待接单
 * 1 = 待服务（已接单，可开始服务）
 * 2 = 服务中
 * 3 = 已完成
 * 4 = 已取消
 */
const mapProviderStatus = (backendStatus: number): number => {
  switch (backendStatus) {
    case 1:
      return 0
    case 2:
      return 1
    case 3:
      return 2
    case 4:
    case 5:
      return 3
    case 8:
      return 4
    default:
      return backendStatus
  }
}

const loadOrders = async () => {
  loading.value = true
  try {
    const data = (await getProviderOrderList({
      page: 1,
      size: 50
    })) as unknown as { records?: ProviderOrderRecord[] }

    const records = data?.records ?? []

    orders.value = records.map((r) => {
      const id = r.orderId ?? r.id ?? 0
      const mappedStatus = mapProviderStatus(Number(r.status ?? 0))

      return {
        id,
        orderNo: `SC${String(id).padStart(8, '0')}`,
        skillTitle: r.skillTitle ?? '技能服务',
        date: r.scheduleDate ?? '',
        timeSlot: r.timeSlot ?? '',
        amount: Number(r.amount ?? 0),
        customerAvatar: r.buyerAvatar || defaultAvatar,
        customerName: r.buyerName ?? '客户',
        status: mappedStatus
      }
    })
  } catch (error) {
    console.error('[skill-orders] loadOrders error =', error)
    orders.value = []
    showToast('加载订单失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadOrders()
})

const pendingAcceptCount = computed(() => orders.value.filter((o) => o.status === 0).length)
const acceptedCount = computed(() => orders.value.filter((o) => o.status === 1).length)
const servingCount = computed(() => orders.value.filter((o) => o.status === 2).length)
const completedCount = computed(() => orders.value.filter((o) => o.status === 3).length)

const filteredOrders = computed(() => {
  if (activeTab.value === 4) {
    return orders.value
  }
  return orders.value.filter((order) => order.status === activeTab.value)
})

const goBack = () => {
  router.back()
}

const getStatusClass = (status: number) => {
  const classes: Record<number, string> = {
    0: 'pending',
    1: 'accepted',
    2: 'serving',
    3: 'completed',
    4: 'cancelled'
  }
  return classes[status] || ''
}

const getStatusText = (status: number) => {
  const texts: Record<number, string> = {
    0: '待接单',
    1: '待服务',
    2: '服务中',
    3: '已完成',
    4: '已取消'
  }
  return texts[status] || '未知状态'
}

const accept = async (order: OrderCardItem) => {
  try {
    await acceptOrder(order.id)
    showSuccessToast('接单成功')
    await loadOrders()
  } catch (error) {
    const err = error as { response?: { data?: { message?: string } }; message?: string }
    showToast(err?.response?.data?.message || err?.message || '接单失败')
  }
}

const startService = async (order: OrderCardItem) => {
  try {
    await startServiceOrder(order.id)
    showSuccessToast('已开始服务')
    await loadOrders()
  } catch (error) {
    const err = error as { response?: { data?: { message?: string } }; message?: string }
    showToast(err?.response?.data?.message || err?.message || '操作失败')
  }
}

const completeService = async (order: OrderCardItem) => {
  try {
    await completeServiceOrder(order.id)
    showSuccessToast('服务已完成，等待用户确认')
    await loadOrders()
  } catch (error) {
    const err = error as { response?: { data?: { message?: string } }; message?: string }
    showToast(err?.response?.data?.message || err?.message || '操作失败')
  }
}

const contactCustomer = () => {
  showToast('正在联系客户...')
}

const viewDetail = (order: OrderCardItem) => {
  router.push(`/order/${order.id}`)
}
</script>

<style scoped>
.skill-orders-container {
  min-height: 100vh;
  padding-top: 46px;
  padding-bottom: 90px;
  background:
    radial-gradient(circle at top right, rgba(102, 126, 234, 0.12), transparent 28%),
    linear-gradient(180deg, #f6f8fc 0%, #eef2f7 100%);
}

.orders-hero {
  position: relative;
  overflow: hidden;
  margin-bottom: 12px;
  padding: 26px 16px 26px;
  background: linear-gradient(135deg, #667eea 0%, #7b61ff 55%, #8f6bff 100%);
  color: #fff;
  border-bottom-left-radius: 24px;
  border-bottom-right-radius: 24px;
}

.hero-content {
  position: relative;
  z-index: 2;
}

.hero-badge {
  display: inline-block;
  margin-bottom: 10px;
  padding: 4px 10px;
  font-size: 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(4px);
}

.orders-hero h1 {
  margin: 0 0 8px;
  font-size: 25px;
  font-weight: 700;
  line-height: 1.3;
}

.orders-hero p {
  margin: 0;
  max-width: 320px;
  font-size: 13px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.9);
}

.hero-glow {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
  filter: blur(4px);
}

.hero-glow-1 {
  top: -8px;
  right: -10px;
  width: 120px;
  height: 120px;
}

.hero-glow-2 {
  right: 72px;
  bottom: -26px;
  width: 88px;
  height: 88px;
}

.content-section {
  padding: 0 12px;
}

.tabs-card {
  margin-bottom: 14px;
  padding: 6px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 10px 24px rgba(31, 45, 61, 0.05);
}

.order-tabs {
  display: flex;
  gap: 6px;
  overflow-x: auto;
}

.order-tab {
  flex: 1;
  min-width: 78px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 12px 8px;
  border-radius: 16px;
  color: #7c8498;
  white-space: nowrap;
  transition: all 0.25s ease;
}

.tab-title {
  font-size: 13px;
  font-weight: 500;
}

.tab-count {
  font-size: 16px;
  font-weight: 700;
}

.order-tab.active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.12), rgba(123, 97, 255, 0.12));
  color: #667eea;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-card {
  padding: 14px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 10px 24px rgba(31, 45, 61, 0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eef1f6;
}

.order-no {
  font-size: 12px;
  color: #98a0b3;
}

.order-status {
  flex-shrink: 0;
  padding: 4px 10px;
  font-size: 11px;
  font-weight: 600;
  border-radius: 999px;
}

.order-status.pending {
  background: #fff7e6;
  color: #ff976a;
}

.order-status.accepted {
  background: #eef4ff;
  color: #5b7cff;
}

.order-status.serving {
  background: #eaf4ff;
  color: #1989fa;
}

.order-status.completed {
  background: #edf9e8;
  color: #52c41a;
}

.order-status.cancelled {
  background: #f1f3f7;
  color: #8a90a2;
}

.order-content {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.service-info {
  flex: 1;
  min-width: 0;
}

.service-info h3 {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 700;
  line-height: 1.4;
  color: #1f2330;
}

.service-time {
  display: flex;
  align-items: center;
  gap: 5px;
  margin: 0;
  font-size: 12px;
  color: #7a8193;
}

.price-info {
  flex-shrink: 0;
  text-align: right;
}

.price-info .price {
  font-size: 20px;
  font-weight: 700;
  color: #ff976a;
  line-height: 1.2;
}

.customer-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 0;
  border-top: 1px dashed #ebeff5;
  border-bottom: 1px solid #eef1f6;
}

.customer-left {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.avatar {
  width: 42px;
  height: 42px;
  flex-shrink: 0;
}

.customer-detail {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.customer-detail .name {
  font-size: 14px;
  font-weight: 600;
  color: #2a3142;
}

.customer-detail .sub-text {
  font-size: 12px;
  color: #98a0b3;
}

.primary-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.order-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 12px;
}

.btn-text {
  margin-left: 4px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 42px 20px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 10px 24px rgba(31, 45, 61, 0.05);
  color: #9aa1b3;
}

.empty-icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 66px;
  height: 66px;
  border-radius: 20px;
  background: #f4f6fb;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

.state-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 40px 0;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 10px 24px rgba(31, 45, 61, 0.05);
  color: #9aa1b3;
}

:deep(.van-nav-bar) {
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(12px);
}

:deep(.van-nav-bar__title) {
  font-weight: 700;
  color: #1f2330;
}
</style>