<template>
  <div class="my-skills-container">
    <van-nav-bar
      title="我的技能"
      left-arrow
      fixed
      @click-left="goBack"
    />

    <section class="skills-hero">
      <div class="hero-content">
        <div class="hero-badge">My Skills</div>
        <h1>技能管理</h1>
        <p>查看已发布、审核中和已下架的技能内容，统一管理你的服务项目</p>
      </div>
      <div class="hero-glow hero-glow-1"></div>
      <div class="hero-glow hero-glow-2"></div>
    </section>

    <section class="content-section">
      <div class="tabs-card">
        <div class="tabs">
          <div
            :class="['tab-item', { active: activeTab === 0 }]"
            @click="activeTab = 0"
          >
            <span class="tab-title">已发布</span>
            <span class="tab-count">{{ publishedCount }}</span>
          </div>
          <div
            :class="['tab-item', { active: activeTab === 1 }]"
            @click="activeTab = 1"
          >
            <span class="tab-title">审核中</span>
            <span class="tab-count">{{ pendingCount }}</span>
          </div>
          <div
            :class="['tab-item', { active: activeTab === 2 }]"
            @click="activeTab = 2"
          >
            <span class="tab-title">已下架</span>
            <span class="tab-count">{{ offlineCount }}</span>
          </div>
        </div>
      </div>

      <div class="skills-list">
        <div v-if="skillLoading" class="state-card">
          <van-loading size="24px" vertical>加载中...</van-loading>
        </div>

        <template v-else-if="filteredSkills.length > 0">
          <div
            v-for="skill in filteredSkills"
            :key="skill.id"
            class="skill-card"
          >
            <div class="skill-header">
              <van-image :src="skill.image" class="skill-image" fit="cover" />
              <div class="skill-info">
                <div class="title-row">
                  <h3>{{ skill.title }}</h3>
                  <div :class="['status-tag', getStatusClass(skill.status)]">
                    {{ getStatusText(skill.status) }}
                  </div>
                </div>

                <p class="category">{{ skill.category }}</p>
                <p class="price">¥{{ skill.price }} <small>/ {{ skill.unit }}</small></p>
              </div>
            </div>

            <div class="skill-stats">
              <div class="stat">
                <span class="num">{{ skill.views }}</span>
                <span class="label">浏览</span>
              </div>
              <div class="stat">
                <span class="num">{{ skill.orders }}</span>
                <span class="label">订单</span>
              </div>
              <div class="stat">
                <span class="num">{{ skill.ratingText }}</span>
                <span class="label">评分</span>
              </div>
            </div>

            <div class="skill-actions">
              <van-button
                v-if="skill.status === 1"
                type="primary"
                size="small"
                round
                plain
                @click="editSkill(skill)"
              >
                编辑
              </van-button>

              <van-button
                v-if="skill.status === 1"
                type="warning"
                size="small"
                round
                plain
                @click="toggleOffline(skill)"
              >
                下架
              </van-button>

              <van-button
                v-if="skill.status === 2"
                type="success"
                size="small"
                round
                plain
                @click="republish(skill)"
              >
                重新上架
              </van-button>
            </div>
          </div>
        </template>

        <div v-else class="empty-state">
          <div class="empty-icon-wrap">
            <van-icon name="service-o" size="36" />
          </div>
          <p>暂无相关技能</p>
        </div>
      </div>
    </section>

    <div class="fab" @click="goToPublish">
      <van-icon name="plus" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getMySkills, type Skill as ApiSkill } from '@/api/skill'
import { useUserStore } from '@/store/user'

interface SkillCardItem {
  id: number
  title: string
  category: string
  price: string
  unit: string
  image: string
  status: number
  views: number
  orders: number
  rating: number | null
  ratingText: string
}

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref(0)
const skillLoading = ref(false)

const skills = ref<SkillCardItem[]>([])

const defaultImage = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'

const unitMap: Record<number, string> = {
  1: '小时',
  2: '次',
  3: '单',
  4: '月'
}

const categoryMap: Record<number, string> = {
  1: '家政服务',
  2: '技能陪练',
  3: '设计服务',
  4: '咨询服务',
  5: '教育培训',
  6: '更多服务'
}

const loadSkills = async () => {
  const userId = userStore.userInfo?.userId
  if (!userId) {
    showToast('请先登录')
    return
  }

  skillLoading.value = true
  try {
    const data = await getMySkills(userId)

    skills.value = (data ?? []).map((r: ApiSkill) => {
      const ratingValue =
        r.avgRating !== undefined && r.avgRating !== null
          ? Number(r.avgRating)
          : r.rating !== undefined && r.rating !== null
            ? Number(r.rating)
            : null

      return {
        id: r.skillId ?? r.id ?? 0,
        title: r.title ?? '技能服务',
        category: categoryMap[r.categoryId ?? 0] ?? '其他',
        price: String(r.pricePerUnit ?? r.price ?? 0),
        unit: r.unit ?? unitMap[r.unitType ?? 0] ?? '次',
        image:
          (typeof r.mediaUrls === 'string' && r.mediaUrls
            ? r.mediaUrls.split(',')[0]
            : '') || defaultImage,
        status: r.status ?? 0,
        views: Number(r.orderCount ?? 0),
        orders: Number(r.orderCount ?? 0),
        rating: ratingValue,
        ratingText: ratingValue !== null ? ratingValue.toFixed(1) : '暂无'
      }
    })
  } catch (err) {
    console.error('[my-skills] loadSkills error =', err)
    skills.value = []
    showToast('加载技能失败')
  } finally {
    skillLoading.value = false
  }
}

onMounted(() => {
  loadSkills()
})

const publishedCount = computed(() => skills.value.filter((s) => s.status === 1).length)
const pendingCount = computed(() => skills.value.filter((s) => s.status === 0).length)
const offlineCount = computed(() => skills.value.filter((s) => s.status === 2).length)

const filteredSkills = computed(() => {
  const statusMap: Record<number, number> = {
    0: 1,
    1: 0,
    2: 2
  }
  return skills.value.filter((skill) => skill.status === statusMap[activeTab.value])
})

const goBack = () => {
  router.back()
}

const goToPublish = () => {
  router.push('/publish-skill')
}

const getStatusClass = (status: number) => {
  const classes: Record<number, string> = {
    0: 'pending',
    1: 'published',
    2: 'offline'
  }
  return classes[status] || ''
}

const getStatusText = (status: number) => {
  const texts: Record<number, string> = {
    0: '审核中',
    1: '已发布',
    2: '已下架'
  }
  return texts[status] || ''
}

const editSkill = (skill: SkillCardItem) => {
  showToast(`编辑功能开发中：${skill.title}`)
}

const toggleOffline = (skill: SkillCardItem) => {
  skill.status = skill.status === 1 ? 2 : 1
  showToast(skill.status === 2 ? '已下架' : '已上架')
}

const republish = (skill: SkillCardItem) => {
  skill.status = 0
  showToast('已重新提交审核')
}
</script>

<style scoped>
.my-skills-container {
  min-height: 100vh;
  padding-top: 46px;
  padding-bottom: 100px;
  background:
    radial-gradient(circle at top right, rgba(102, 126, 234, 0.12), transparent 28%),
    linear-gradient(180deg, #f6f8fc 0%, #eef2f7 100%);
}

.skills-hero {
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

.skills-hero h1 {
  margin: 0 0 8px;
  font-size: 25px;
  font-weight: 700;
  line-height: 1.3;
}

.skills-hero p {
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

.tabs-card {
  margin-bottom: 14px;
  padding: 6px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 10px 24px rgba(31, 45, 61, 0.05);
}

.tabs {
  display: flex;
  gap: 6px;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 12px 8px;
  border-radius: 16px;
  color: #7c8498;
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

.tab-item.active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.12), rgba(123, 97, 255, 0.12));
  color: #667eea;
}

.skills-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skill-card {
  padding: 14px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 10px 24px rgba(31, 45, 61, 0.05);
}

.skill-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 14px;
}

.skill-image {
  width: 78px;
  height: 78px;
  border-radius: 14px;
  overflow: hidden;
  flex-shrink: 0;
}

.skill-info {
  flex: 1;
  min-width: 0;
}

.title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 6px;
}

.skill-info h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  line-height: 1.4;
  color: #1f2330;
}

.skill-info .category {
  margin: 0 0 6px;
  font-size: 12px;
  color: #8a90a2;
}

.skill-info .price {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #ff976a;
}

.skill-info .price small {
  font-size: 12px;
  font-weight: 400;
  color: #9aa1b3;
}

.status-tag {
  flex-shrink: 0;
  padding: 4px 10px;
  font-size: 11px;
  font-weight: 600;
  border-radius: 999px;
}

.status-tag.pending {
  background: #fff7e6;
  color: #ff976a;
}

.status-tag.published {
  background: #eaf4ff;
  color: #1989fa;
}

.status-tag.offline {
  background: #f1f3f7;
  color: #8a90a2;
}

.skill-stats {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  padding: 14px 12px;
  margin-bottom: 14px;
  border-radius: 16px;
  background: linear-gradient(180deg, #fafbff 0%, #f5f7fc 100%);
  border: 1px solid rgba(102, 126, 234, 0.08);
}

.stat {
  flex: 1;
  text-align: center;
}

.stat .num {
  display: block;
  margin-bottom: 4px;
  font-size: 18px;
  font-weight: 700;
  color: #2a3142;
}

.stat .label {
  font-size: 12px;
  color: #9aa1b3;
}

.skill-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.state-card {
  display: flex;
  justify-content: center;
  padding: 40px 0;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 10px 24px rgba(31, 45, 61, 0.05);
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

.fab {
  position: fixed;
  right: 18px;
  bottom: 84px;
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 58px;
  height: 58px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #7b61ff 100%);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.35);
}

.fab .van-icon {
  font-size: 28px;
  color: #fff;
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