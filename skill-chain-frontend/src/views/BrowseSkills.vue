<template>
  <div class="browse-container">
    <van-nav-bar 
      title="浏览技能" 
      left-arrow 
      fixed 
      @click-left="goBack"
    >
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
          :class="['filter-tab', { active: activeFilter === 'nearby' }]"
          @click="setFilter('nearby')"
        >
          <van-icon name="location-o" /> 附近
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
      <div v-if="viewMode === 'grid'" class="skills-grid">
        <div 
          v-for="skill in filteredSkills" 
          :key="skill.id" 
          class="skill-card"
          @click="goToSkill(skill.id)"
        >
          <div class="card-image">
            <van-image :src="skill.image" fit="cover" />
            <div class="skill-tag" v-if="skill.isHot">
              <van-icon name="fire" size="10px" /> 热门
            </div>
            <div class="skill-tag new" v-else-if="skill.isNew">
              <van-icon name="new-o" size="10px" /> 新人
            </div>
          </div>
          <div class="card-content">
            <h4>{{ skill.title }}</h4>
            <p class="provider">{{ skill.provider }}</p>
            <div class="card-meta">
              <div class="rating">
                <van-rate :model-value="skill.rating" readonly size="12px" color="#ff976a" />
                <span>{{ skill.rating }}</span>
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

      <div v-else class="skills-list">
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
            <p class="description">{{ skill.description }}</p>
            <div class="row-meta">
              <div class="rating">
                <van-rate :model-value="skill.rating" readonly size="12px" color="#ff976a" />
                <span>{{ skill.rating }} ({{ skill.reviewCount }})</span>
              </div>
              <span class="location" v-if="skill.distance">
                <van-icon name="location-o" size="12px" />
                {{ skill.distance }}
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

      <div v-if="filteredSkills.length === 0" class="empty-state">
        <van-icon name="search" size="60px" color="#ccc" />
        <p>未找到相关技能</p>
        <van-button type="primary" plain @click="clearFilters">清除筛选</van-button>
      </div>

      <div class="load-status">
        <van-divider v-if="isLoading">
          <van-loading type="spinner" size="16px" />
        </van-divider>
        <van-divider v-else-if="hasMore">
          上拉加载更多
        </van-divider>
        <van-divider v-else>
          没有更多了
        </van-divider>
      </div>
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
            <van-checkbox name="online">线上</van-checkbox>
            <van-checkbox name="offline">线下</van-checkbox>
          </van-checkbox-group>
        </div>

        <div class="filter-group">
          <div class="group-title">接单状态</div>
          <van-radio-group v-model="filterForm.availability" direction="horizontal">
            <van-radio :name="''">全部</van-radio>
            <van-radio :name="'available'">可接单</van-radio>
            <van-radio :name="'busy'">忙碌中</van-radio>
          </van-radio-group>
        </div>

        <div class="panel-footer">
          <van-button type="primary" block round @click="applyFilters">应用筛选</van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'

interface Skill {
  id: number
  title: string
  description: string
  provider: string
  price: number
  unit: string
  rating: number
  reviewCount: number
  orderCount: number
  image: string
  isHot?: boolean
  isNew?: boolean
  distance?: string
  serviceType: 'online' | 'offline' | 'both'
  available: boolean
}

const router = useRouter()
const route = useRoute()

const searchKeyword = ref('')
const activeFilter = ref('hot')
const sortBy = ref('default')
const priceSortAsc = ref(true)
const viewMode = ref<'grid' | 'list'>('grid')
const showFilterPopup = ref(false)
const isLoading = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)

const filterForm = ref({
  priceMin: '',
  priceMax: '',
  minRating: 0,
  serviceTypes: [] as string[],
  availability: ''
})

const allSkills = ref<Skill[]>([
  { id: 1, title: '专业家政清洁服务', description: '提供家庭深度清洁、日常保洁、开荒保洁等服务', provider: '张阿姨', price: 100, unit: '次', rating: 4.8, reviewCount: 256, orderCount: 589, image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg', isHot: true, serviceType: 'offline', available: true },
  { id: 2, title: '王者荣耀陪练上分', description: '专业代练，快速上分，赛季冲榜', provider: '小王', price: 50, unit: '小时', rating: 4.9, reviewCount: 189, orderCount: 456, image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg', isHot: true, serviceType: 'online', available: true },
  { id: 3, title: '专业Logo设计', description: '品牌Logo、VI设计、商标注册', provider: '李设计师', price: 200, unit: '个', rating: 4.7, reviewCount: 98, orderCount: 234, image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg', isHot: true, serviceType: 'online', available: true },
  { id: 4, title: 'Python编程入门', description: '一对一教学，从零基础到项目实战', provider: '王老师', price: 150, unit: '小时', rating: 5.0, reviewCount: 67, orderCount: 167, image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg', isNew: true, serviceType: 'online', available: true },
  { id: 5, title: '瑜伽私教课程', description: '减脂塑形、康复训练、孕妇瑜伽', provider: '张教练', price: 180, unit: '小时', rating: 4.9, reviewCount: 145, orderCount: 312, image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg', serviceType: 'both', available: true },
  { id: 6, title: '英语口语陪练', description: '商务英语、出国口语、雅思托福', provider: '李老师', price: 120, unit: '小时', rating: 4.8, reviewCount: 234, orderCount: 567, image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg', serviceType: 'both', available: false },
  { id: 7, title: '简历优化服务', description: '简历诊断、修改润色、面试辅导', provider: 'HR王老师', price: 80, unit: '份', rating: 4.7, reviewCount: 89, orderCount: 234, image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg', serviceType: 'online', available: true },
  { id: 8, title: '钢琴入门教学', description: '零基础入门、考级辅导、成人钢琴', provider: '赵老师', price: 200, unit: '小时', rating: 5.0, reviewCount: 56, orderCount: 123, image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg', isNew: true, serviceType: 'offline', available: true },
  { id: 9, title: '摄影修图服务', description: '人像精修、婚礼跟拍、产品摄影', provider: '陈摄影师', price: 300, unit: '次', rating: 4.8, reviewCount: 78, orderCount: 189, image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg', serviceType: 'offline', available: true },
  { id: 10, title: '宠物美容护理', description: '洗澡美容、造型修剪、寄养服务', provider: '宠物店', price: 80, unit: '次', rating: 4.7, reviewCount: 123, orderCount: 345, image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg', serviceType: 'offline', available: true }
])

const filteredSkills = computed(() => {
  let result = [...allSkills.value]

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(s => 
      s.title.toLowerCase().includes(keyword) ||
      s.provider.toLowerCase().includes(keyword) ||
      s.description.toLowerCase().includes(keyword)
    )
  }

  if (filterForm.value.priceMin) {
    result = result.filter(s => s.price >= Number(filterForm.value.priceMin))
  }
  if (filterForm.value.priceMax) {
    result = result.filter(s => s.price <= Number(filterForm.value.priceMax))
  }
  if (filterForm.value.minRating > 0) {
    result = result.filter(s => s.rating >= filterForm.value.minRating)
  }
  if (filterForm.value.serviceTypes.length > 0) {
    result = result.filter(s => 
      s.serviceType === 'both' || 
      filterForm.value.serviceTypes.includes(s.serviceType)
    )
  }
  if (filterForm.value.availability === 'available') {
    result = result.filter(s => s.available)
  } else if (filterForm.value.availability === 'busy') {
    result = result.filter(s => !s.available)
  }

  switch (sortBy.value) {
    case 'sales':
      result.sort((a, b) => b.orderCount - a.orderCount)
      break
    case 'price':
      result.sort((a, b) => priceSortAsc.value ? a.price - b.price : b.price - a.price)
      break
    case 'rating':
      result.sort((a, b) => b.rating - a.rating)
      break
    default:
      if (activeFilter.value === 'hot') {
        result.sort((a, b) => (b.isHot ? 1 : 0) - (a.isHot ? 1 : 0))
      } else if (activeFilter.value === 'new') {
        result.sort((a, b) => (b.isNew ? 1 : 0) - (a.isNew ? 1 : 0))
      } else if (activeFilter.value === 'nearby') {
        result = result.map(s => ({
          ...s,
          distance: `${(Math.random() * 5 + 0.5).toFixed(1)}km`
        })).sort((a, b) => parseFloat(a.distance!) - parseFloat(b.distance!))
      }
  }

  return result
})

const hasActiveFilters = computed(() => {
  return filterForm.value.priceMin || 
    filterForm.value.priceMax || 
    filterForm.value.minRating > 0 ||
    filterForm.value.serviceTypes.length > 0 ||
    filterForm.value.availability
})

const priceSortIcon = computed(() => {
  if (sortBy.value !== 'price') return 'caret-down'
  return priceSortAsc.value ? 'caret-up' : 'caret-down'
})

onMounted(() => {
  if (route.query.keyword) {
    searchKeyword.value = route.query.keyword as string
  }
  if (route.query.tab) {
    activeFilter.value = route.query.tab as string
  }
})

watch(() => route.query.tab, (newTab) => {
  if (newTab) {
    activeFilter.value = newTab as string
  }
})

const goBack = () => router.back()

const goToSkill = (id: number) => {
  router.push(`/skill/${id}`)
}

const toggleViewMode = () => {
  viewMode.value = viewMode.value === 'grid' ? 'list' : 'grid'
}

const setFilter = (filter: string) => {
  activeFilter.value = filter
  if (filter === 'price' && sortBy.value === 'price') {
    priceSortAsc.value = !priceSortAsc.value
  } else if (filter !== 'price') {
    sortBy.value = 'default'
  }
}

const setSort = (sort: string) => {
  if (sortBy.value === sort && sort === 'price') {
    priceSortAsc.value = !priceSortAsc.value
  }
  sortBy.value = sort
  if (sort !== 'hot' && sort !== 'new' && sort !== 'nearby') {
    activeFilter.value = 'hot'
  }
}

const onSearch = () => {
  currentPage.value = 1
  showToast('搜索完成')
}

const clearFilters = () => {
  filterForm.value = {
    priceMin: '',
    priceMax: '',
    minRating: 0,
    serviceTypes: [],
    availability: ''
  }
  searchKeyword.value = ''
  showFilterPopup.value = false
  showToast('已清除筛选')
}

const applyFilters = () => {
  showFilterPopup.value = false
  currentPage.value = 1
  showToast('筛选已应用')
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
}

.rating {
  display: flex;
  align-items: center;
  gap: 4px;
}

.rating span {
  font-size: 12px;
  color: #ff976a;
}

.orders {
  font-size: 11px;
  color: #999;
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
}

.row-content {
  flex: 1;
  display: flex;
  flex-direction: column;
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
}

.row-meta .rating span {
  font-size: 12px;
  color: #666;
}

.location {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 2px;
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

.load-status {
  padding: 20px 0;
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
