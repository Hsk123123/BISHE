<template>
  <div class="orders-container">
    <van-nav-bar title="我的订单" fixed />

    <section class="orders-hero">
      <div class="hero-content">
        <div class="hero-badge">My Orders</div>
        <h1>订单中心</h1>
        <p>查看服务进度、联系服务者、管理你的预约订单</p>
      </div>
      <div class="hero-glow hero-glow-1"></div>
      <div class="hero-glow hero-glow-2"></div>
    </section>

    <section class="tabs-shell">
      <van-tabs v-model:active="activeTab" animated swipeable class="orders-tabs">
        <van-tab title="全部" name="all">
          <div v-if="orderLoading" class="state-card">
            <van-loading size="24px" vertical>加载中...</van-loading>
          </div>

          <div v-else-if="allOrders.length > 0" class="order-list">
            <div
              v-for="order in allOrders"
              :key="order.id"
              class="order-card"
              @click="showOrderDetail(order)"
            >
              <div class="order-header">
                <div class="order-id">
                  <span class="label">订单号</span>
                  <span class="value">{{ order.orderNo }}</span>
                </div>
                <van-tag round :type="getStatusType(order.status)">
                  {{ getStatusText(order.status) }}
                </van-tag>
              </div>

              <div class="order-content">
                <div class="skill-header">
                  <div class="skill-text">
                    <h3 class="skill-title">{{ order.skillTitle }}</h3>
                    <p class="skill-desc">{{ order.description }}</p>
                  </div>
                  <div class="skill-price-block">
                    <span class="skill-price">¥{{ order.price.toFixed(2) }}</span>
                  </div>
                </div>

                <div class="meta-row">
                  <span class="meta-pill">
                    <van-icon name="clock-o" />
                    {{ order.serviceDate }} {{ order.serviceTime }}
                  </span>
                </div>

                <div class="worker-panel">
                  <div class="worker-left">
                    <van-image round width="42" height="42" :src="order.workerAvatar" />
                    <div class="worker-detail">
                      <span class="worker-name">{{ order.workerName }}</span>
                      <span v-if="order.workerRating !== null" class="worker-rating">
                        评分 {{ order.workerRating }}
                      </span>
                    </div>
                  </div>
                  <van-button size="small" plain type="primary" round @click.stop="contactWorker(order)">
                    联系
                  </van-button>
                </div>
              </div>

              <div class="order-actions">
                <template v-if="order.status === 0">
                  <van-button size="small" round plain type="default" @click.stop="cancelOrderAction(order)">
                    取消订单
                  </van-button>
                  <van-button size="small" round type="primary" @click.stop="payOrderAction(order)">
                    立即支付
                  </van-button>
                </template>

                <template v-else-if="order.status === 2 || order.status === 3">
                  <van-button size="small" round plain type="danger" @click.stop="applyRefund(order)">
                    申请退款
                  </van-button>
                </template>

                <template v-else-if="order.status === 4">
                  <van-button size="small" round type="primary" @click.stop="rateOrder(order)">
                    立即评价
                  </van-button>
                </template>

                <template v-else-if="order.status === 5">
                  <van-button size="small" round plain type="primary" @click.stop="viewReceipt(order)">
                    查看收据
                  </van-button>
                  <van-button size="small" round plain type="default" @click.stop="rebook(order)">
                    再次预约
                  </van-button>
                </template>
              </div>
            </div>
          </div>

          <div v-else class="empty-state">
            <div class="empty-icon-wrap">
              <van-icon name="orders-o" size="36" />
            </div>
            <p>暂无订单</p>
            <van-button type="primary" round @click="goToBrowse">去浏览技能</van-button>
          </div>
        </van-tab>

        <van-tab title="待支付" name="pending">
          <div v-if="orderLoading" class="state-card">
            <van-loading size="24px" vertical>加载中...</van-loading>
          </div>

          <div v-else-if="pendingOrders.length > 0" class="order-list">
            <div
              v-for="order in pendingOrders"
              :key="order.id"
              class="order-card"
              @click="showOrderDetail(order)"
            >
              <div class="order-header">
                <div class="order-id">
                  <span class="label">订单号</span>
                  <span class="value">{{ order.orderNo }}</span>
                </div>
                <van-tag round type="warning">待支付</van-tag>
              </div>

              <div class="order-content">
                <div class="skill-header">
                  <div class="skill-text">
                    <h3 class="skill-title">{{ order.skillTitle }}</h3>
                    <p class="skill-desc">{{ order.description }}</p>
                  </div>
                  <div class="skill-price-block">
                    <span class="skill-price">¥{{ order.price.toFixed(2) }}</span>
                  </div>
                </div>

                <div class="meta-row">
                  <span class="meta-pill">
                    <van-icon name="clock-o" />
                    {{ order.serviceDate }} {{ order.serviceTime }}
                  </span>
                </div>

                <div class="worker-panel">
                  <div class="worker-left">
                    <van-image round width="42" height="42" :src="order.workerAvatar" />
                    <div class="worker-detail">
                      <span class="worker-name">{{ order.workerName }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="order-actions">
                <van-button size="small" round plain type="default" @click.stop="cancelOrderAction(order)">
                  取消订单
                </van-button>
                <van-button size="small" round type="primary" @click.stop="payOrderAction(order)">
                  立即支付
                </van-button>
              </div>
            </div>
          </div>

          <div v-else class="empty-state">
            <div class="empty-icon-wrap">
              <van-icon name="clock-o" size="36" />
            </div>
            <p>暂无待支付订单</p>
          </div>
        </van-tab>

        <van-tab title="待服务" name="service">
          <div v-if="orderLoading" class="state-card">
            <van-loading size="24px" vertical>加载中...</van-loading>
          </div>

          <div v-else-if="serviceOrders.length > 0" class="order-list">
            <div
              v-for="order in serviceOrders"
              :key="order.id"
              class="order-card"
              @click="showOrderDetail(order)"
            >
              <div class="order-header">
                <div class="order-id">
                  <span class="label">订单号</span>
                  <span class="value">{{ order.orderNo }}</span>
                </div>
                <van-tag round type="primary">{{ getStatusText(order.status) }}</van-tag>
              </div>

              <div class="order-content">
                <div class="skill-header">
                  <div class="skill-text">
                    <h3 class="skill-title">{{ order.skillTitle }}</h3>
                    <p class="skill-desc">{{ order.description }}</p>
                  </div>
                  <div class="skill-price-block">
                    <span class="skill-price">¥{{ order.price.toFixed(2) }}</span>
                  </div>
                </div>

                <div class="meta-row">
                  <span class="meta-pill">
                    <van-icon name="clock-o" />
                    {{ order.serviceDate }} {{ order.serviceTime }}
                  </span>
                </div>

                <div class="worker-panel">
                  <div class="worker-left">
                    <van-image round width="42" height="42" :src="order.workerAvatar" />
                    <div class="worker-detail">
                      <span class="worker-name">{{ order.workerName }}</span>
                      <span v-if="order.workerRating !== null" class="worker-rating">
                        评分 {{ order.workerRating }}
                      </span>
                    </div>
                  </div>
                  <van-button size="small" round plain type="primary" @click.stop="contactWorker(order)">
                    联系
                  </van-button>
                </div>

                <div class="progress-bar">
                  <div class="progress-track">
                    <div class="progress-fill" :style="{ width: getProgressWidth(order.status) }"></div>
                  </div>
                  <div class="progress-labels">
                    <span :class="{ active: order.status >= 1 }">待接单</span>
                    <span :class="{ active: order.status >= 2 }">已接单</span>
                    <span :class="{ active: order.status >= 3 }">服务中</span>
                    <span :class="{ active: order.status >= 4 }">待确认</span>
                  </div>
                </div>
              </div>

              <div class="order-actions">
                <van-button size="small" round plain type="danger" @click.stop="applyRefund(order)">
                  申请退款
                </van-button>
              </div>
            </div>
          </div>

          <div v-else class="empty-state">
            <div class="empty-icon-wrap">
              <van-icon name="service-o" size="36" />
            </div>
            <p>暂无待服务订单</p>
          </div>
        </van-tab>

        <van-tab title="已完成" name="completed">
          <div v-if="orderLoading" class="state-card">
            <van-loading size="24px" vertical>加载中...</van-loading>
          </div>

          <div v-else-if="completedOrders.length > 0" class="order-list">
            <div
              v-for="order in completedOrders"
              :key="order.id"
              class="order-card"
              @click="showOrderDetail(order)"
            >
              <div class="order-header">
                <div class="order-id">
                  <span class="label">订单号</span>
                  <span class="value">{{ order.orderNo }}</span>
                </div>
                <van-tag round type="success">{{ getStatusText(order.status) }}</van-tag>
              </div>

              <div class="order-content">
                <div class="skill-header">
                  <div class="skill-text">
                    <h3 class="skill-title">{{ order.skillTitle }}</h3>
                    <p class="skill-desc">{{ order.description }}</p>
                  </div>
                  <div class="skill-price-block">
                    <span class="skill-price">¥{{ order.price.toFixed(2) }}</span>
                  </div>
                </div>

                <div class="meta-row">
                  <span class="meta-pill">
                    <van-icon name="clock-o" />
                    {{ order.serviceDate }} {{ order.serviceTime }}
                  </span>
                </div>

                <div v-if="order.rating !== null" class="rating-info">
                  <van-rate readonly :model-value="order.rating" size="14px" color="#ff976a" />
                  <span class="rating-text">{{ order.rating }}分</span>
                </div>

                <div class="worker-panel">
                  <div class="worker-left">
                    <van-image round width="42" height="42" :src="order.workerAvatar" />
                    <div class="worker-detail">
                      <span class="worker-name">{{ order.workerName }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="order-actions">
                <van-button size="small" round plain type="primary" @click.stop="viewReceipt(order)">
                  查看收据
                </van-button>
                <van-button size="small" round plain type="default" @click.stop="rebook(order)">
                  再次预约
                </van-button>
              </div>
            </div>
          </div>

          <div v-else class="empty-state">
            <div class="empty-icon-wrap">
              <van-icon name="checked" size="36" />
            </div>
            <p>暂无已完成订单</p>
          </div>
        </van-tab>

        <van-tab title="退款/取消" name="refund">
          <div v-if="orderLoading" class="state-card">
            <van-loading size="24px" vertical>加载中...</van-loading>
          </div>

          <div v-else-if="refundOrders.length > 0" class="order-list">
            <div
              v-for="order in refundOrders"
              :key="order.id"
              class="order-card"
              @click="showOrderDetail(order)"
            >
              <div class="order-header">
                <div class="order-id">
                  <span class="label">订单号</span>
                  <span class="value">{{ order.orderNo }}</span>
                </div>
                <van-tag round :type="order.status === 6 ? 'warning' : 'danger'">
                  {{ getStatusText(order.status) }}
                </van-tag>
              </div>

              <div class="order-content">
                <div class="skill-header">
                  <div class="skill-text">
                    <h3 class="skill-title">{{ order.skillTitle }}</h3>
                    <p class="skill-desc">{{ order.description }}</p>
                  </div>
                  <div class="skill-price-block">
                    <span class="skill-price">¥{{ order.price.toFixed(2) }}</span>
                  </div>
                </div>

                <div v-if="order.refundReason" class="refund-reason">
                  <van-icon name="info-o" />
                  <span>{{ order.refundReason }}</span>
                </div>

                <div v-if="order.status === 6" class="refund-progress">
                  <van-steps :active="1" active-icon="clock" inactive-icon="info">
                    <van-step>申请退款</van-step>
                    <van-step>审核中</van-step>
                    <van-step>退款成功</van-step>
                  </van-steps>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="empty-state">
            <div class="empty-icon-wrap">
              <van-icon name="revoke" size="36" />
            </div>
            <p>暂无退款/取消订单</p>
          </div>
        </van-tab>
      </van-tabs>
    </section>

    <van-popup
      v-model:show="showDetailPopup"
      position="bottom"
      :style="{ height: '85%' }"
      round
    >
      <div class="order-detail-popup" v-if="selectedOrder">
        <div class="popup-header">
          <div>
            <h3>订单详情</h3>
            <p>查看当前订单的完整服务信息</p>
          </div>
          <van-icon name="cross" @click="showDetailPopup = false" />
        </div>

        <div class="popup-content">
          <div class="detail-card">
            <div class="section-title">订单状态</div>
            <div class="status-row">
              <van-tag :type="getStatusType(selectedOrder.status)" size="large" round>
                {{ getStatusText(selectedOrder.status) }}
              </van-tag>
            </div>
          </div>

          <div class="detail-card">
            <div class="section-title">服务信息</div>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">服务项目</span>
                <span class="value">{{ selectedOrder.skillTitle }}</span>
              </div>
              <div class="info-item">
                <span class="label">服务时间</span>
                <span class="value">{{ selectedOrder.serviceDate }} {{ selectedOrder.serviceTime }}</span>
              </div>
              <div class="info-item">
                <span class="label">服务地点</span>
                <span class="value">{{ selectedOrder.address }}</span>
              </div>
              <div class="info-item">
                <span class="label">订单金额</span>
                <span class="value price">¥{{ selectedOrder.price.toFixed(2) }}</span>
              </div>
            </div>
          </div>

          <div class="detail-card">
            <div class="section-title">服务者信息</div>
            <div class="worker-card">
              <div class="worker-left">
                <van-image round width="50" height="50" :src="selectedOrder.workerAvatar" />
                <div class="worker-info">
                  <span class="name">{{ selectedOrder.workerName }}</span>
                  <span v-if="selectedOrder.workerRating !== null" class="rating">
                    评分 {{ selectedOrder.workerRating }}
                  </span>
                </div>
              </div>
              <van-button size="small" type="primary" round @click="contactWorker(selectedOrder)">
                联系
              </van-button>
            </div>
          </div>

          <div class="detail-card">
            <div class="section-title">订单信息</div>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">订单编号</span>
                <span class="value">{{ selectedOrder.orderNo }}</span>
              </div>
              <div class="info-item">
                <span class="label">下单时间</span>
                <span class="value">{{ selectedOrder.createTime }}</span>
              </div>
              <div class="info-item" v-if="selectedOrder.payTime">
                <span class="label">支付时间</span>
                <span class="value">{{ selectedOrder.payTime }}</span>
              </div>
              <div class="info-item" v-if="selectedOrder.completeTime">
                <span class="label">完成时间</span>
                <span class="value">{{ selectedOrder.completeTime }}</span>
              </div>
            </div>
          </div>

          <div v-if="selectedOrder.remark" class="detail-card">
            <div class="section-title">备注信息</div>
            <p class="remark">{{ selectedOrder.remark }}</p>
          </div>
        </div>

        <div class="popup-actions">
          <template v-if="selectedOrder.status === 0">
            <van-button block round plain type="default" @click="cancelOrderAction(selectedOrder)">
              取消订单
            </van-button>
            <van-button block round type="primary" @click="payOrderAction(selectedOrder)">
              立即支付
            </van-button>
          </template>

          <template v-else-if="selectedOrder.status === 2 || selectedOrder.status === 3">
            <van-button block round plain type="danger" @click="applyRefund(selectedOrder)">
              申请退款
            </van-button>
          </template>

          <template v-else-if="selectedOrder.status === 4">
            <van-button block round type="primary" @click="rateOrder(selectedOrder)">
              立即评价
            </van-button>
          </template>

          <template v-else>
            <van-button block round plain type="primary" @click="rebook(selectedOrder)">
              再次预约
            </van-button>
          </template>
        </div>
      </div>
    </van-popup>

    <van-dialog
      v-model:show="showRatingDialog"
      title="服务评价"
      show-cancel-button
      @confirm="submitRating"
    >
      <div class="rating-form" v-if="ratingOrder">
        <div class="rating-stars">
          <span class="label">星级评分</span>
          <van-rate v-model="ratingForm.stars" size="28px" color="#ff976a" />
        </div>
        <van-field
          v-model="ratingForm.comment"
          type="textarea"
          rows="3"
          placeholder="请分享您的服务体验..."
          maxlength="200"
          show-word-limit
        />
      </div>
    </van-dialog>

    <van-dialog
      v-model:show="showRefundDialog"
      title="申请退款"
      show-cancel-button
      @confirm="submitRefund"
    >
      <div class="refund-form" v-if="refundOrder">
        <van-field
          v-model="refundForm.reason"
          type="textarea"
          rows="3"
          placeholder="请输入退款原因..."
          maxlength="200"
          show-word-limit
        />
      </div>
    </van-dialog>

    <van-tabbar route fixed>
      <van-tabbar-item icon="home-o" to="/home">首页</van-tabbar-item>
      <van-tabbar-item icon="search" to="/browse">发现</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/orders">订单</van-tabbar-item>
      <van-tabbar-item icon="user-o" to="/profile">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast, showFailToast } from 'vant'
import {
  getOrderList,
  payOrder as apiPayOrder,
  cancelOrder as apiCancelOrder,
  refundOrder as apiRefundOrder,
  type OrderVO
} from '@/api/order'
import { createReview } from '@/api/review'

interface Order {
  id: number
  skillId: number | null
  orderNo: string
  skillTitle: string
  description: string
  price: number
  serviceDate: string
  serviceTime: string
  address: string
  workerName: string
  workerAvatar: string
  workerRating: number | null
  status: number
  createTime: string
  payTime?: string
  completeTime?: string
  rating: number | null
  refundReason?: string
  remark?: string
}

interface ApiError {
  response?: {
    data?: {
      message?: string
    }
  }
  message?: string
}

const router = useRouter()
const activeTab = ref('all')
const showDetailPopup = ref(false)
const showRatingDialog = ref(false)
const showRefundDialog = ref(false)
const selectedOrder = ref<Order | null>(null)
const ratingOrder = ref<Order | null>(null)
const refundOrder = ref<Order | null>(null)

const ratingForm = ref({
  stars: 5,
  comment: ''
})

const refundForm = ref({
  reason: ''
})

const orders = ref<Order[]>([])
const orderLoading = ref(true)

const defaultAvatar = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'

const loadOrders = async () => {
  orderLoading.value = true
  try {
    const data = await getOrderList({ page: 1, size: 50 })
    const records = data?.records ?? []

    orders.value = records.map((r: OrderVO) => ({
      id: r.orderId ?? 0,
      skillId: r.skillId ?? null,
      orderNo: `SC${String(r.orderId ?? 0).padStart(8, '0')}`,
      skillTitle: r.skillTitle ?? '技能服务',
      description: r.description ?? '',
      price: Number(r.amount ?? 0),
      serviceDate: r.scheduleDate ?? '',
      serviceTime: r.timeSlot ?? '',
      address: r.location ?? '',
      workerName: r.workerName ?? '服务者',
      workerAvatar: r.workerAvatar || defaultAvatar,
      workerRating: null,
      status: Number(r.status ?? 0),
      createTime: r.createTime ?? '',
      payTime: '',
      completeTime: '',
      remark: '',
      rating: null,
      refundReason: ''
    }))
  } catch (err) {
    const error = err as ApiError
    showToast(error?.response?.data?.message || error?.message || '加载失败')
    orders.value = []
  } finally {
    orderLoading.value = false
  }
}

onMounted(() => {
  loadOrders()
})

const allOrders = computed(() => orders.value)
const pendingOrders = computed(() => orders.value.filter((o) => o.status === 0))
const serviceOrders = computed(() => orders.value.filter((o) => [1, 2, 3, 4].includes(o.status)))
const completedOrders = computed(() => orders.value.filter((o) => o.status === 5))
const refundOrders = computed(() => orders.value.filter((o) => [6, 7, 8].includes(o.status)))

type OrderTagType = 'primary' | 'success' | 'warning' | 'danger' | 'default'

const getStatusType = (status: number): OrderTagType => {
  const types: Record<number, OrderTagType> = {
    0: 'warning',
    1: 'primary',
    2: 'primary',
    3: 'primary',
    4: 'primary',
    5: 'success',
    6: 'warning',
    7: 'danger',
    8: 'default'
  }
  return types[status] ?? 'default'
}

const getStatusText = (status: number): string => {
  const texts: Record<number, string> = {
    0: '待支付',
    1: '待接单',
    2: '已接单',
    3: '服务中',
    4: '待确认',
    5: '已完成',
    6: '退款中',
    7: '已退款',
    8: '已取消'
  }
  return texts[status] || '未知'
}

const getProgressWidth = (status: number): string => {
  const widths: Record<number, string> = {
    1: '25%',
    2: '50%',
    3: '75%',
    4: '100%'
  }
  return widths[status] || '0%'
}

const showOrderDetail = (order: Order) => {
  selectedOrder.value = order
  showDetailPopup.value = true
}

const goToBrowse = () => {
  router.push('/browse')
}

const contactWorker = (order: Order) => {
  showToast(`联系服务者: ${order.workerName}`)
}

const payOrderAction = async (order: Order) => {
  try {
    await apiPayOrder(order.id)
    showSuccessToast('支付成功！')
    await loadOrders()
  } catch (err) {
    const error = err as ApiError
    showToast(error?.response?.data?.message || error?.message || '支付失败')
  }
}

const cancelOrderAction = async (order: Order) => {
  try {
    await apiCancelOrder(order.id)
    showSuccessToast('订单已取消')
    await loadOrders()
  } catch (err) {
    const error = err as ApiError
    showToast(error?.response?.data?.message || error?.message || '取消失败')
  }
}

const applyRefund = (order: Order) => {
  refundOrder.value = order
  refundForm.value.reason = ''
  showRefundDialog.value = true
}

const submitRefund = async () => {
  if (!refundOrder.value) return

  try {
    await apiRefundOrder(refundOrder.value.id)
    showSuccessToast('退款申请已提交')
    showRefundDialog.value = false
    await loadOrders()
  } catch (err) {
    const error = err as ApiError
    showFailToast(error?.response?.data?.message || error?.message || '退款申请失败')
  }
}

const rateOrder = (order: Order) => {
  ratingOrder.value = order
  ratingForm.value.stars = 5
  ratingForm.value.comment = ''
  showRatingDialog.value = true
}

const submitRating = async () => {
  if (!ratingOrder.value) return

  try {
    await createReview({
      orderId: ratingOrder.value.id,
      rating: ratingForm.value.stars,
      content: ratingForm.value.comment
    })
    showSuccessToast('评价成功！')
    showRatingDialog.value = false
    await loadOrders()
  } catch (err) {
    const error = err as ApiError
    showToast(error?.response?.data?.message || error?.message || '评价失败')
  }
}

const viewReceipt = (_order: Order) => {
  showToast('查看收据功能开发中')
}

const rebook = (order: Order) => {
  if (!order.skillId) {
    showToast('未找到技能信息')
    return
  }
  router.push(`/skill/${order.skillId}`)
}
</script>

<style scoped>
.orders-container {
  min-height: 100vh;
  padding-top: 46px;
  padding-bottom: 100px;
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
  max-width: 280px;
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

.tabs-shell {
  padding: 0 12px;
}

.order-list {
  padding: 12px 0 4px;
}

.order-card {
  margin-bottom: 12px;
  border-radius: 20px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 10px 24px rgba(31, 45, 61, 0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  background: linear-gradient(180deg, #fafbff 0%, #f5f7fc 100%);
  border-bottom: 1px solid #eef1f6;
}

.order-id .label {
  margin-right: 8px;
  font-size: 12px;
  color: #98a0b3;
}

.order-id .value {
  font-size: 13px;
  font-weight: 600;
  color: #2a3142;
}

.order-content {
  padding: 15px 16px;
}

.skill-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 14px;
  margin-bottom: 10px;
}

.skill-text {
  flex: 1;
  min-width: 0;
}

.skill-title {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 700;
  line-height: 1.4;
  color: #1f2330;
}

.skill-desc {
  margin: 0;
  font-size: 12px;
  line-height: 1.6;
  color: #7a8193;
}

.skill-price-block {
  flex-shrink: 0;
  text-align: right;
}

.skill-price {
  font-size: 20px;
  font-weight: 700;
  color: #ff976a;
  line-height: 1.2;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.meta-pill {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 10px;
  font-size: 11px;
  color: #667eea;
  background: rgba(102, 126, 234, 0.08);
  border-radius: 999px;
}

.worker-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding-top: 12px;
  border-top: 1px dashed #ebeff5;
}

.worker-left {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.worker-detail {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}

.worker-name {
  font-size: 14px;
  font-weight: 600;
  color: #2a3142;
}

.worker-rating {
  font-size: 12px;
  color: #ff976a;
}

.progress-bar {
  margin-top: 14px;
  padding: 12px;
  border-radius: 14px;
  background: #f8faff;
}

.progress-track {
  height: 5px;
  margin-bottom: 8px;
  overflow: hidden;
  background: #e4e9f2;
  border-radius: 999px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #7b61ff 100%);
  border-radius: 999px;
  transition: width 0.3s ease;
}

.progress-labels {
  display: flex;
  justify-content: space-between;
  gap: 6px;
  font-size: 11px;
  color: #98a0b3;
}

.progress-labels span.active {
  color: #667eea;
  font-weight: 600;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 16px 15px;
  border-top: 1px solid #eef1f6;
}

.state-card {
  display: flex;
  justify-content: center;
  padding: 40px 0;
  margin-top: 12px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 10px 24px rgba(31, 45, 61, 0.05);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 42px 20px;
  margin-top: 12px;
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

.rating-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 10px 0 0;
}

.rating-text {
  font-size: 13px;
  font-weight: 600;
  color: #ff976a;
}

.refund-reason {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  margin-top: 10px;
  padding: 10px 12px;
  font-size: 12px;
  line-height: 1.6;
  color: #ff976a;
  background: #fff7e9;
  border-radius: 12px;
}

.refund-progress {
  margin-top: 12px;
  padding: 12px 0 0;
}

.order-detail-popup {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f7f9fd;
}

.popup-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 16px 16px 14px;
  background: #fff;
  border-bottom: 1px solid #eef1f6;
}

.popup-header h3 {
  margin: 0 0 4px;
  font-size: 18px;
  font-weight: 700;
  color: #1f2330;
}

.popup-header p {
  margin: 0;
  font-size: 12px;
  color: #8a90a2;
}

.popup-content {
  flex: 1;
  overflow-y: auto;
  padding: 14px 14px 6px;
}

.detail-card {
  margin-bottom: 12px;
  padding: 14px;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 8px 20px rgba(31, 45, 61, 0.04);
}

.section-title {
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 700;
  color: #1f2330;
}

.status-row {
  padding: 2px 0;
}

.info-grid {
  display: grid;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.info-item .label {
  flex-shrink: 0;
  font-size: 13px;
  color: #98a0b3;
}

.info-item .value {
  flex: 1;
  font-size: 13px;
  line-height: 1.6;
  text-align: right;
  color: #2a3142;
}

.info-item .value.price {
  font-size: 18px;
  font-weight: 700;
  color: #ff976a;
}

.worker-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px;
  border-radius: 14px;
  background: #f7f9fd;
}

.worker-card .worker-left {
  flex: 1;
}

.worker-card .worker-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.worker-card .name {
  font-size: 14px;
  font-weight: 600;
  color: #2a3142;
}

.worker-card .rating {
  font-size: 12px;
  color: #ff976a;
}

.remark {
  margin: 0;
  padding: 12px;
  font-size: 13px;
  line-height: 1.7;
  color: #5f6678;
  background: #f7f9fd;
  border-radius: 12px;
}

.popup-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 15px;
  background: #fff;
  border-top: 1px solid #eef1f6;
}

.rating-form,
.refund-form {
  padding: 15px;
}

.rating-stars {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 15px;
}

.rating-stars .label {
  font-size: 14px;
  color: #2a3142;
}

:deep(.van-nav-bar) {
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(12px);
}

:deep(.van-nav-bar__title) {
  font-weight: 700;
  color: #1f2330;
}

:deep(.orders-tabs .van-tabs__wrap) {
  margin-bottom: 8px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: 0 8px 20px rgba(31, 45, 61, 0.04);
}

:deep(.orders-tabs .van-tab) {
  color: #7c8498;
  font-size: 13px;
}

:deep(.orders-tabs .van-tab--active) {
  color: #667eea;
  font-weight: 700;
}

:deep(.orders-tabs .van-tabs__line) {
  background: linear-gradient(90deg, #667eea 0%, #7b61ff 100%);
}

:deep(.van-step__title) {
  font-size: 12px;
}
</style>