<template>
  <div class="discover-container">
    <van-nav-bar title="发现" fixed />
    
    <div class="search-bar">
      <van-search v-model="searchText" placeholder="搜索技能" @search="onSearch" />
    </div>

    <div class="filter-bar">
      <van-dropdown-menu>
        <van-dropdown-item v-model="category" :options="categoryOptions" @change="onFilterChange" />
        <van-dropdown-item v-model="sort" :options="sortOptions" @change="onFilterChange" />
      </van-dropdown-menu>
    </div>

    <div class="skill-list">
      <van-loading v-if="loading" size="24px" vertical style="padding: 20px;">加载中...</van-loading>
      <div v-else-if="skills.length === 0" class="empty-tip">暂无技能数据</div>
      <van-card
        v-else
        v-for="skill in skills"
        :key="skill.id"
        :desc="skill.description"
        :title="skill.title"
        :thumb="skill.image"
        :price="skill.price"
        @click="goToSkill(skill.id)"
      />
    </div>

    <van-tabbar v-model="activeTab" fixed>
      <van-tabbar-item icon="home-o" to="/home">首页</van-tabbar-item>
      <van-tabbar-item icon="search" to="/discover">发现</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/orders">订单</van-tabbar-item>
      <van-tabbar-item icon="user-o" to="/profile">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getSkillList } from '@/api/skill'

interface SkillItem {
  id: number
  title: string
  description: string
  price: string
  image: string
}

const router = useRouter()
const activeTab = ref(1)
const searchText = ref('')
const category = ref(0)
const sort = ref(0)
const loading = ref(false)

const categoryOptions = [
  { text: '全部分类', value: 0 },
  { text: '家政服务', value: 1 },
  { text: '技能陪练', value: 2 },
  { text: '设计服务', value: 3 },
  { text: '咨询服务', value: 4 },
  { text: '教育培训', value: 5 }
]

const sortOptions = [
  { text: '综合排序', value: 0 },
  { text: '价格从低到高', value: 1 },
  { text: '价格从高到低', value: 2 },
  { text: '最新发布', value: 3 }
]

const skills = ref<SkillItem[]>([])

const loadSkills = async () => {
  loading.value = true
  try {
    const params: any = {
      page: 1,
      size: 50,
      sort: sort.value
    }
    if (category.value > 0) {
      params.categoryId = category.value
    }
    if (searchText.value.trim()) {
      params.keyword = searchText.value.trim()
    }
    
    const data = await getSkillList(params) as { records?: Array<any> }
    const records = data?.records ?? []
    const unitMap: Record<number, string> = { 1: '小时', 2: '次', 3: '单', 4: '月' }
    
    skills.value = records.map((r: any) => ({
      id: r.skillId ?? r.id,
      title: r.title ?? '技能服务',
      description: r.description ?? '',
      price: `¥${Number(r.pricePerUnit ?? 0)}/${unitMap[r.unitType] ?? '次'}`,
      image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'
    }))
  } catch {
    skills.value = []
  } finally {
    loading.value = false
  }
}

watch([category, sort], () => {
  loadSkills()
})

const onSearch = () => {
  loadSkills()
}

const goToSkill = (id: number) => {
  router.push(`/skill/${id}`)
}

onMounted(() => {
  loadSkills()
})
</script>

<style scoped>
.discover-container {
  padding-top: 46px;
  padding-bottom: 50px;
  min-height: 100vh;
  background: #f5f5f5;
}

.search-bar {
  background: white;
  padding: 10px;
}

.skill-list {
  padding: 10px;
}

.skill-list .van-card {
  margin-bottom: 10px;
  border-radius: 8px;
  overflow: hidden;
}

.empty-tip {
  text-align: center;
  color: #999;
  font-size: 14px;
  padding: 40px 20px;
}
</style>