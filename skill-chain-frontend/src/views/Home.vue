<template>
  <div class="home-container">
    <van-nav-bar title="技能链" fixed />

    <NoticePopup ref="noticePopupRef" @view-detail="goToNotices" />

    <section class="hero-section">
      <div class="hero-content">
        <div class="hero-badge">Skill-Chain</div>
        <h1>发现你的技能伙伴</h1>
        <p>游戏化技能共享预约平台，找到合适的人，预约合适的服务</p>
      </div>
      <div class="hero-glow hero-glow-1"></div>
      <div class="hero-glow hero-glow-2"></div>
    </section>

    <div class="search-section">
      <div class="search-card">
        <van-search
          v-model="searchKeyword"
          placeholder="搜索技能、服务者"
          shape="round"
          @search="onSearch"
        />
      </div>
    </div>

    <section class="quick-access-section">
      <div class="quick-access">
        <div class="access-item" @click="goToBrowse()">
          <div class="access-icon-wrap icon-blue">
            <van-icon name="search" class="access-icon" />
          </div>
          <span>浏览全部</span>
        </div>

        <div class="access-item" @click="goToBrowse({ sort: 'sales' })">
          <div class="access-icon-wrap icon-orange">
            <van-icon name="fire" class="access-icon" />
          </div>
          <span>高销量</span>
        </div>

        <div class="access-item" @click="goToBrowse({ sort: 'rating' })">
          <div class="access-icon-wrap icon-green">
            <van-icon name="star-o" class="access-icon" />
          </div>
          <span>高评分</span>
        </div>

        <div class="access-item" @click="goToBrowse({ tab: 'new' })">
          <div class="access-icon-wrap icon-purple">
            <van-icon name="new-o" class="access-icon" />
          </div>
          <span>最新上架</span>
        </div>
      </div>
    </section>

    <section class="content-section">
      <div class="section-card">
        <div class="section-header">
          <div>
            <h3>热门技能</h3>
            <p>当前平台预约量更高的服务</p>
          </div>
          <span class="more" @click="goToBrowse({ sort: 'sales' })">查看更多</span>
        </div>

        <van-loading v-if="skillLoading" size="24px" vertical class="state-wrap">
          加载中...
        </van-loading>

        <div v-else-if="loadFailed" class="empty-tip">
          <p>技能数据加载失败</p>
          <van-button size="small" round plain type="primary" @click="loadSkills">
            重新加载
          </van-button>
        </div>

        <div v-else-if="featuredSkills.length === 0" class="empty-tip">暂无技能数据</div>

        <div v-else class="skills-list">
          <div
            v-for="skill in featuredSkills"
            :key="skill.id"
            class="skill-item"
            @click="goToSkill(skill.id)"
          >
            <div class="skill-main">
              <div class="skill-text">
                <div class="skill-top-row">
                  <h4>{{ skill.title }}</h4>
                  <span class="skill-tag">热门</span>
                </div>

                <p class="skill-desc">
                  {{ skill.description || '优质技能服务，支持在线预约。' }}
                </p>

                <div class="skill-meta-row">
                  <span class="meta-pill">{{ skill.orderCount }}人已预约</span>
                  <span class="meta-pill light">{{ skill.provider }}</span>
                </div>
              </div>

              <div class="skill-price-block">
                <span class="skill-price">¥{{ skill.price }}</span>
                <small>/{{ skill.unit }}</small>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="section-card">
        <div class="section-header">
          <div>
            <h3>精选技能</h3>
            <p>平台内容中值得关注的优质技能</p>
          </div>
          <span class="more" @click="goToBrowse({ sort: 'rating' })">查看更多</span>
        </div>

        <van-loading v-if="skillLoading" size="24px" vertical class="state-wrap">
          加载中...
        </van-loading>

        <div v-else-if="loadFailed" class="empty-tip">
          <p>技能数据加载失败</p>
          <van-button size="small" round plain type="primary" @click="loadSkills">
            重新加载
          </van-button>
        </div>

        <div v-else-if="curatedSkills.length === 0" class="empty-tip">暂无精选数据</div>

        <div v-else class="recommend-list">
          <div
            v-for="skill in curatedSkills"
            :key="skill.id"
            class="recommend-item"
            @click="goToSkill(skill.id)"
          >
            <div class="recommend-header">
              <div class="recommend-title-wrap">
                <div class="recommend-title">{{ skill.title }}</div>
                <div class="recommend-provider">{{ skill.provider }}</div>
              </div>

              <div class="recommend-price">
                ¥{{ skill.price }}<small>/{{ skill.unit }}</small>
              </div>
            </div>

            <div class="recommend-desc">
              {{ skill.description || '平台精选技能服务，支持快速预约。' }}
            </div>

            <div class="recommend-meta">
              <div class="recommend-rating">
                <template v-if="skill.rating !== null">
                  <van-rate
                    :model-value="skill.rating"
                    readonly
                    allow-half
                    size="12px"
                    color="#ff976a"
                  />
                  <span class="rating">{{ skill.rating.toFixed(1) }}分</span>
                </template>
                <template v-else>
                  <span class="no-rating">暂无评分</span>
                </template>
              </div>
              <span class="meta-pill light">{{ skill.orderCount }}人预约</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <van-tabbar route fixed>
      <van-tabbar-item icon="home-o" to="/home">首页</van-tabbar-item>
      <van-tabbar-item icon="search" to="/discover">发现</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/orders">订单</van-tabbar-item>
      <van-tabbar-item icon="user-o" to="/profile">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import NoticePopup from '@/components/NoticePopup.vue'
import { getSkillList } from '@/api/skill'

interface RawSkill {
  skillId?: number
  id?: number
  title?: string
  description?: string
  pricePerUnit?: number
  price?: number
  unitType?: number
  unit?: string
  mediaUrls?: string
  providerName?: string
  orderCount?: number
  avgRating?: number
  rating?: number
  reviewCount?: number
  serviceMode?: number
}

interface SkillCardItem {
  id: number
  title: string
  description: string
  provider: string
  price: number
  unit: string
  orderCount: number
  rating: number | null
  reviewCount: number
  image: string
  serviceMode: number
}

const router = useRouter()

const searchKeyword = ref('')
const noticePopupRef = ref()
const skillLoading = ref(false)
const loadFailed = ref(false)
const allSkills = ref<SkillCardItem[]>([])

const unitMap: Record<number, string> = {
  1: '小时',
  2: '次',
  3: '单',
  4: '月'
}

const defaultImage = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'

const normalizeSkill = (raw: RawSkill): SkillCardItem => {
  const image =
    typeof raw.mediaUrls === 'string' && raw.mediaUrls.trim()
      ? raw.mediaUrls.split(',')[0]
      : defaultImage

  const rating =
    raw.avgRating !== undefined && raw.avgRating !== null
      ? Number(raw.avgRating)
      : raw.rating !== undefined && raw.rating !== null
        ? Number(raw.rating)
        : null

  return {
    id: Number(raw.skillId ?? raw.id ?? 0),
    title: raw.title ?? '技能服务',
    description: raw.description ?? '',
    provider: raw.providerName ?? '服务者',
    price: Number(raw.pricePerUnit ?? raw.price ?? 0),
    unit: raw.unit ?? unitMap[Number(raw.unitType)] ?? '次',
    orderCount: Number(raw.orderCount ?? 0),
    rating,
    reviewCount: Number(raw.reviewCount ?? 0),
    image,
    serviceMode: Number(raw.serviceMode ?? 0)
  }
}

const featuredSkills = computed(() => {
  return [...allSkills.value]
    .sort((a, b) => {
      if (b.orderCount !== a.orderCount) return b.orderCount - a.orderCount
      return b.id - a.id
    })
    .slice(0, 4)
})

const curatedSkills = computed(() => {
  const featuredIds = new Set(featuredSkills.value.map((item) => item.id))

  const preferred = allSkills.value
    .filter((item) => !featuredIds.has(item.id))
    .sort((a, b) => {
      const ratingA = a.rating ?? -1
      const ratingB = b.rating ?? -1

      if (ratingB !== ratingA) return ratingB - ratingA
      if (b.orderCount !== a.orderCount) return b.orderCount - a.orderCount
      return b.id - a.id
    })

  const result = preferred.slice(0, 4)

  if (result.length < 4) {
    const fallback = allSkills.value
      .filter((item) => !result.some((r) => r.id === item.id))
      .slice(0, 4 - result.length)

    result.push(...fallback)
  }

  return result
})

const loadSkills = async () => {
  skillLoading.value = true
  loadFailed.value = false

  try {
    const data = (await getSkillList({ page: 1, size: 20 })) as { records?: RawSkill[] }
    const records = data?.records ?? []
    allSkills.value = records.map(normalizeSkill).filter((item) => item.id > 0)
  } catch {
    allSkills.value = []
    loadFailed.value = true
  } finally {
    skillLoading.value = false
  }
}

onMounted(async () => {
  await loadSkills()
  await nextTick()
  noticePopupRef.value?.show?.()
})

const goToNotices = () => {
  router.push('/notices')
}

const goToSkill = (id: number) => {
  router.push(`/skill/${id}`)
}

const goToBrowse = (query: Record<string, string> = {}) => {
  router.push({ path: '/browse', query })
}

const onSearch = () => {
  const keyword = searchKeyword.value.trim()
  router.push({
    path: '/browse',
    query: keyword ? { keyword } : {}
  })
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  padding-top: 46px;
  padding-bottom: 62px;
  background:
    radial-gradient(circle at top right, rgba(102, 126, 234, 0.12), transparent 28%),
    linear-gradient(180deg, #f6f8fc 0%, #eef2f7 100%);
}

.hero-section {
  position: relative;
  overflow: hidden;
  margin: 0 0 14px;
  padding: 28px 18px 44px;
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
  margin-bottom: 12px;
  padding: 4px 10px;
  font-size: 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(4px);
}

.hero-section h1 {
  margin: 0 0 10px;
  font-size: 26px;
  font-weight: 700;
  line-height: 1.3;
}

.hero-section p {
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

.search-section {
  position: relative;
  z-index: 10;
  margin: -24px 14px 14px;
}

.search-card {
  padding: 6px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 10px 30px rgba(31, 45, 61, 0.08);
}

.quick-access-section {
  padding: 0 14px;
  margin-bottom: 14px;
}

.quick-access {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  padding: 14px 12px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 10px 24px rgba(31, 45, 61, 0.05);
}

.access-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 8px 4px;
  border-radius: 14px;
  transition: transform 0.15s ease, background 0.2s ease;
}

.access-item:active {
  transform: scale(0.97);
  background: #f5f7fb;
}

.access-icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 14px;
}

.icon-blue {
  background: rgba(102, 126, 234, 0.12);
  color: #667eea;
}

.icon-green {
  background: rgba(32, 201, 151, 0.12);
  color: #20c997;
}

.icon-orange {
  background: rgba(255, 151, 106, 0.14);
  color: #ff976a;
}

.icon-purple {
  background: rgba(123, 97, 255, 0.12);
  color: #7b61ff;
}

.access-icon {
  font-size: 22px;
}

.access-item span {
  font-size: 12px;
  font-weight: 500;
  color: #2f3441;
}

.content-section {
  padding: 0 14px 8px;
}

.section-card {
  margin-bottom: 14px;
  padding: 16px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 10px 24px rgba(31, 45, 61, 0.05);
}

.section-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.section-header h3 {
  margin: 0 0 4px;
  font-size: 18px;
  font-weight: 700;
  color: #1f2330;
}

.section-header p {
  margin: 0;
  font-size: 12px;
  color: #8a90a2;
  line-height: 1.5;
}

.more {
  flex-shrink: 0;
  padding: 6px 10px;
  font-size: 12px;
  color: #667eea;
  background: rgba(102, 126, 234, 0.08);
  border-radius: 999px;
}

.state-wrap {
  padding: 26px 0;
}

.skills-list,
.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skill-item,
.recommend-item {
  border-radius: 18px;
  background: linear-gradient(180deg, #fafbff 0%, #f5f7fc 100%);
  border: 1px solid rgba(102, 126, 234, 0.08);
  transition: transform 0.15s ease, box-shadow 0.2s ease;
}

.skill-item {
  padding: 14px;
}

.recommend-item {
  padding: 15px;
}

.skill-item:active,
.recommend-item:active {
  transform: scale(0.985);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.08);
}

.skill-main {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
}

.skill-text {
  flex: 1;
  min-width: 0;
}

.skill-top-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.skill-top-row h4 {
  margin: 0;
  font-size: 15px;
  font-weight: 700;
  color: #1f2330;
  line-height: 1.4;
}

.skill-tag {
  flex-shrink: 0;
  padding: 2px 8px;
  font-size: 11px;
  color: #ff976a;
  background: rgba(255, 151, 106, 0.14);
  border-radius: 999px;
}

.skill-desc {
  margin: 0 0 10px;
  font-size: 12px;
  line-height: 1.6;
  color: #7a8193;
}

.skill-meta-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.meta-pill {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  font-size: 11px;
  color: #667eea;
  background: rgba(102, 126, 234, 0.08);
  border-radius: 999px;
}

.meta-pill.light {
  color: #6f7688;
  background: #eef1f6;
}

.skill-price-block {
  flex-shrink: 0;
  text-align: right;
}

.skill-price {
  display: block;
  font-size: 20px;
  font-weight: 700;
  color: #ff976a;
  line-height: 1.2;
}

.skill-price-block small,
.recommend-price small {
  font-size: 12px;
  color: #9aa1b3;
}

.recommend-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.recommend-title-wrap {
  flex: 1;
  min-width: 0;
}

.recommend-title {
  margin-bottom: 4px;
  font-size: 15px;
  font-weight: 700;
  color: #1f2330;
  line-height: 1.4;
}

.recommend-provider {
  font-size: 12px;
  color: #8a90a2;
}

.recommend-price {
  flex-shrink: 0;
  font-size: 18px;
  font-weight: 700;
  color: #ff976a;
}

.recommend-desc {
  margin-bottom: 12px;
  font-size: 12px;
  line-height: 1.6;
  color: #7a8193;
}

.recommend-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.recommend-rating {
  display: flex;
  align-items: center;
  gap: 6px;
}

.rating {
  font-size: 12px;
  font-weight: 600;
  color: #ff976a;
}

.no-rating {
  font-size: 12px;
  color: #99a1b3;
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 28px 12px;
  text-align: center;
  font-size: 13px;
  color: #99a1b3;
}

:deep(.van-search) {
  padding: 0;
  background: transparent;
}

:deep(.van-search__content) {
  border-radius: 14px;
  background: #f4f6fb;
}

:deep(.van-field__left-icon),
:deep(.van-search__action) {
  color: #667eea;
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