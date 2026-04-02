<template>
  <div class="earnings-container">
    <van-nav-bar
      title="我的收益"
      left-arrow
      fixed
      @click-left="goBack"
    />

    <section class="earnings-hero">
      <div class="hero-content">
        <div class="hero-badge">My Earnings</div>
        <h1>收益中心</h1>
        <p>查看可提现余额、累计收益和提现记录，统一管理你的经营收入</p>
      </div>
      <div class="hero-glow hero-glow-1"></div>
      <div class="hero-glow hero-glow-2"></div>
    </section>

    <section class="content-section">
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
          <div class="section-title-wrap">
            <div class="section-icon-wrap icon-blue">
              <van-icon name="chart-trending-o" />
            </div>
            <div>
              <h3>收益统计</h3>
              <p>查看本月表现与核心经营数据</p>
            </div>
          </div>
        </div>

        <div class="stats-grid">
          <div class="stats-item">
            <div class="stats-value">{{ monthlyEarnings.toFixed(2) }}</div>
            <div class="stats-label">本月收益</div>
          </div>
          <div class="stats-item">
            <div class="stats-value">{{ monthlyOrders }}</div>
            <div class="stats-label">本月订单</div>
          </div>
          <div class="stats-item">
            <div class="stats-value">{{ avgOrderValue.toFixed(2) }}</div>
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
          <div class="section-title-wrap">
            <div class="section-icon-wrap icon-orange">
              <van-icon name="orders-o" />
            </div>
            <div>
              <h3>收益明细</h3>
              <p>收入与提现记录一目了然</p>
            </div>
          </div>
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
            <div class="empty-icon-wrap">
              <van-icon name="balance-o" size="36" />
            </div>
            <p>暂无收益记录</p>
          </div>
        </div>
      </div>
    </section>

    <van-popup v-model:show="showWithdrawPopup" position="bottom" round>
      <div class="withdraw-popup">
        <div class="popup-header">
          <div>
            <h2>申请提现</h2>
            <p>请输入提现金额并选择提现方式</p>
          </div>
          <van-icon name="cross" @click="showWithdrawPopup = false" />
        </div>

        <div class="withdraw-content">
          <div class="withdraw-section-card">
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
              <span>可用余额 ¥{{ availableAmount.toFixed(2) }}</span>
              <span class="all-btn" @click="withdrawAll">全部提现</span>
            </div>
          </div>

          <div class="withdraw-section-card">
            <div class="method-label">提现方式</div>
            <div class="method-options">
              <div
                :class="['method-item', { active: withdrawMethod === 0 }]"
                @click="withdrawMethod = 0"
              >
                <van-icon name="paid" color="#1677ff" />
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
const monthlyEarnings = ref(0)
const monthlyOrders = ref(0)
const avgOrderValue = ref(0)
const completedOrders = ref(0)

const transactions = ref<Transaction[]>([])

const loadEarnings = async () => {
  try {
    const stats = await getEarningsStats() as {
      availableAmount?: number
      frozenAmount?: number
      totalEarnings?: number
      monthlyEarnings?: number
      monthlyOrders?: number
      avgOrderValue?: number
      completedOrders?: number
    }
    availableAmount.value = Number(stats?.availableAmount ?? 0)
    frozenAmount.value = Number(stats?.frozenAmount ?? 0)
    totalEarnings.value = Number(stats?.totalEarnings ?? 0)
    monthlyEarnings.value = Number(stats?.monthlyEarnings ?? 0)
    monthlyOrders.value = Number(stats?.monthlyOrders ?? 0)
    avgOrderValue.value = Number(stats?.avgOrderValue ?? 0)
    completedOrders.value = Number(stats?.completedOrders ?? 0)
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
  padding-top: 46px;
  padding-bottom: 90px;
  background:
    radial-gradient(circle at top right, rgba(102, 126, 234, 0.12), transparent 28%),
    linear-gradient(180deg, #f6f8fc 0%, #eef2f7 100%);
}

.earnings-hero {
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

.earnings-hero h1 {
  margin: 0 0 8px;
  font-size: 25px;
  font-weight: 700;
  line-height: 1.3;
}

.earnings-hero p {
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

.content-section {
  padding: 0 12px;
}

.earnings-card {
  margin-bottom: 14px;
  padding: 22px 18px;
  border-radius: 22px;
  color: #fff;
  background: linear-gradient(135deg, #667eea 0%, #7b61ff 55%, #8f6bff 100%);
  box-shadow: 0 14px 28px rgba(102, 126, 234, 0.18);
}

.earnings-header {
  text-align: center;
  margin-bottom: 24px;
}

.earnings-label {
  margin-bottom: 10px;
  font-size: 14px;
  opacity: 0.9;
}

.earnings-amount {
  display: flex;
  align-items: flex-start;
  justify-content: center;
}

.earnings-amount .currency {
  margin-top: 6px;
  font-size: 24px;
  font-weight: 600;
}

.earnings-amount .amount {
  font-size: 42px;
  font-weight: 700;
  line-height: 1;
}

.earnings-stats {
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin-bottom: 22px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 18px;
  font-weight: 700;
}

.stat-label {
  margin-top: 4px;
  font-size: 12px;
  opacity: 0.82;
}

.stat-divider {
  width: 1px;
  height: 34px;
  background: rgba(255, 255, 255, 0.28);
}

.withdraw-section {
  padding: 0 12px;
}

.withdraw-section .van-button {
  height: 44px;
  font-size: 16px;
  font-weight: 700;
  color: #667eea;
  background: #fff;
  border: none;
}

.section-card {
  margin-bottom: 14px;
  padding: 16px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 10px 24px rgba(31, 45, 61, 0.05);
}

.section-header {
  margin-bottom: 14px;
}

.section-title-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-title-wrap h3 {
  margin: 0 0 4px;
  font-size: 18px;
  font-weight: 700;
  color: #1f2330;
}

.section-title-wrap p {
  margin: 0;
  font-size: 12px;
  color: #8a90a2;
  line-height: 1.5;
}

.section-icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 14px;
  flex-shrink: 0;
}

.section-icon-wrap .van-icon {
  font-size: 20px;
}

.icon-blue {
  background: rgba(102, 126, 234, 0.12);
  color: #667eea;
}

.icon-orange {
  background: rgba(255, 151, 106, 0.14);
  color: #ff976a;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.stats-item {
  padding: 16px 12px;
  text-align: center;
  border-radius: 16px;
  background: linear-gradient(180deg, #fafbff 0%, #f5f7fc 100%);
  border: 1px solid rgba(102, 126, 234, 0.08);
}

.stats-value {
  font-size: 20px;
  font-weight: 700;
  color: #2a3142;
}

.stats-label {
  margin-top: 4px;
  font-size: 12px;
  color: #9aa1b3;
}

.transaction-tabs {
  display: flex;
  gap: 6px;
  padding: 6px;
  margin-bottom: 14px;
  border-radius: 16px;
  background: #f4f6fb;
}

.tab-item {
  flex: 1;
  padding: 10px 8px;
  text-align: center;
  font-size: 13px;
  color: #7c8498;
  border-radius: 12px;
  transition: all 0.25s ease;
}

.tab-item.active {
  font-weight: 700;
  color: #667eea;
  background: #fff;
  box-shadow: 0 4px 10px rgba(31, 45, 61, 0.06);
}

.transaction-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.transaction-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #eef1f6;
}

.transaction-item:last-child {
  border-bottom: none;
}

.transaction-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 14px;
  flex-shrink: 0;
}

.transaction-icon.income {
  background: #eaf9ef;
  color: #07c160;
}

.transaction-icon.withdraw {
  background: #fff4e8;
  color: #ff976a;
}

.transaction-info {
  flex: 1;
  min-width: 0;
}

.transaction-title {
  margin-bottom: 4px;
  font-size: 14px;
  font-weight: 600;
  color: #2a3142;
}

.transaction-date {
  font-size: 12px;
  color: #98a0b3;
}

.transaction-amount {
  font-size: 16px;
  font-weight: 700;
}

.transaction-amount.income {
  color: #07c160;
}

.transaction-amount.withdraw {
  color: #ff976a;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 36px 20px;
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

.withdraw-popup {
  display: flex;
  flex-direction: column;
  max-height: 90vh;
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

.popup-header h2 {
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

.withdraw-content {
  padding: 14px 14px 22px;
}

.withdraw-section-card {
  padding: 14px;
  margin-bottom: 12px;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 8px 20px rgba(31, 45, 61, 0.04);
}

.withdraw-label,
.method-label {
  margin-bottom: 12px;
  font-size: 15px;
  font-weight: 700;
  color: #1f2330;
}

.withdraw-input {
  display: flex;
  align-items: center;
  padding: 10px 0 12px;
  border-bottom: 2px solid #e7ebf3;
}

.withdraw-input .currency {
  font-size: 24px;
  font-weight: 700;
  color: #2a3142;
}

.withdraw-input input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 28px;
  font-weight: 700;
  color: #2a3142;
  background: transparent;
}

.withdraw-balance {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 10px;
  font-size: 13px;
  color: #98a0b3;
}

.all-btn {
  color: #667eea;
  font-weight: 600;
}

.method-options {
  display: flex;
  gap: 10px;
}

.method-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 14px 10px;
  border: 1px solid #e8ecf3;
  border-radius: 14px;
  background: #fafbff;
  transition: all 0.25s ease;
}

.method-item.active {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.06);
}

.method-item .van-icon {
  font-size: 28px;
}

.method-item span {
  font-size: 13px;
  color: #2a3142;
}

.withdraw-submit {
  padding-top: 4px;
}

.withdraw-submit .van-button {
  height: 44px;
  font-size: 16px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #7b61ff 100%);
  border: none;
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