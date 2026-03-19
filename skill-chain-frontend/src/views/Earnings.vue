<template>
  <div class="earnings-container">
    <van-nav-bar 
      title="我的收益" 
      left-arrow 
      fixed 
      @click-left="goBack"
    />
    
    <div class="content">
      <div class="earnings-card">
        <div class="earnings-header">
          <div class="earnings-label">可提现收益</div>
          <div class="earnings-amount">
            <span class="currency">¥</span>
            <span class="amount">{{ availableAmount.toFixed(2) }}</span>
          </div>
        </div>
        
        <div class="earnings-stats">
          <div class="stat-item">
            <div class="stat-value">{{ frozenAmount.toFixed(2) }}</div>
            <div class="stat-label">冻结中</div>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <div class="stat-value">{{ totalEarnings.toFixed(2) }}</div>
            <div class="stat-label">累计收益</div>
          </div>
        </div>
        
        <div class="withdraw-section">
          <van-button 
            type="primary" 
            block 
            round 
            @click="showWithdrawPopup = true"
            :disabled="availableAmount < 10"
          >
            {{ availableAmount >= 10 ? '申请提现' : '满10元可提现' }}
          </van-button>
        </div>
      </div>

      <div class="section-card">
        <div class="section-header">
          <van-icon name="chart-trending-o" />
          <span>收益统计</span>
        </div>
        <div class="stats-grid">
          <div class="stats-item">
            <div class="stats-value">{{ monthlyEarnings }}</div>
            <div class="stats-label">本月收益</div>
          </div>
          <div class="stats-item">
            <div class="stats-value">{{ monthlyOrders }}</div>
            <div class="stats-label">本月订单</div>
          </div>
          <div class="stats-item">
            <div class="stats-value">{{ avgOrderValue }}</div>
            <div class="stats-label">客单价</div>
          </div>
          <div class="stats-item">
            <div class="stats-value">{{ completedOrders }}</div>
            <div class="stats-label">已完成</div>
          </div>
        </div>
      </div>

      <div class="section-card">
        <div class="section-header">
          <van-icon name="orders-o" />
          <span>收益明细</span>
        </div>
        
        <div class="transaction-tabs">
          <div 
            :class="['tab-item', { active: transactionTab === 0 }]"
            @click="transactionTab = 0"
          >
            全部
          </div>
          <div 
            :class="['tab-item', { active: transactionTab === 1 }]"
            @click="transactionTab = 1"
          >
            收入
          </div>
          <div 
            :class="['tab-item', { active: transactionTab === 2 }]"
            @click="transactionTab = 2"
          >
            提现
          </div>
        </div>

        <div class="transaction-list">
          <div 
            v-for="item in filteredTransactions" 
            :key="item.id" 
            class="transaction-item"
          >
            <div class="transaction-icon" :class="item.type">
              <van-icon :name="item.type === 'income' ? 'arrow-up' : 'arrow-down'" />
            </div>
            <div class="transaction-info">
              <div class="transaction-title">{{ item.title }}</div>
              <div class="transaction-date">{{ item.date }}</div>
            </div>
            <div class="transaction-amount" :class="item.type">
              {{ item.type === 'income' ? '+' : '-' }}¥{{ item.amount.toFixed(2) }}
            </div>
          </div>

          <div v-if="filteredTransactions.length === 0" class="empty-state">
            <van-icon name="balance-o" size="40px" color="#ccc" />
            <p>暂无收益记录</p>
          </div>
        </div>
      </div>
    </div>

    <van-popup v-model:show="showWithdrawPopup" position="bottom" round>
      <div class="withdraw-popup">
        <div class="popup-header">
          <span>申请提现</span>
          <van-icon name="cross" @click="showWithdrawPopup = false" />
        </div>
        
        <div class="withdraw-amount-section">
          <div class="withdraw-label">提现金额</div>
          <div class="withdraw-input">
            <span class="currency">¥</span>
            <input 
              type="number" 
              v-model="withdrawAmount" 
              placeholder="请输入提现金额"
              @input="onWithdrawAmountChange"
            />
          </div>
          <div class="withdraw-balance">
            可用余额 ¥{{ availableAmount.toFixed(2) }}
            <span @click="withdrawAll">全部提现</span>
          </div>
        </div>

        <div class="withdraw-method-section">
          <div class="method-label">提现方式</div>
          <div class="method-options">
            <div 
              :class="['method-item', { active: withdrawMethod === 0 }]"
              @click="withdrawMethod = 0"
            >
              <van-icon name="alipay" color="#1677ff" />
              <span>支付宝</span>
            </div>
            <div 
              :class="['method-item', { active: withdrawMethod === 1 }]"
              @click="withdrawMethod = 1"
            >
              <van-icon name="wechat" color="#07c160" />
              <span>微信</span>
            </div>
            <div 
              :class="['method-item', { active: withdrawMethod === 2 }]"
              @click="withdrawMethod = 2"
            >
              <van-icon name="card" color="#ff976a" />
              <span>银行卡</span>
            </div>
          </div>
        </div>

        <div class="withdraw-submit">
          <van-button 
            type="primary" 
            block 
            round 
            @click="submitWithdraw"
            :disabled="!canWithdraw"
            :loading="isSubmitting"
          >
            确认提现
          </van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import { getEarningsStats, applyWithdrawal, getMyWithdrawals } from '@/api/withdrawal'

interface Transaction {
  id: number
  title: string
  amount: number
  date: string
  type: 'income' | 'withdraw'
}

const router = useRouter()
const transactionTab = ref(0)
const showWithdrawPopup = ref(false)
const withdrawAmount = ref<number | ''>('')
const withdrawMethod = ref(0)
const isSubmitting = ref(false)

const availableAmount = ref(0)
const frozenAmount = ref(0)
const totalEarnings = ref(0)
const monthlyEarnings = ref(1580.00)
const monthlyOrders = ref(15)
const avgOrderValue = ref(105.33)
const completedOrders = ref(12)

const transactions = ref<Transaction[]>([])

const loadEarnings = async () => {
  try {
    const stats = await getEarningsStats() as { availableAmount?: number; frozenAmount?: number; totalEarnings?: number }
    availableAmount.value = Number(stats?.availableAmount ?? 0)
    frozenAmount.value = Number(stats?.frozenAmount ?? 0)
    totalEarnings.value = Number(stats?.totalEarnings ?? 0)
  } catch {
    // ignore
  }
}

const loadTransactions = async () => {
  try {
    const data = await getMyWithdrawals({ page: 1, size: 50 }) as { records?: Array<{ id?: number; amount?: number; createTime?: string; status?: number }> }
    const list = data?.records ?? []
    const withdrawList: Transaction[] = list.map((r: any) => ({
      id: r.id ?? 0,
      title: r.status === 1 ? '提现已到账' : r.status === 2 ? '提现已拒绝' : '提现申请中',
      amount: Number(r.amount ?? 0),
      date: r.createTime ?? '',
      type: 'withdraw' as const
    }))
    transactions.value = withdrawList
  } catch {
    transactions.value = []
  }
}

const filteredTransactions = computed(() => {
  if (transactionTab.value === 0) {
    return transactions.value
  } else if (transactionTab.value === 1) {
    return transactions.value.filter(t => t.type === 'income')
  } else {
    return transactions.value.filter(t => t.type === 'withdraw')
  }
})

const canWithdraw = computed(() => {
  const amount = typeof withdrawAmount.value === 'number' ? withdrawAmount.value : 0
  return amount >= 10 && amount <= availableAmount.value
})

const goBack = () => {
  router.back()
}

const onWithdrawAmountChange = () => {
  if (typeof withdrawAmount.value === 'string') {
    withdrawAmount.value = parseFloat(withdrawAmount.value) || ''
  }
}

const withdrawAll = () => {
  withdrawAmount.value = availableAmount.value
}

const submitWithdraw = async () => {
  if (!canWithdraw.value) return

  isSubmitting.value = true
  try {
    const amount = withdrawAmount.value as number
    await applyWithdrawal({ amount })
    showSuccessToast('提现申请已提交，预计1-3个工作日到账')
    showWithdrawPopup.value = false
    withdrawAmount.value = ''
    loadEarnings()
    loadTransactions()
  } catch (error) {
    showToast((error as Error)?.message || '提现失败，请重试')
  } finally {
    isSubmitting.value = false
  }
}

onMounted(() => {
  loadEarnings()
  loadTransactions()
})
</script>

<style scoped>
.earnings-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-top: 46px;
}

.content {
  padding: 10px;
}

.earnings-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 25px 20px;
  color: white;
  margin-bottom: 15px;
}

.earnings-header {
  text-align: center;
  margin-bottom: 25px;
}

.earnings-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 10px;
}

.earnings-amount {
  display: flex;
  align-items: flex-start;
  justify-content: center;
}

.earnings-amount .currency {
  font-size: 24px;
  font-weight: 600;
  margin-top: 5px;
}

.earnings-amount .amount {
  font-size: 42px;
  font-weight: 700;
  line-height: 1;
}

.earnings-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 25px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
}

.stat-label {
  font-size: 12px;
  opacity: 0.8;
  margin-top: 4px;
}

.stat-divider {
  width: 1px;
  background: rgba(255, 255, 255, 0.3);
}

.withdraw-section {
  padding: 0 20px;
}

.withdraw-section .van-button {
  height: 44px;
  font-size: 16px;
  font-weight: 600;
  background: white;
  color: #667eea;
  border: none;
}

.section-card {
  background: white;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 15px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.section-header .van-icon {
  color: #667eea;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.stats-item {
  text-align: center;
}

.stats-value {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.stats-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.transaction-tabs {
  display: flex;
  background: #f5f5f5;
  border-radius: 8px;
  padding: 4px;
  margin-bottom: 15px;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 8px;
  font-size: 13px;
  color: #666;
  border-radius: 6px;
  transition: all 0.3s;
}

.tab-item.active {
  background: white;
  color: #667eea;
  font-weight: 600;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.transaction-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.transaction-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.transaction-item:last-child {
  border-bottom: none;
}

.transaction-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.transaction-icon.income {
  background: #e6f7ff;
  color: #1890ff;
}

.transaction-icon.withdraw {
  background: #fff7e6;
  color: #ff976a;
}

.transaction-info {
  flex: 1;
}

.transaction-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.transaction-date {
  font-size: 12px;
  color: #999;
}

.transaction-amount {
  font-size: 16px;
  font-weight: 600;
}

.transaction-amount.income {
  color: #52c41a;
}

.transaction-amount.withdraw {
  color: #ff976a;
}

.empty-state {
  text-align: center;
  padding: 30px;
  color: #999;
}

.empty-state p {
  margin-top: 10px;
}

.withdraw-popup {
  padding: 20px;
  padding-bottom: 30px;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  font-size: 17px;
  font-weight: 600;
}

.popup-header .van-icon {
  color: #999;
  font-size: 20px;
}

.withdraw-amount-section {
  margin-bottom: 20px;
}

.withdraw-label {
  font-size: 14px;
  color: #333;
  margin-bottom: 10px;
}

.withdraw-input {
  display: flex;
  align-items: center;
  border-bottom: 2px solid #667eea;
  padding: 10px 0;
  margin-bottom: 10px;
}

.withdraw-input .currency {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.withdraw-input input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 28px;
  font-weight: 600;
  background: transparent;
}

.withdraw-balance {
  font-size: 13px;
  color: #999;
  display: flex;
  justify-content: space-between;
}

.withdraw-balance span {
  color: #667eea;
}

.withdraw-method-section {
  margin-bottom: 25px;
}

.method-label {
  font-size: 14px;
  color: #333;
  margin-bottom: 12px;
}

.method-options {
  display: flex;
  gap: 12px;
}

.method-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 15px;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  transition: all 0.3s;
}

.method-item.active {
  border-color: #667eea;
  background: #f0f4ff;
}

.method-item .van-icon {
  font-size: 28px;
}

.method-item span {
  font-size: 13px;
  color: #333;
}

.withdraw-submit {
  padding: 0 10px;
}

.withdraw-submit .van-button {
  height: 44px;
  font-size: 16px;
  font-weight: 600;
}
</style>
