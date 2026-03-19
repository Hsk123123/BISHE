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
      <van-card
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeTab = ref(1)
const searchText = ref('')
const category = ref(0)
const sort = ref(0)

const categoryOptions = [
  { text: '全部分类', value: 0 },
  { text: '家政服务', value: 1 },
  { text: '技能陪练', value: 2 },
  { text: '设计服务', value: 3 },
  { text: '教育培训', value: 4 }
]

const sortOptions = [
  { text: '综合排序', value: 0 },
  { text: '价格从低到高', value: 1 },
  { text: '价格从高到低', value: 2 },
  { text: '最新发布', value: 3 }
]

const skills = ref([
  { id: 1, title: '专业家政清洁', description: '提供家庭深度清洁服务', price: '100元/次', image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg' },
  { id: 2, title: '游戏陪练', description: '王者荣耀/英雄联盟陪练', price: '50元/小时', image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg' },
  { id: 3, title: 'Logo设计', description: '专业品牌Logo设计', price: '200元/个', image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg' },
  { id: 4, title: '英语口语陪练', description: '雅思/托福口语陪练', price: '80元/小时', image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg' }
])

const onSearch = () => {
  console.log('搜索:', searchText.value)
}

const onFilterChange = () => {
  console.log('筛选:', category.value, sort.value)
}

const goToSkill = (id: number) => {
  router.push(`/skill/${id}`)
}
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
</style>