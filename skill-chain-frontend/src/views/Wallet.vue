<template>
  <div class="wallet-container">
    <van-nav-bar title="我的钱包" left-arrow @click-left="goBack" fixed />

    <!-- 余额卡片 -->
    <div class="balance-card">
      <div class="balance-item">
        <p>CNY挂钩币</p>
        <h2>¥{{ wallet.cnyBalance }}</h2>
      </div>
      <div class="balance-item">
        <p>积分</p>
        <h2>{{ wallet.points }}</h2>
      </div>
    </div>

    <!-- 操作入口 -->
    <div class="actions">
      <van-grid :column-num="4" :border="false">
        <van-grid-item icon="gold-coin-o" text="充值" @click="openRecharge" />
        <van-grid-item icon="balance-pay" text="提现" @click="openWithdraw" />
        <van-grid-item icon="records" text="明细" @click="activeTab = 'tx'" />
        <van-grid-item icon="cash-back-record" text="提现记录" @click="activeTab = 'withdrawal'" />
      </van-grid>
    </div>

    <!-- Tab 切换：交易明细 / 提现记录 -->
    <div class="records-section">
      <van-tabs v-model:active="activeTab" animated>
        <!-- 交易明细 -->
        <van-tab title="交易明细" name="tx">
          <van-list
            v-model:loading="txLoading"
            :finished="txFinished"
            finished-text="没有更多了"
            @load="loadTransactions"
          >
            <van-cell v-for="record in records" :key="record.id">
              <template #title>
                <div class="record-title">
                  <div class="record-left">
                    <span class="record-name">{{ record.title }}</span>
                    <span class="record-desc">{{ record.desc }}</span>
                  </div>
                  <span :class="record.type === 'income' ? 'income' : 'expense'">
                    {{ record.type === 'income' ? '+' : '-' }}¥{{ record.amount }}
                  </span>
                </div>
              </template>
              <template #label>
                <span>{{ formatTime(record.time) }}</span>
              </template>
            </van-cell>
            <van-empty v-if="!txLoading && records.length === 0" description="暂无交易记录" />
          </van-list>
        </van-tab>

        <!-- 提现记录 -->
        <van-tab title="提现记录" name="withdrawal">
          <van-list
            v-model:loading="wdLoading"
            :finished="wdFinished"
            finished-text="没有更多了"
            @load="loadWithdrawals"
          >
            <van-cell v-for="wd in withdrawals" :key="wd.id">
              <template #title>
                <div class="record-title">
                  <div class="record-left">
                    <span class="record-name">提现 {{ wd.bankName }}</span>
                    <van-tag :type="wdStatusType(wd.status)" size="small" style="margin-left:6px">
                      {{ wdStatusText(wd.status) }}
                    </van-tag>
                  </div>
                  <span class="expense">-¥{{ Number(wd.amount).toFixed(2) }}</span>
                </div>
              </template>
              <template #label>
                <div class="wd-detail">
                  <span>实到 ¥{{ Number(wd.actualAmount).toFixed(2) }}（手续费 ¥{{ Number(wd.fee).toFixed(2) }}）</span>
                  <span>{{ formatTime(wd.createTime) }}</span>
                </div>
              </template>
            </van-cell>
            <van-empty v-if="!wdLoading && withdrawals.length === 0" description="暂无提现记录" />
          </van-list>
        </van-tab>
      </van-tabs>
    </div>

    <!-- 充值弹窗 -->
    <van-popup v-model:show="showRecharge" position="bottom" round style="padding: 20px 16px 32px">
      <div class="popup-title">选择充值金额</div>
      <div class="amount-grid">
        <div
          v-for="opt in rechargeOptions"
          :key="opt"
          class="amount-item"
          :class="{ selected: selectedAmount === opt }"
          @click="selectedAmount = opt"
        >
          ¥{{ opt }}
        </div>
      </div>
      <van-button
        type="primary"
        block
        :loading="rechargeLoading"
        style="margin-top:20px"
        @click="confirmRecharge"
      >
        确认充值 ¥{{ selectedAmount }}
      </van-button>
    </van-popup>

    <!-- 提现弹窗 -->
    <van-popup v-model:show="showWithdraw" position="bottom" round style="padding: 20px 16px 32px">
      <div class="popup-title">申请提现</div>
      <van-form @submit="confirmWithdraw">
        <van-field
          v-model="withdrawForm.amount"
          name="amount"
          label="提现金额"
          type="number"
          placeholder="最低10元，5%手续费"
          :rules="[{ required: true, message: '请输入金额' }]"
        />
        <van-field
          v-model="withdrawForm.bankName"
          name="bankName"
          label="银行名称"
          placeholder="如：招商银行"
          :rules="[{ required: true, message: '请输入银行名称' }]"
        />
        <van-field
          v-model="withdrawForm.bankCard"
          name="bankCard"
          label="银行卡号"
          placeholder="请输入收款卡号"
          :rules="[{ required: true, message: '请输入银行卡号' }]"
        />
        <van-button
          type="primary"
          block
          native-type="submit"
          :loading="withdrawLoading"
          style="margin-top:16px"
        >
          提交申请
        </van-button>
      </van-form>
      <p class="withdraw-tip">* 当前余额 ¥{{ wallet.cnyBalance }}，提现后余额实时扣减，审核通过后到账</p>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import {
  getWallet,
  getTransactionList,
  recharge as rechargeApi,
  applyWithdrawal,
  getWithdrawalList,
  type WithdrawalRecord
} from '@/api/wallet'

const router = useRouter()

const wallet = ref({ cnyBalance: '0.00', points: 0 })

const typeNames: Record<number, string> = {
  1: '充值',
  2: '消费',
  3: '收入',
  4: '提现',
  5: '退款'
}

// Tab
const activeTab = ref<'tx' | 'withdrawal'>('tx')

// 交易明细
interface TxRecord { id: number; title: string; desc: string; amount: string; type: string; time: string }
const records = ref<TxRecord[]>([])
const txLoading = ref(false)
const txFinished = ref(false)
let txPage = 1

// 提现记录
const withdrawals = ref<WithdrawalRecord[]>([])
const wdLoading = ref(false)
const wdFinished = ref(false)
let wdPage = 1

// 充值
const showRecharge = ref(false)
const rechargeOptions = [10, 50, 100, 200]
const selectedAmount = ref(50)
const rechargeLoading = ref(false)

// 提现
const showWithdraw = ref(false)
const withdrawLoading = ref(false)
const withdrawForm = ref({ amount: '', bankName: '', bankCard: '' })

const goBack = () => router.back()

const loadWallet = async () => {
  try {
    const data = await getWallet() as { cnyCoinBalance?: number; pointBalance?: number }
    wallet.value = {
      cnyBalance: (data?.cnyCoinBalance ?? 0).toFixed(2),
      points: Number(data?.pointBalance ?? 0)
    }
  } catch { /* 401 will redirect */ }
}

const loadTransactions = async () => {
  if (txLoading.value || txFinished.value) return
  txLoading.value = true
  try {
    const data = await getTransactionList({ page: txPage, size: 20 }) as any
    const list = data?.records ?? []
    const mapped: TxRecord[] = list.map((r: any) => ({
      id: r.logId ?? 0,
      title: typeNames[r.type ?? 0] ?? '交易',
      desc: r.description ?? '',
      amount: Math.abs(Number(r.amount ?? 0)).toFixed(2),
      type: [1, 3, 5].includes(r.type) ? 'income' : 'expense',
      time: r.createTime ?? ''
    }))
    records.value.push(...mapped)
    txPage++
    if (list.length < 20) txFinished.value = true
  } catch {
    txFinished.value = true
  } finally {
    txLoading.value = false
  }
}

const loadWithdrawals = async () => {
  if (wdLoading.value || wdFinished.value) return
  wdLoading.value = true
  try {
    const data = await getWithdrawalList({ page: wdPage, size: 20 }) as any
    const list = data?.records ?? []
    withdrawals.value.push(...list)
    wdPage++
    if (list.length < 20) wdFinished.value = true
  } catch {
    wdFinished.value = true
  } finally {
    wdLoading.value = false
  }
}

const openRecharge = () => {
  selectedAmount.value = 50
  showRecharge.value = true
}

const confirmRecharge = async () => {
  rechargeLoading.value = true
  try {
    await rechargeApi(selectedAmount.value)
    showToast('充值成功')
    showRecharge.value = false
    // 刷新余额和流水
    await loadWallet()
    records.value = []
    txPage = 1
    txFinished.value = false
    await loadTransactions()
  } catch { /* interceptor shows toast */ } finally {
    rechargeLoading.value = false
  }
}

const openWithdraw = () => {
  withdrawForm.value = { amount: '', bankName: '', bankCard: '' }
  showWithdraw.value = true
}

const confirmWithdraw = async () => {
  const amt = Number(withdrawForm.value.amount)
  if (amt < 10) { showToast('最低提现金额为10元'); return }
  withdrawLoading.value = true
  try {
    await applyWithdrawal({
      amount: amt,
      bankName: withdrawForm.value.bankName,
      bankCard: withdrawForm.value.bankCard
    })
    showToast('提现申请已提交，审核通过后到账')
    showWithdraw.value = false
    await loadWallet()
    // 刷新提现记录
    withdrawals.value = []
    wdPage = 1
    wdFinished.value = false
    activeTab.value = 'withdrawal'
    await loadWithdrawals()
  } catch { /* interceptor shows toast */ } finally {
    withdrawLoading.value = false
  }
}

const wdStatusText = (status?: number) => {
  const map: Record<number, string> = { 0: '审核中', 1: '已通过', 2: '已拒绝' }
  return map[status ?? 0] ?? '未知'
}

const wdStatusType = (status?: number): 'warning' | 'success' | 'danger' | 'primary' => {
  const map: Record<number, 'warning' | 'success' | 'danger'> = {
    0: 'warning', 1: 'success', 2: 'danger'
  }
  return map[status ?? 0] ?? 'primary'
}

const formatTime = (t?: string) => {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}

onMounted(async () => {
  await loadWallet()
  await loadTransactions()
})
</script>

<style scoped>
.wallet-container {
  padding-top: 46px;
  min-height: 100vh;
  background: #f5f5f5;
}

.balance-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px 20px;
  display: flex;
  justify-content: space-around;
  margin: 10px;
  border-radius: 8px;
}

.balance-item p {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 10px;
}

.balance-item h2 {
  font-size: 28px;
  font-weight: bold;
}

.actions {
  background: white;
  margin: 10px;
  border-radius: 8px;
  padding: 8px 0;
}

.records-section {
  background: white;
  margin: 10px;
  border-radius: 8px;
  overflow: hidden;
}

.record-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.record-left {
  display: flex;
  flex-direction: column;
}

.record-name {
  font-size: 14px;
  color: #323233;
}

.record-desc {
  font-size: 12px;
  color: #969799;
  margin-top: 2px;
}

.income {
  color: #07c160;
  font-weight: bold;
  font-size: 15px;
}

.expense {
  color: #ee0a24;
  font-weight: bold;
  font-size: 15px;
}

.wd-detail {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #969799;
}

/* 充值弹窗 */
.popup-title {
  font-size: 16px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 16px;
  color: #323233;
}

.amount-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.amount-item {
  border: 1px solid #ebedf0;
  border-radius: 8px;
  padding: 12px 0;
  text-align: center;
  font-size: 15px;
  color: #323233;
  cursor: pointer;
  transition: all 0.2s;
}

.amount-item.selected {
  border-color: #1989fa;
  background: #ecf5ff;
  color: #1989fa;
  font-weight: bold;
}

.withdraw-tip {
  font-size: 12px;
  color: #969799;
  margin-top: 12px;
  text-align: center;
}
</style>
