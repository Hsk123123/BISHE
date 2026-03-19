<template>
  <div class="skill-orders-container">
    <van-nav-bar 
      title="技能订单" 
      left-arrow 
      fixed 
      @click-left="goBack"
    />
    
    <div class="content">
      <div class="order-tabs">
        <div 
          :class="['order-tab', { active: activeTab === 0 }]"
          @click="activeTab = 0"
        >
          待服务 ({{ pendingCount }})
        </div>
        <div 
          :class="['order-tab', { active: activeTab === 1 }]"
          @click="activeTab = 1"
        >
          服务中 ({{ servingCount }})
        </div>
        <div 
          :class="['order-tab', { active: activeTab === 2 }]"
          @click="activeTab = 2"
        >
          已完成 ({{ completedCount }})
        </div>
        <div 
          :class="['order-tab', { active: activeTab === 3 }]"
          @click="activeTab = 3"
        >
          全部 ({{ orders.length }})
        </div>
      </div>

      <div class="orders-list">
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
              <p class="service-duration">
                <van-icon name="todo-list-o" />
                服务时长：{{ order.duration }}小时
              </p>
            </div>
            <div class="price-info">
              <span class="price">¥{{ order.amount }}</span>
            </div>
          </div>
          
          <div class="customer-info">
            <van-image :src="order.customerAvatar" class="avatar" round />
            <div class="customer-detail">
              <span class="name">{{ order.customerName }}</span>
              <span class="phone">{{ order.customerPhone }}</span>
            </div>
            <van-button 
              v-if="order.status === 0"
              type="primary" 
              size="small" 
              @click="startService(order)"
            >
              开始服务
            </van-button>
            <van-button 
              v-if="order.status === 1"
              type="success" 
              size="small" 
              @click="completeService(order)"
            >
              完成服务
            </van-button>
          </div>
          
          <div class="order-actions" v-if="order.status < 2">
            <van-button size="small" @click="contactCustomer">
              <van-icon name="phone-o" /> 联系客户
            </van-button>
            <van-button size="small" @click="viewDetail(order)">
              查看详情
            </van-button>
          </div>
        </div>

        <div v-if="filteredOrders.length === 0" class="empty-state">
          <van-icon name="orders-o" size="48px" color="#ccc" />
          <p>暂无相关订单</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

interface Order {
  id: number
  orderNo: string
  skillTitle: string
  date: string
  timeSlot: string
  duration: number
  amount: number
  customerAvatar: string
  customerName: string
  customerPhone: string
  status: number
}

const router = useRouter()
const activeTab = ref(0)

const orders = ref<Order[]>([
  {
    id: 1,
    orderNo: 'SK202401070001',
    skillTitle: '专业家政清洁',
    date: '2024-01-08',
    timeSlot: '09:00-10:00',
    duration: 2,
    amount: 200,
    customerAvatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
    customerName: '张三',
    customerPhone: '138****1234',
    status: 0
  },
  {
    id: 2,
    orderNo: 'SK202401060002',
    skillTitle: '高级瑜伽私教',
    date: '2024-01-07',
    timeSlot: '14:00-15:00',
    duration: 1,
    amount: 200,
    customerAvatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
    customerName: '李女士',
    customerPhone: '139****5678',
    status: 1
  },
  {
    id: 3,
    orderNo: 'SK202401050003',
    skillTitle: '专业家政清洁',
    date: '2024-01-06',
    timeSlot: '10:00-11:00',
    duration: 3,
    amount: 300,
    customerAvatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
    customerName: '王先生',
    customerPhone: '137****9012',
    status: 2
  }
])

const pendingCount = computed(() => orders.value.filter(o => o.status === 0).length)
const servingCount = computed(() => orders.value.filter(o => o.status === 1).length)
const completedCount = computed(() => orders.value.filter(o => o.status === 2).length)

const filteredOrders = computed(() => {
  if (activeTab.value === 3) {
    return orders.value
  }
  return orders.value.filter(order => order.status === activeTab.value)
})

const goBack = () => {
  router.back()
}

const getStatusClass = (status: number) => {
  const classes: Record<number, string> = {
    0: 'pending',
    1: 'serving',
    2: 'completed',
    3: 'cancelled'
  }
  return classes[status] || ''
}

const getStatusText = (status: number) => {
  const texts: Record<number, string> = {
    0: '待服务',
    1: '服务中',
    2: '已完成',
    3: '已取消'
  }
  return texts[status] || ''
}

const startService = (order: Order) => {
  order.status = 1
  showToast('已开始服务')
}

const completeService = (order: Order) => {
  order.status = 2
  showToast('服务完成！收益已到账')
}

const contactCustomer = () => {
  showToast('正在联系客户...')
}

const viewDetail = (order: Order) => {
  router.push(`/order/${order.id}`)
}
</script>

<style scoped>
.skill-orders-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-top: 46px;
}

.content {
  padding: 10px;
}

.order-tabs {
  display: flex;
  background: white;
  border-radius: 12px;
  margin-bottom: 15px;
  overflow-x: auto;
}

.order-tab {
  flex: 1;
  min-width: 80px;
  text-align: center;
  padding: 12px 8px;
  font-size: 13px;
  color: #666;
  white-space: nowrap;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.order-tab.active {
  color: #667eea;
  border-bottom-color: #667eea;
  font-weight: 600;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-card {
  background: white;
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-no {
  font-size: 12px;
  color: #999;
}

.order-status {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 4px;
}

.order-status.pending {
  background: #fff7e6;
  color: #ff976a;
}

.order-status.serving {
  background: #e6f7ff;
  color: #1890ff;
}

.order-status.completed {
  background: #f6ffed;
  color: #52c41a;
}

.order-status.cancelled {
  background: #f5f5f5;
  color: #999;
}

.order-content {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
}

.service-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.service-time,
.service-duration {
  font-size: 13px;
  color: #666;
  margin: 4px 0;
  display: flex;
  align-items: center;
  gap: 4px;
}

.price-info .price {
  font-size: 20px;
  font-weight: 600;
  color: #ff976a;
}

.customer-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.avatar {
  width: 40px;
  height: 40px;
}

.customer-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.customer-detail .name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.customer-detail .phone {
  font-size: 12px;
  color: #999;
}

.order-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 12px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state p {
  margin-top: 10px;
}
</style>
