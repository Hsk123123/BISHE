<template>
  <div class="home-container">
    <van-nav-bar title="技能链" fixed />
    
    <NoticePopup ref="noticePopupRef" @view-detail="goToNotices" />
    
    <div class="banner">
      <h1>发现你的技能伙伴</h1>
      <p>游戏化技能共享预约平台</p>
    </div>

    <div class="search-section">
      <van-search
        v-model="searchKeyword"
        placeholder="搜索技能、服务者"
        shape="round"
        @search="onSearch"
        @click="goToBrowse"
      />
    </div>

    <div class="quick-access">
      <div class="access-item" @click="goToBrowse">
        <van-icon name="search" class="access-icon" />
        <span>浏览全部</span>
      </div>
      <div class="access-item" @click="goToNearby">
        <van-icon name="location-o" class="access-icon" />
        <span>附近</span>
      </div>
      <div class="access-item" @click="goToHot">
        <van-icon name="fire" class="access-icon" />
        <span>热门</span>
      </div>
      <div class="access-item" @click="goToNew">
        <van-icon name="new-o" class="access-icon" />
        <span>最新</span>
      </div>
    </div>

    <div class="featured-skills">
      <div class="section-header">
        <h3>🔥 热门技能</h3>
        <span class="more" @click="goToBrowse">查看更多</span>
      </div>
      <div class="skills-list">
        <div 
          v-for="skill in featuredSkills" 
          :key="skill.id" 
          class="skill-item"
          @click="goToSkill(skill.id)"
        >
          <div class="skill-title-row">
            <h4>{{ skill.title }}</h4>
            <span class="skill-price">¥{{ skill.price }}<small>/{{ skill.unit }}</small></span>
          </div>
          <p class="skill-meta-row">
            <span class="orders">{{ skill.orderCount }}人已预约</span>
          </p>
        </div>
      </div>
    </div>

    <div class="recommendation-section">
      <div class="section-header">
        <h3>✨ 为你推荐</h3>
      </div>
      <div class="recommend-list">
        <div 
          v-for="skill in recommendedSkills" 
          :key="skill.id" 
          class="recommend-item"
          @click="goToSkill(skill.id)"
        >
          <div class="recommend-title">{{ skill.title }}</div>
          <div class="recommend-meta">
            <span class="provider">{{ skill.provider }}</span>
            <van-rate :model-value="skill.rating" readonly size="12px" color="#ff976a" />
            <span class="rating">{{ skill.rating }}分</span>
          </div>
          <div class="recommend-footer">
            <span class="price">¥{{ skill.price }}<small>/{{ skill.unit }}</small></span>
          </div>
        </div>
      </div>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import NoticePopup from '@/components/NoticePopup.vue'

const router = useRouter()
const activeTab = ref(0)
const searchKeyword = ref('')
const noticePopupRef = ref()

onMounted(() => {
  setTimeout(() => {
    if (noticePopupRef.value) {
      noticePopupRef.value.show()
    }
  }, 500)
})

const goToNotices = () => {
  router.push('/notices')
}

const featuredSkills = ref([
  { 
    id: 1, 
    title: '专业家政清洁', 
    description: '提供家庭深度清洁服务', 
    price: 100, 
    unit: '次', 
    orderCount: 256,
    image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg' 
  },
  { 
    id: 2, 
    title: '游戏陪练上分', 
    description: '王者荣耀/英雄联盟专业陪练', 
    price: 50, 
    unit: '小时', 
    orderCount: 189,
    image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg' 
  },
  { 
    id: 3, 
    title: '专业Logo设计', 
    description: '品牌Logo/VI设计服务', 
    price: 200, 
    unit: '个', 
    orderCount: 98,
    image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg' 
  },
  { 
    id: 4, 
    title: 'Python编程教学', 
    description: '一对一编程入门教学', 
    price: 150, 
    unit: '小时', 
    orderCount: 67,
    image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg' 
  }
])

const recommendedSkills = ref([
  { 
    id: 5, 
    title: '瑜伽私教课程', 
    provider: '张老师', 
    price: 180, 
    unit: '小时', 
    rating: 4.9,
    image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg' 
  },
  { 
    id: 6, 
    title: '英语口语陪练', 
    provider: '李老师', 
    price: 120, 
    unit: '小时', 
    rating: 4.8,
    image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg' 
  },
  { 
    id: 7, 
    title: '简历优化服务', 
    provider: '王老师', 
    price: 80, 
    unit: '份', 
    rating: 4.7,
    image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg' 
  },
  { 
    id: 8, 
    title: '钢琴入门教学', 
    provider: '赵老师', 
    price: 200, 
    unit: '小时', 
    rating: 5.0,
    image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg' 
  }
])

const goToSkill = (id: number) => {
  router.push(`/skill/${id}`)
}

const goToBrowse = () => {
  router.push('/browse')
}

const goToNearby = () => {
  router.push('/browse?tab=nearby')
}

const goToHot = () => {
  router.push('/browse?tab=hot')
}

const goToNew = () => {
  router.push('/browse?tab=new')
}

const onSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push(`/browse?keyword=${encodeURIComponent(searchKeyword.value)}`)
  } else {
    router.push('/browse')
  }
}
</script>

<style scoped>
.home-container {
  padding-top: 46px;
  padding-bottom: 50px;
  min-height: 100vh;
  background: #f5f5f5;
}

.banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px 20px;
  text-align: center;
}

.banner h1 {
  font-size: 22px;
  margin-bottom: 8px;
  font-weight: 600;
}

.banner p {
  font-size: 13px;
  opacity: 0.9;
  margin: 0;
}

.search-section {
  margin: -20px 15px 10px 15px;
  position: relative;
  z-index: 10;
}

.quick-access {
  display: flex;
  background: white;
  margin: 10px;
  border-radius: 12px;
  padding: 15px 10px;
  justify-content: space-around;
}

.access-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background 0.2s;
}

.access-item:active {
  background: #f5f5f5;
}

.access-icon {
  font-size: 24px;
  color: #667eea;
}

.access-item span {
  font-size: 12px;
  color: #333;
}

.featured-skills, .recommendation-section {
  background: white;
  margin: 10px;
  padding: 15px;
  border-radius: 12px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.more {
  font-size: 13px;
  color: #667eea;
}

.skills-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.skill-item {
  padding: 14px;
  background: #f9f9f9;
  border-radius: 10px;
  transition: transform 0.2s, background 0.2s;
}

.skill-item:active {
  transform: scale(0.98);
  background: #f0f0f0;
}

.skill-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.skill-title-row h4 {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.skill-price {
  font-size: 16px;
  font-weight: 600;
  color: #ff976a;
}

.skill-price small {
  font-size: 12px;
  font-weight: 400;
  color: #999;
}

.skill-meta-row .orders {
  font-size: 12px;
  color: #667eea;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.recommend-item {
  padding: 14px;
  background: #f9f9f9;
  border-radius: 10px;
  transition: transform 0.2s;
}

.recommend-item:active {
  transform: scale(0.98);
}

.recommend-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.recommend-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.recommend-meta .provider {
  font-size: 13px;
  color: #666;
}

.rating {
  font-size: 12px;
  color: #ff976a;
  font-weight: 500;
}

.recommend-footer {
  display: flex;
  justify-content: flex-end;
}

.recommend-footer .price {
  font-size: 18px;
  font-weight: 600;
  color: #ff976a;
}

.recommend-footer .price small {
  font-size: 12px;
  font-weight: 400;
  color: #999;
}
</style>
