<template>
  <div class="stats-container">
    <van-nav-bar title="数据统计" left-arrow @click-left="router.back()" fixed />

    <div class="page-body">
      <div v-if="loading" class="loading-wrap">
        <van-loading size="24" vertical>加载中...</van-loading>
      </div>

      <template v-else>
        <div class="stat-grid">
          <div class="stat-card primary">
            <div class="stat-value">¥{{ stats.availableAmount.toFixed(2) }}</div>
            <div class="stat-label">可提现收益</div>
          </div>
          <div class="stat-card orange">
            <div class="stat-value">{{ stats.completedOrders }}</div>
            <div class="stat-label">已完成订单</div>
          </div>
          <div class="stat-card blue">
            <div class="stat-value">{{ stats.skillCount }}</div>
            <div class="stat-label">发布技能数</div>
          </div>
          <div class="stat-card green">
            <div class="stat-value">{{ stats.avgRating > 0 ? stats.avgRating.toFixed(1) : '-' }}</div>
            <div class="stat-label">平均评分</div>
          </div>
        </div>

        <div class="section-card">
          <div class="section-title">我的技能</div>
          <div v-if="skills.length === 0" class="empty-inline">暂无技能</div>
          <div v-else class="skill-list">
            <div v-for="skill in skills" :key="skill.skillId" class="skill-row">
              <div class="skill-info">
                <span class="skill-title">{{ skill.title }}</span>
                <span class="skill-price">¥{{ skill.pricePerUnit }}</span>
              </div>
              <div class="skill-meta">
                <van-tag :type="skillStatusType(skill.status)" size="small">
                  {{ skillStatusText(skill.status) }}
                </van-tag>
                <span class="order-count">{{ skill.orderCount ?? 0 }}单</span>
                <span class="rating" v-if="skill.avgRating">
                  ⭐ {{ Number(skill.avgRating).toFixed(1) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { useUserStore } from '@/store/user'
import { getMySkills, type Skill } from '@/api/skill'
import { getEarningsStats } from '@/api/withdrawal'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const skills = ref<Skill[]>([])

const stats = ref({
  availableAmount: 0,
  completedOrders: 0,
  skillCount: 0,
  avgRating: 0
})

const load = async () => {
  loading.value = true
  try {
    const userId = (userStore.userInfo as any)?.userId
    const [earningsRes, skillsRes] = await Promise.all([
      getEarningsStats(),
      userId ? getMySkills(userId) : Promise.resolve([])
    ])

    const e = earningsRes as any
    stats.value.availableAmount = Number(e?.availableAmount ?? 0)
    stats.value.completedOrders = Number(e?.completedOrders ?? 0)

    const list = (skillsRes as Skill[]) ?? []
    skills.value = list
    stats.value.skillCount = list.length

    const rated = list.filter(s => s.avgRating && Number(s.avgRating) > 0)
    if (rated.length > 0) {
      const sum = rated.reduce((acc, s) => acc + Number(s.avgRating ?? 0), 0)
      stats.value.avgRating = sum / rated.length
    }
  } catch {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

const skillStatusText = (status?: number) => {
  if (status === 1) return '上架'
  if (status === 2) return '下架'
  return '待审核'
}

const skillStatusType = (status?: number) => {
  if (status === 1) return 'success'
  if (status === 2) return 'default'
  return 'warning'
}

onMounted(load)
</script>

<style scoped>
.stats-container {
  min-height: 100vh;
  padding-top: 46px;
  background: #f6f8fc;
}

.page-body {
  padding: 14px;
}

.loading-wrap {
  display: flex;
  justify-content: center;
  padding: 40px 0;
}

.stat-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 14px;
}

.stat-card {
  padding: 20px 16px;
  border-radius: 18px;
  text-align: center;
  box-shadow: 0 4px 14px rgba(31, 45, 61, 0.06);
}

.stat-card.primary {
  background: linear-gradient(135deg, #667eea 0%, #7b61ff 100%);
  color: #fff;
}

.stat-card.orange {
  background: linear-gradient(135deg, #ff976a 0%, #ff6b35 100%);
  color: #fff;
}

.stat-card.blue {
  background: linear-gradient(135deg, #1989fa 0%, #1677e5 100%);
  color: #fff;
}

.stat-card.green {
  background: linear-gradient(135deg, #07c160 0%, #05a14f 100%);
  color: #fff;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  line-height: 1.2;
  margin-bottom: 6px;
}

.stat-label {
  font-size: 12px;
  opacity: 0.88;
}

.section-card {
  background: #fff;
  border-radius: 18px;
  padding: 16px;
  box-shadow: 0 4px 14px rgba(31, 45, 61, 0.06);
}

.section-title {
  font-size: 16px;
  font-weight: 700;
  color: #1f2330;
  margin-bottom: 14px;
}

.empty-inline {
  font-size: 13px;
  color: #98a0b3;
  text-align: center;
  padding: 16px 0;
}

.skill-list {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.skill-row {
  padding: 12px 0;
  border-bottom: 1px solid #f2f4f8;
}

.skill-row:last-child {
  border-bottom: none;
}

.skill-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.skill-title {
  font-size: 14px;
  font-weight: 600;
  color: #1f2330;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 8px;
}

.skill-price {
  font-size: 14px;
  font-weight: 700;
  color: #ff976a;
  flex-shrink: 0;
}

.skill-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.order-count {
  font-size: 12px;
  color: #7a8193;
}

.rating {
  font-size: 12px;
  color: #ff976a;
}
</style>
