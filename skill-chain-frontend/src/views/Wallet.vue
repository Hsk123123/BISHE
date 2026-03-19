<template>
  <div class="wallet-container">
    <van-nav-bar title="我的钱包" left-arrow @click-left="goBack" fixed />
    
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

    <div class="actions">
      <van-grid :column-num="4" :border="false">
        <van-grid-item icon="gold-coin-o" text="充值" @click="recharge" />
        <van-grid-item icon="balance-pay" text="提现" @click="withdraw" />
        <van-grid-item icon="records" text="明细" @click="viewRecords" />
        <van-grid-item icon="shop-o" text="商城" @click="goToShop" />
      </van-grid>
    </div>

    <div class="transaction-records">
      <h3>交易明细</h3>
      <van-cell-group>
        <van-cell v-for="record in records" :key="record.id">
          <template #title>
            <div class="record-title">
              <span>{{ record.title }}</span>
              <span :class="{ income: record.type === 'income', expense: record.type === 'expense' }">
                {{ record.type === 'income' ? '+' : '-' }}{{ record.amount }}
              </span>
            </div>
          </template>
          <template #label>
            <p>{{ record.time }}</p>
          </template>
        </van-cell>
      </van-cell-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getWallet, getTransactionList } from '@/api/wallet'

const router = useRouter()

const wallet = ref({
  cnyBalance: '0.00',
  points: 0
})

const typeNames: Record<number, string> = {
  1: '充值',
  2: '消费',
  3: '收入',
  4: '提现',
  5: '退款'
}

const records = ref<{ id: number; title: string; amount: string; type: string; time: string }[]>([])
const loading = ref(true)

const goBack = () => {
  router.back()
}

const loadWallet = async () => {
  try {
    const data = await getWallet() as { cnyCoinBalance?: number; pointBalance?: number }
    wallet.value = {
      cnyBalance: (data?.cnyCoinBalance ?? 0).toFixed(2),
      points: Number(data?.pointBalance ?? 0)
    }
  } catch {
    // 401 will redirect to login
  } finally {
    loading.value = false
  }
}

const loadTransactions = async () => {
  try {
    const data = await getTransactionList({ page: 1, size: 20 }) as { records?: Array<{ logId?: number; type?: number; amount?: number; description?: string; createTime?: string }> }
    const list = data?.records ?? []
    records.value = list.map((r: any) => ({
      id: r.logId ?? 0,
      title: typeNames[r.type ?? 0] ?? r.description ?? '交易',
      amount: String(Math.abs(Number(r.amount ?? 0))),
      type: r.type === 1 || r.type === 3 || r.type === 5 ? 'income' : 'expense',
      time: r.createTime ?? ''
    }))
  } catch {
    records.value = []
  }
}

const recharge = () => {
  showToast('充值功能开发中')
}

const withdraw = () => {
  showToast('提现功能开发中')
}

const viewRecords = () => {
  loadTransactions()
}

const goToShop = () => {
  showToast('积分商城开发中')
}

onMounted(() => {
  loadWallet().then(() => loadTransactions())
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
  padding: 15px;
  border-radius: 8px;
}

.transaction-records {
  background: white;
  margin: 10px;
  padding: 15px;
  border-radius: 8px;
}

.transaction-records h3 {
  margin-bottom: 15px;
  font-size: 16px;
}

.record-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.income {
  color: #07c160;
}

.expense {
  color: #ee0a24;
}
</style>