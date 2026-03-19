<template>
  <div class="orders-container">
    <van-nav-bar title="我的订单" fixed />
    
    <van-tabs v-model:active="activeTab" animated swipeable>
      <van-tab title="全部" name="all">
        <div class="order-list" v-if="allOrders.length > 0">
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
              <van-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</van-tag>
            </div>
            
            <div class="order-content">
              <div class="skill-info">
                <div class="skill-header">
                  <h3 class="skill-title">{{ order.skillTitle }}</h3>
                  <span class="skill-price">¥{{ order.price.toFixed(2) }}</span>
                </div>
                <div class="skill-desc">{{ order.description }}</div>
                <div class="service-time">
                  <van-icon name="clock-o" />
                  <span>{{ order.serviceDate }} {{ order.serviceTime }}</span>
                </div>
              </div>
              
              <div class="worker-info">
                <van-image round width="40" height="40" :src="order.workerAvatar" />
                <div class="worker-detail">
                  <span class="worker-name">{{ order.workerName }}</span>
                  <span class="worker-rating">评分 {{ order.workerRating }}</span>
                </div>
                <van-button size="small" plain type="primary" @click.stop="contactWorker(order)">
                  联系
                </van-button>
              </div>
            </div>
            
            <div class="order-actions">
              <template v-if="order.status === 0">
                <van-button size="small" plain type="default" @click.stop="cancelOrder(order)">取消订单</van-button>
                <van-button size="small" type="primary" @click.stop="payOrder(order)">立即支付</van-button>
              </template>
              <template v-else-if="order.status === 2 || order.status === 3">
                <van-button size="small" plain type="danger" @click.stop="applyRefund(order)">申请退款</van-button>
                <van-button size="small" type="primary" @click.stop="confirmService(order)">确认完成</van-button>
              </template>
              <template v-else-if="order.status === 4">
                <van-button size="small" type="primary" @click.stop="rateOrder(order)">立即评价</van-button>
              </template>
              <template v-else-if="order.status === 5">
                <van-button size="small" plain type="primary" @click.stop="viewReceipt(order)">查看收据</van-button>
                <van-button size="small" plain type="default" @click.stop="rebook(order)">再次预约</van-button>
              </template>
            </div>
          </div>
        </div>
        
        <div v-else class="empty-state">
          <van-icon name="orders-o" size="64px" color="#ccc" />
          <p>暂无订单</p>
          <van-button type="primary" @click="goToBrowse">去浏览技能</van-button>
        </div>
      </van-tab>
      
      <van-tab title="待支付" name="pending">
        <div class="order-list" v-if="pendingOrders.length > 0">
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
              <van-tag type="warning">待支付</van-tag>
            </div>
            
            <div class="order-content">
              <div class="skill-info">
                <div class="skill-header">
                  <h3 class="skill-title">{{ order.skillTitle }}</h3>
                  <span class="skill-price">¥{{ order.price.toFixed(2) }}</span>
                </div>
                <div class="skill-desc">{{ order.description }}</div>
                <div class="service-time">
                  <van-icon name="clock-o" />
                  <span>{{ order.serviceDate }} {{ order.serviceTime }}</span>
                </div>
              </div>
              
              <div class="worker-info">
                <van-image round width="40" height="40" :src="order.workerAvatar" />
                <div class="worker-detail">
                  <span class="worker-name">{{ order.workerName }}</span>
                </div>
              </div>
            </div>
            
            <div class="order-actions">
              <van-button size="small" plain type="default" @click.stop="cancelOrder(order)">取消订单</van-button>
              <van-button size="small" type="primary" @click.stop="payOrder(order)">立即支付</van-button>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <van-icon name="clock-o" size="64px" color="#ccc" />
          <p>暂无待支付订单</p>
        </div>
      </van-tab>
      
      <van-tab title="待服务" name="service">
        <div class="order-list" v-if="serviceOrders.length > 0">
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
              <van-tag type="primary">{{ getStatusText(order.status) }}</van-tag>
            </div>
            
            <div class="order-content">
              <div class="skill-info">
                <div class="skill-header">
                  <h3 class="skill-title">{{ order.skillTitle }}</h3>
                  <span class="skill-price">¥{{ order.price.toFixed(2) }}</span>
                </div>
                <div class="skill-desc">{{ order.description }}</div>
                <div class="service-time">
                  <van-icon name="clock-o" />
                  <span>{{ order.serviceDate }} {{ order.serviceTime }}</span>
                </div>
              </div>
              
              <div class="worker-info">
                <van-image round width="40" height="40" :src="order.workerAvatar" />
                <div class="worker-detail">
                  <span class="worker-name">{{ order.workerName }}</span>
                  <span class="worker-rating">评分 {{ order.workerRating }}</span>
                </div>
                <van-button size="small" plain type="primary" @click.stop="contactWorker(order)">联系</van-button>
              </div>
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
            
            <div class="order-actions">
              <van-button size="small" plain type="danger" @click.stop="applyRefund(order)">申请退款</van-button>
              <van-button size="small" type="primary" @click.stop="confirmService(order)">确认完成</van-button>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <van-icon name="service" size="64px" color="#ccc" />
          <p>暂无待服务订单</p>
        </div>
      </van-tab>
      
      <van-tab title="已完成" name="completed">
        <div class="order-list" v-if="completedOrders.length > 0">
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
              <van-tag type="success">{{ getStatusText(order.status) }}</van-tag>
            </div>
            
            <div class="order-content">
              <div class="skill-info">
                <div class="skill-header">
                  <h3 class="skill-title">{{ order.skillTitle }}</h3>
                  <span class="skill-price">¥{{ order.price.toFixed(2) }}</span>
                </div>
                <div class="skill-desc">{{ order.description }}</div>
                <div class="service-time">
                  <van-icon name="clock-o" />
                  <span>{{ order.serviceDate }} {{ order.serviceTime }}</span>
                </div>
                <div v-if="order.rating" class="rating-info">
                  <van-rate readonly :model-value="order.rating" size="14px" color="#ff976a" />
                  <span class="rating-text">{{ order.rating }}分</span>
                </div>
              </div>
              
              <div class="worker-info">
                <van-image round width="40" height="40" :src="order.workerAvatar" />
                <div class="worker-detail">
                  <span class="worker-name">{{ order.workerName }}</span>
                </div>
              </div>
            </div>
            
            <div class="order-actions">
              <van-button size="small" plain type="primary" @click.stop="viewReceipt(order)">查看收据</van-button>
              <van-button size="small" plain type="default" @click.stop="rebook(order)">再次预约</van-button>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <van-icon name="checked" size="64px" color="#ccc" />
          <p>暂无已完成订单</p>
        </div>
      </van-tab>
      
      <van-tab title="退款/取消" name="refund">
        <div class="order-list" v-if="refundOrders.length > 0">
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
              <van-tag :type="order.status === 6 ? 'warning' : 'danger'">{{ getStatusText(order.status) }}</van-tag>
            </div>
            
            <div class="order-content">
              <div class="skill-info">
                <div class="skill-header">
                  <h3 class="skill-title">{{ order.skillTitle }}</h3>
                  <span class="skill-price">¥{{ order.price.toFixed(2) }}</span>
                </div>
                <div class="skill-desc">{{ order.description }}</div>
                <div v-if="order.refundReason" class="refund-reason">
                  <van-icon name="info-o" />
                  <span>{{ order.refundReason }}</span>
                </div>
              </div>
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
        <div v-else class="empty-state">
          <van-icon name="revoke" size="64px" color="#ccc" />
          <p>暂无退款/取消订单</p>
        </div>
      </van-tab>
    </van-tabs>

    <van-popup 
      v-model:show="showDetailPopup" 
      position="bottom" 
      :style="{ height: '85%' }"
      round
    >
      <div class="order-detail-popup" v-if="selectedOrder">
        <div class="popup-header">
          <h3>订单详情</h3>
          <van-icon name="cross" @click="showDetailPopup = false" />
        </div>
        
        <div class="popup-content">
          <div class="detail-section">
            <div class="section-title">订单状态</div>
            <div class="status-row">
              <van-tag :type="getStatusType(selectedOrder.status)" size="large">
                {{ getStatusText(selectedOrder.status) }}
              </van-tag>
            </div>
          </div>
          
          <div class="detail-section">
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
          
          <div class="detail-section">
            <div class="section-title">服务者信息</div>
            <div class="worker-card">
              <van-image round width="50" height="50" :src="selectedOrder.workerAvatar" />
              <div class="worker-info">
                <span class="name">{{ selectedOrder.workerName }}</span>
                <span class="rating">评分 {{ selectedOrder.workerRating }}</span>
              </div>
              <van-button size="small" type="primary" @click="contactWorker(selectedOrder)">
                联系
              </van-button>
            </div>
          </div>
          
          <div class="detail-section">
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
          
          <div v-if="selectedOrder.remark" class="detail-section">
            <div class="section-title">备注信息</div>
            <p class="remark">{{ selectedOrder.remark }}</p>
          </div>
        </div>
        
        <div class="popup-actions">
          <template v-if="selectedOrder.status === 0">
            <van-button block plain type="default" @click="cancelOrder(selectedOrder)">取消订单</van-button>
            <van-button block type="primary" @click="payOrder(selectedOrder)">立即支付</van-button>
          </template>
          <template v-else-if="selectedOrder.status === 2 || selectedOrder.status === 3">
            <van-button block plain type="danger" @click="applyRefund(selectedOrder)">申请退款</van-button>
            <van-button block type="primary" @click="confirmService(selectedOrder)">确认完成</van-button>
          </template>
          <template v-else-if="selectedOrder.status === 4">
            <van-button block type="primary" @click="rateOrder(selectedOrder)">立即评价</van-button>
          </template>
          <template v-else>
            <van-button block plain type="primary" @click="rebook(selectedOrder)">再次预约</van-button>
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

    <van-tabbar v-model="activeTabbar" fixed>
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
import { getOrderList, payOrder as apiPayOrder, cancelOrder as apiCancelOrder } from '@/api/order'
import { createReview } from '@/api/review'

interface Order {
  id: number
  orderNo: string
  skillTitle: string
  description: string
  price: number
  serviceDate: string
  serviceTime: string
  address: string
  workerName: string
  workerAvatar: string
  workerRating: number
  status: number
  createTime: string
  payTime?: string
  completeTime?: string
  rating?: number
  refundReason?: string
  remark?: string
}

const router = useRouter()
const activeTab = ref('all')
const activeTabbar = ref(2)
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

const loadOrders = async () => {
  orderLoading.value = true
  try {
    const data = await getOrderList({ page: 1, size: 50 }) as { records?: Array<any> }
    const records = data?.records ?? []
    orders.value = records.map((r: any) => ({
      id: r.orderId ?? r.id,
      orderNo: `SC${String(r.orderId ?? r.id).padStart(8, '0')}`,
      skillTitle: r.skillTitle ?? '技能服务',
      description: r.description ?? '',
      price: Number(r.amount ?? 0),
      serviceDate: r.scheduleDate ?? '',
      serviceTime: r.timeSlot ?? '',
      address: r.location ?? '',
      workerName: r.workerName ?? '服务者',
      workerAvatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
      workerRating: r.workerRating ?? 5,
      status: r.status ?? 0,
      createTime: r.createTime ?? '',
      payTime: r.payTime ?? '',
      completeTime: r.completeTime ?? '',
      remark: r.remark ?? '',
      rating: r.rating,
      refundReason: r.refundReason
    }))
  } catch {
    orders.value = []
  } finally {
    orderLoading.value = false
  }
}

onMounted(() => {
  loadOrders()
})

const allOrders = computed(() => orders.value)
const pendingOrders = computed(() => orders.value.filter(o => o.status === 0))
const serviceOrders = computed(() => orders.value.filter(o => o.status === 2 || o.status === 3))
const completedOrders = computed(() => orders.value.filter(o => o.status === 5))
const refundOrders = computed(() => orders.value.filter(o => o.status === 6 || o.status === 7 || o.status === 8))

const getStatusType = (status: number): string => {
  const types: Record<number, string> = {
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
  return types[status] || 'default'
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

const payOrder = async (order: Order) => {
  try {
    await apiPayOrder(order.id)
    showSuccessToast('支付成功！')
    loadOrders()
  } catch (err) {
    showToast((err as Error)?.message || '支付失败')
  }
}

const cancelOrder = async (order: Order) => {
  try {
    await apiCancelOrder(order.id)
    showSuccessToast('订单已取消')
    loadOrders()
  } catch (err) {
    showToast((err as Error)?.message || '取消失败')
  }
}

const confirmService = (order: Order) => {
  showSuccessToast('确认服务完成！')
  order.status = 4
  order.completeTime = new Date().toLocaleString()
}

const applyRefund = (order: Order) => {
  refundOrder.value = order
  refundForm.value.reason = ''
  showRefundDialog.value = true
}

const submitRefund = () => {
  if (!refundForm.value.reason.trim()) {
    showFailToast('请输入退款原因')
    return
  }
  if (refundOrder.value) {
    refundOrder.value.status = 6
    refundOrder.value.refundReason = refundForm.value.reason
    showSuccessToast('退款申请已提交')
  }
  showRefundDialog.value = false
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
    loadOrders()
  } catch (err) {
    showToast((err as Error)?.message || '评价失败')
  }
}

const viewReceipt = (order: Order) => {
  showToast('查看收据功能开发中')
}

const rebook = (order: Order) => {
  showToast(`再次预约: ${order.skillTitle}`)
  router.push(`/skill/${order.id}`)
}
</script>

<style scoped>
.orders-container {
  padding-top: 46px;
  padding-bottom: 100px;
  min-height: 100vh;
  background: #f5f5f5;
}

.order-list {
  padding: 10px;
}

.order-card {
  background: white;
  border-radius: 12px;
  margin-bottom: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.order-id .label {
  color: #999;
  font-size: 12px;
  margin-right: 8px;
}

.order-id .value {
  color: #333;
  font-size: 13px;
  font-weight: 500;
}

.order-content {
  padding: 15px;
}

.skill-info {
  margin-bottom: 12px;
}

.skill-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.skill-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
  flex: 1;
}

.skill-price {
  font-size: 18px;
  font-weight: 600;
  color: #ff976a;
  margin-left: 10px;
}

.skill-desc {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
  line-height: 1.4;
}

.service-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

.worker-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 12px;
  border-top: 1px dashed #f0f0f0;
}

.worker-detail {
  flex: 1;
}

.worker-name {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.worker-rating {
  font-size: 12px;
  color: #ff976a;
}

.progress-bar {
  padding: 15px;
  background: #fafafa;
  border-top: 1px solid #f0f0f0;
}

.progress-track {
  height: 4px;
  background: #e0e0e0;
  border-radius: 2px;
  margin-bottom: 8px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
  transition: width 0.3s ease;
}

.progress-labels {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #999;
}

.progress-labels span.active {
  color: #667eea;
  font-weight: 500;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 15px;
  border-top: 1px solid #f0f0f0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state p {
  margin: 15px 0;
}

.rating-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.rating-text {
  font-size: 13px;
  color: #ff976a;
}

.refund-reason {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  margin-top: 8px;
  padding: 8px;
  background: #fff7e6;
  border-radius: 6px;
  font-size: 12px;
  color: #ff976a;
}

.refund-progress {
  padding: 15px;
}

.order-detail-popup {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.popup-header h3 {
  margin: 0;
  font-size: 17px;
}

.popup-content {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.detail-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  padding-left: 10px;
  border-left: 3px solid #667eea;
}

.status-row {
  padding: 10px 0;
}

.info-grid {
  display: grid;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.info-item .label {
  font-size: 13px;
  color: #999;
}

.info-item .value {
  font-size: 13px;
  color: #333;
  text-align: right;
  flex: 1;
  margin-left: 10px;
}

.info-item .value.price {
  font-size: 18px;
  font-weight: 600;
  color: #ff976a;
}

.worker-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.worker-card .worker-info {
  flex: 1;
  border: none;
  padding: 0;
}

.worker-card .name {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.worker-card .rating {
  font-size: 12px;
  color: #ff976a;
}

.remark {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  margin: 0;
  padding: 10px;
  background: #fafafa;
  border-radius: 6px;
}

.popup-actions {
  padding: 15px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  gap: 10px;
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
  color: #333;
}
</style>
