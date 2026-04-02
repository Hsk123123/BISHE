<template>
  <div class="browse-container">
    <van-nav-bar title="浏览技能" left-arrow fixed @click-left="goBack">
      <template #right>
        <van-icon
          :name="viewMode === 'grid' ? 'bars' : 'apps-o'"
          size="20"
          @click="toggleViewMode"
        />
      </template>
    </van-nav-bar>

    <div class="search-bar">
      <van-search
        v-model="searchKeyword"
        placeholder="搜索技能、服务者"
        shape="round"
        show-action
        @search="onSearch"
      >
        <template #action>
          <span @click="onSearch">搜索</span>
        </template>
      </van-search>
    </div>

    <div class="filter-section">
      <div class="filter-tabs">
        <div
          :class="['filter-tab', { active: activeFilter === 'hot' }]"
          @click="setFilter('hot')"
        >
          <van-icon name="fire" /> 热门
        </div>

        <div
          :class="['filter-tab', { active: activeFilter === 'new' }]"
          @click="setFilter('new')"
        >
          <van-icon name="new-o" /> 最新
        </div>

        <div
          :class="['filter-tab', { active: showFilterPopup }]"
          @click="showFilterPopup = true"
        >
          <van-icon name="filter-o" /> 筛选
          <span v-if="hasActiveFilters" class="filter-badge"></span>
        </div>
      </div>

      <div class="sort-tabs">
        <div
          :class="['sort-item', { active: sortBy === 'default' }]"
          @click="setSort('default')"
        >
          综合
        </div>

        <div
          :class="['sort-item', { active: sortBy === 'sales' }]"
          @click="setSort('sales')"
        >
          销量
        </div>

        <div
          :class="['sort-item', { active: sortBy === 'price' }]"
          @click="setSort('price')"
        >
          价格
          <van-icon :name="priceSortIcon" size="12px" />
        </div>

        <div
          :class="['sort-item', { active: sortBy === 'rating' }]"
          @click="setSort('rating')"
        >
          评分
        </div>
      </div>
    </div>

    <div class="skills-content">
      <van-loading v-if="isLoading" size="24px" vertical class="state-wrap">
        加载中...
      </van-loading>

      <div v-else-if="loadFailed" class="empty-state">
        <van-icon name="warning-o" size="60px" color="#ccc" />
        <p>技能列表加载失败</p>
        <van-button type="primary" plain round @click="loadSkills">重新加载</van-button>
      </div>

      <template v-else>
        <div v-if="filteredSkills.length > 0 && viewMode === 'grid'" class="skills-grid">
          <div
            v-for="skill in filteredSkills"
            :key="skill.id"
            class="skill-card"
            @click="goToSkill(skill.id)"
          >
            <div class="card-image">
              <van-image :src="skill.image" fit="cover" />
              <div class="skill-tag" v-if="isHotSkill(skill)">
                <van-icon name="fire" size="10px" /> 热门
              </div>
              <div class="skill-tag new" v-else-if="isNewSkill(skill)">
                <van-icon name="new-o" size="10px" /> 新上架
              </div>
            </div>

            <div class="card-content">
              <h4>{{ skill.title }}</h4>
              <p class="provider">{{ skill.provider }}</p>

              <div class="card-meta">
                <div class="rating">
                  <template v-if="skill.rating !== null">
                    <van-rate
                      :model-value="skill.rating"
                      readonly
                      allow-half
                      size="12px"
                      color="#ff976a"
                    />
                    <span>{{ skill.rating.toFixed(1) }}</span>
                  </template>
                  <template v-else>
                    <span class="muted-text">暂无评分</span>
                  </template>
                </div>

                <span class="orders">{{ skill.orderCount }}人</span>
              </div>

              <div class="card-footer">
                <div class="price">
                  <span class="currency">¥</span>
                  <span class="amount">{{ skill.price }}</span>
                  <span class="unit">/{{ skill.unit }}</span>
                </div>
                <van-button type="primary" size="mini" round>预约</van-button>
              </div>
            </div>
          </div>
        </div>

        <div v-else-if="filteredSkills.length > 0" class="skills-list">
          <div
            v-for="skill in filteredSkills"
            :key="skill.id"
            class="skill-row"
            @click="goToSkill(skill.id)"
          >
            <van-image :src="skill.image" class="row-image" />

            <div class="row-content">
              <h4>{{ skill.title }}</h4>
              <p class="provider">{{ skill.provider }}</p>
              <p class="description">{{ skill.description || '暂无详细描述' }}</p>

              <div class="row-meta">
                <div class="rating">
                  <template v-if="skill.rating !== null">
                    <van-rate
                      :model-value="skill.rating"
                      readonly
                      allow-half
                      size="12px"
                      color="#ff976a"
                    />
                    <span>{{ skill.rating.toFixed(1) }} ({{ skill.reviewCount }})</span>
                  </template>
                  <template v-else>
                    <span class="muted-text">暂无评分</span>
                  </template>
                </div>

                <span class="service-type">
                  {{ getServiceModeText(skill.serviceMode) }}
                </span>
              </div>

              <div class="row-footer">
                <div class="price">
                  <span class="currency">¥</span>
                  <span class="amount">{{ skill.price }}</span>
                  <span class="unit">/{{ skill.unit }}</span>
                </div>
                <span class="sales">{{ skill.orderCount }}人已预约</span>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <van-icon name="search" size="60px" color="#ccc" />
          <p>未找到相关技能</p>
          <van-button type="primary" plain round @click="clearFilters">清除筛选</van-button>
        </div>
      </template>
    </div>

    <van-popup v-model:show="showFilterPopup" position="right" :style="{ width: '80%', height: '100%' }">
      <div class="filter-panel">
        <div class="panel-header">
          <span>筛选条件</span>
          <van-button type="primary" size="small" text @click="clearFilters">清除</van-button>
        </div>

        <div class="filter-group">
          <div class="group-title">价格范围</div>
          <div class="price-range">
            <van-field
              v-model="filterForm.priceMin"
              type="number"
              placeholder="最低价"
              class="price-input"
            />
            <span class="separator">-</span>
            <van-field
              v-model="filterForm.priceMax"
              type="number"
              placeholder="最高价"
              class="price-input"
            />
          </div>
        </div>

        <div class="filter-group">
          <div class="group-title">评分要求</div>
          <van-radio-group v-model="filterForm.minRating" direction="horizontal">
            <van-radio :name="0">不限</van-radio>
            <van-radio :name="4">4分以上</van-radio>
            <van-radio :name="4.5">4.5分以上</van-radio>
          </van-radio-group>
        </div>

        <div class="filter-group">
          <div class="group-title">服务方式</div>
          <van-checkbox-group v-model="filterForm.serviceTypes" direction="horizontal">
            <van-checkbox name="1">线上</van-checkbox>
            <van-checkbox name="2">线下</van-checkbox>
          </van-checkbox-group>
        </div>

        <div class="panel-footer">
          <van-button type="primary" block round @click="applyFilters">应用筛选</van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
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

interface SkillItem {
  id: number
  title: string
  description: string
  provider: string
  price: number
  unit: string
  rating: number | null
  reviewCount: number
  orderCount: number
  image: string
  serviceMode: number
}

type SortType = 'default' | 'sales' | 'price' | 'rating'
type FilterType = 'hot' | 'new'

const router = useRouter()
const route = useRoute()

const searchKeyword = ref('')
const activeFilter = ref<FilterType>('hot')
const sortBy = ref<SortType>('default')
const priceSortAsc = ref(true)
const viewMode = ref<'grid' | 'list'>('grid')
const showFilterPopup = ref(false)
const isLoading = ref(false)
const loadFailed = ref(false)

const filterForm = ref({
  priceMin: '',
  priceMax: '',
  minRating: 0,
  serviceTypes: [] as string[]
})

const allSkills = ref<SkillItem[]>([])

const unitMap: Record<number, string> = {
  1: '小时',
  2: '次',
  3: '单',
  4: '月'
}

const defaultImage = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'

const normalizeSkill = (raw: RawSkill): SkillItem => {
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
    rating,
    reviewCount: Number(raw.reviewCount ?? 0),
    orderCount: Number(raw.orderCount ?? 0),
    image,
    serviceMode: Number(raw.serviceMode ?? 0)
  }
}

const hotSkillIds = computed(() => {
  return [...allSkills.value]
    .sort((a, b) => {
      if (b.orderCount !== a.orderCount) return b.orderCount - a.orderCount
      return b.id - a.id
    })
    .slice(0, 6)
    .map((item) => item.id)
})

const newSkillIds = computed(() => {
  return [...allSkills.value]
    .sort((a, b) => b.id - a.id)
    .slice(0, 6)
    .map((item) => item.id)
})

const isHotSkill = (skill: SkillItem) => hotSkillIds.value.includes(skill.id)
const isNewSkill = (skill: SkillItem) => newSkillIds.value.includes(skill.id)

const filteredSkills = computed(() => {
  let result = [...allSkills.value]

  const keyword = searchKeyword.value.trim().toLowerCase()
  if (keyword) {
    result = result.filter((s) =>
      s.title.toLowerCase().includes(keyword) ||
      s.provider.toLowerCase().includes(keyword) ||
      s.description.toLowerCase().includes(keyword)
    )
  }

  if (filterForm.value.priceMin) {
    result = result.filter((s) => s.price >= Number(filterForm.value.priceMin))
  }

  if (filterForm.value.priceMax) {
    result = result.filter((s) => s.price <= Number(filterForm.value.priceMax))
  }

  if (filterForm.value.minRating > 0) {
    result = result.filter((s) => (s.rating ?? 0) >= filterForm.value.minRating)
  }

  if (filterForm.value.serviceTypes.length > 0) {
    result = result.filter((s) =>
      filterForm.value.serviceTypes.includes(String(s.serviceMode))
    )
  }

  if (sortBy.value === 'sales') {
    result.sort((a, b) => {
      if (b.orderCount !== a.orderCount) return b.orderCount - a.orderCount
      return b.id - a.id
    })
    return result
  }

  if (sortBy.value === 'price') {
    result.sort((a, b) => (priceSortAsc.value ? a.price - b.price : b.price - a.price))
    return result
  }

  if (sortBy.value === 'rating') {
    result.sort((a, b) => {
      const ratingA = a.rating ?? -1
      const ratingB = b.rating ?? -1
      if (ratingB !== ratingA) return ratingB - ratingA
      return b.id - a.id
    })
    return result
  }

  if (activeFilter.value === 'new') {
    result.sort((a, b) => b.id - a.id)
  } else {
    result.sort((a, b) => {
      if (b.orderCount !== a.orderCount) return b.orderCount - a.orderCount
      return b.id - a.id
    })
  }

  return result
})

const hasActiveFilters = computed(() => {
  return Boolean(
    filterForm.value.priceMin ||
    filterForm.value.priceMax ||
    filterForm.value.minRating > 0 ||
    filterForm.value.serviceTypes.length > 0
  )
})

const priceSortIcon = computed(() => {
  if (sortBy.value !== 'price') return 'arrow-down'
  return priceSortAsc.value ? 'arrow-up' : 'arrow-down'
})

const syncStateFromRoute = () => {
  searchKeyword.value = typeof route.query.keyword === 'string' ? route.query.keyword : ''

  const tab = route.query.tab
  activeFilter.value = tab === 'new' ? 'new' : 'hot'

  const sort = route.query.sort
  if (sort === 'sales' || sort === 'price' || sort === 'rating' || sort === 'default') {
    sortBy.value = sort
  } else {
    sortBy.value = 'default'
  }
}

const updateRouteQuery = () => {
  const query: Record<string, string> = {}

  const keyword = searchKeyword.value.trim()
  if (keyword) query.keyword = keyword
  if (activeFilter.value !== 'hot') query.tab = activeFilter.value
  if (sortBy.value !== 'default') query.sort = sortBy.value

  router.replace({ path: '/browse', query })
}

const loadSkills = async () => {
  isLoading.value = true
  loadFailed.value = false

  try {
    const params: Record<string, string | number> = {
      page: 1,
      size: 50
    }

    const keyword = searchKeyword.value.trim()
    if (keyword) {
      params.keyword = keyword
    }

    const data = (await getSkillList(params)) as { records?: RawSkill[] }
    const records = data?.records ?? []
    allSkills.value = records.map(normalizeSkill).filter((item) => item.id > 0)
  } catch {
    allSkills.value = []
    loadFailed.value = true
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  syncStateFromRoute()
  loadSkills()
})

watch(
  () => route.query,
  () => {
    syncStateFromRoute()
    loadSkills()
  }
)

const goBack = () => router.back()

const goToSkill = (id: number) => {
  router.push(`/skill/${id}`)
}

const toggleViewMode = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}

const setFilter = (filter: FilterType) => {
  activeFilter.value = filter
  updateRouteQuery()
}

const setSort = (sort: SortType) => {
  if (sortBy.value === sort && sort === 'price') {
    priceSortAsc.value = !priceSortAsc.value
  } else {
    sortBy.value = sort
  }
  updateRouteQuery()
}

const onSearch = () => {
  updateRouteQuery()
}

const clearFilters = () => {
  filterForm.value = {
    priceMin: '',
    priceMax: '',
    minRating: 0,
    serviceTypes: []
  }
  searchKeyword.value = ''
  activeFilter.value = 'hot'
  sortBy.value = 'default'
  priceSortAsc.value = true
  showFilterPopup.value = false
  router.replace({ path: '/browse', query: {} })
}

const applyFilters = () => {
  showFilterPopup.value = false
}

const getServiceModeText = (serviceMode: number) => {
  if (serviceMode === 1) return '线上服务'
  if (serviceMode === 2) return '线下服务'
  return '服务方式待定'
}
</script>

<style scoped>
.browse-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-top: 46px;
}

.search-bar {
  background: white;
  position: sticky;
  top: 46px;
  z-index: 10;
}

.filter-section {
  background: white;
  border-bottom: 1px solid #f0f0f0;
}

.filter-tabs {
  display: flex;
  padding: 10px 15px;
  gap: 10px;
  overflow-x: auto;
}

.filter-tab {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  font-size: 13px;
  color: #666;
  background: #f5f5f5;
  border-radius: 16px;
  white-space: nowrap;
  position: relative;
  transition: all 0.3s;
}

.filter-tab.active {
  color: #667eea;
  background: #f0f4ff;
}

.filter-badge {
  position: absolute;
  top: -2px;
  right: -2px;
  width: 8px;
  height: 8px;
  background: #ff4d4f;
  border-radius: 50%;
}

.sort-tabs {
  display: flex;
  padding: 10px 15px;
  border-top: 1px solid #f0f0f0;
}

.sort-item {
  flex: 1;
  text-align: center;
  font-size: 14px;
  color: #666;
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2px;
  transition: color 0.3s;
}

.sort-item.active {
  color: #667eea;
  font-weight: 600;
}

.skills-content {
  padding: 10px;
}

.state-wrap {
  padding: 32px 0;
}

.skills-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.skill-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s;
}

.skill-card:active {
  transform: scale(0.98);
}

.card-image {
  position: relative;
  height: 120px;
}

.card-image .van-image {
  width: 100%;
  height: 100%;
}

.skill-tag {
  position: absolute;
  top: 8px;
  left: 8px;
  background: linear-gradient(135deg, #ff976a, #ff6030);
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 2px;
}

.skill-tag.new {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.card-content {
  padding: 12px;
}

.card-content h4 {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-content .provider {
  font-size: 12px;
  color: #999;
  margin: 0 0 8px 0;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  gap: 8px;
}

.rating {
  display: flex;
  align-items: center;
  gap: 4px;
  min-width: 0;
}

.rating span {
  font-size: 12px;
  color: #ff976a;
}

.muted-text {
  font-size: 12px;
  color: #999;
}

.orders {
  font-size: 11px;
  color: #999;
  flex-shrink: 0;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price .currency {
  font-size: 12px;
  color: #ff976a;
}

.price .amount {
  font-size: 18px;
  font-weight: 600;
  color: #ff976a;
}

.price .unit {
  font-size: 12px;
  color: #999;
}

.skills-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skill-row {
  display: flex;
  gap: 12px;
  background: white;
  border-radius: 12px;
  padding: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s;
}

.skill-row:active {
  transform: scale(0.98);
}

.row-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  flex-shrink: 0;
}

.row-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.row-content h4 {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.row-content .provider {
  font-size: 13px;
  color: #999;
  margin: 0 0 4px 0;
}

.row-content .description {
  font-size: 12px;
  color: #666;
  margin: 0 0 8px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.row-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  gap: 8px;
}

.service-type {
  font-size: 12px;
  color: #999;
  flex-shrink: 0;
}

.row-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sales {
  font-size: 12px;
  color: #999;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-state p {
  color: #999;
  margin: 15px 0;
}

.filter-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 16px;
  font-weight: 600;
}

.filter-group {
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.group-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.price-range {
  display: flex;
  align-items: center;
  gap: 10px;
}

.price-input {
  flex: 1;
  background: #f5f5f5;
  border-radius: 6px;
  padding: 8px 12px;
}

.separator {
  color: #999;
}

.panel-footer {
  margin-top: auto;
  padding: 15px;
  border-top: 1px solid #f0f0f0;
}
</style>