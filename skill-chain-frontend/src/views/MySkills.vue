<template>
  <div class="my-skills-container">
    <van-nav-bar 
      title="我的技能" 
      left-arrow 
      fixed 
      @click-left="goBack"
    />
    
    <div class="content">
      <div class="tabs">
        <div 
          :class="['tab-item', { active: activeTab === 0 }]"
          @click="activeTab = 0"
        >
          已发布 ({{ publishedCount }})
        </div>
        <div 
          :class="['tab-item', { active: activeTab === 1 }]"
          @click="activeTab = 1"
        >
          审核中 ({{ pendingCount }})
        </div>
        <div 
          :class="['tab-item', { active: activeTab === 2 }]"
          @click="activeTab = 2"
        >
          已下架 ({{ offlineCount }})
        </div>
      </div>

      <div class="skills-list">
        <div v-for="skill in filteredSkills" :key="skill.id" class="skill-card">
          <div class="skill-header">
            <van-image :src="skill.image" class="skill-image" />
            <div class="skill-info">
              <h3>{{ skill.title }}</h3>
              <p class="category">{{ skill.category }}</p>
              <p class="price">¥{{ skill.price }} / {{ skill.unit }}</p>
            </div>
            <div :class="['status-tag', getStatusClass(skill.status)]">
              {{ getStatusText(skill.status) }}
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
              <span class="num">{{ skill.rating }}</span>
              <span class="label">评分</span>
            </div>
          </div>
          
          <div class="skill-actions">
            <van-button 
              v-if="skill.status === 1"
              type="primary" 
              size="small" 
              plain
              @click="editSkill(skill)"
            >
              编辑
            </van-button>
            <van-button 
              v-if="skill.status === 1"
              type="warning" 
              size="small" 
              plain
              @click="toggleOffline(skill)"
            >
              下架
            </van-button>
            <van-button 
              v-if="skill.status === 2"
              type="success" 
              size="small" 
              plain
              @click="republish(skill)"
            >
              重新上架
            </van-button>
          </div>
        </div>

        <div v-if="filteredSkills.length === 0" class="empty-state">
          <van-icon name="service-o" size="48px" color="#ccc" />
          <p>暂无相关技能</p>
        </div>
      </div>
    </div>

    <div class="fab" @click="goToPublish">
      <van-icon name="plus" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

interface Skill {
  id: number
  title: string
  category: string
  price: string
  unit: string
  image: string
  status: number
  views: number
  orders: number
  rating: number
}

const router = useRouter()
const activeTab = ref(0)

const skills = ref<Skill[]>([
  {
    id: 1,
    title: '专业家政清洁',
    category: '家政服务',
    price: '100',
    unit: '小时',
    image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
    status: 1,
    views: 156,
    orders: 23,
    rating: 4.8
  },
  {
    id: 2,
    title: '高级瑜伽私教',
    category: '技能陪练',
    price: '200',
    unit: '小时',
    image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
    status: 1,
    views: 89,
    orders: 12,
    rating: 5.0
  },
  {
    id: 3,
    title: 'Python编程教学',
    category: '教育培训',
    price: '150',
    unit: '小时',
    image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
    status: 0,
    views: 45,
    orders: 0,
    rating: 0
  },
  {
    id: 4,
    title: '宠物美容护理',
    category: '家政服务',
    price: '80',
    unit: '次',
    image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
    status: 2,
    views: 234,
    orders: 45,
    rating: 4.6
  }
])

const publishedCount = computed(() => skills.value.filter(s => s.status === 1).length)
const pendingCount = computed(() => skills.value.filter(s => s.status === 0).length)
const offlineCount = computed(() => skills.value.filter(s => s.status === 2).length)

const filteredSkills = computed(() => {
  return skills.value.filter(skill => skill.status === activeTab.value)
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

const editSkill = (skill: Skill) => {
  showToast('编辑功能开发中')
}

const toggleOffline = (skill: Skill) => {
  skill.status = skill.status === 1 ? 2 : 1
  showToast(skill.status === 2 ? '已下架' : '已上架')
}

const republish = (skill: Skill) => {
  skill.status = 0
  showToast('已重新提交审核')
}
</script>

<style scoped>
.my-skills-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-top: 46px;
}

.content {
  padding: 10px;
}

.tabs {
  display: flex;
  background: white;
  border-radius: 12px;
  margin-bottom: 15px;
  overflow: hidden;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 15px;
  font-size: 14px;
  color: #666;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.tab-item.active {
  color: #667eea;
  border-bottom-color: #667eea;
  font-weight: 600;
}

.skills-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.skill-card {
  background: white;
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.skill-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 12px;
}

.skill-image {
  width: 70px;
  height: 70px;
  border-radius: 8px;
}

.skill-info {
  flex: 1;
}

.skill-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.skill-info .category {
  font-size: 12px;
  color: #999;
  margin: 0 0 4px 0;
}

.skill-info .price {
  font-size: 16px;
  font-weight: 600;
  color: #ff976a;
  margin: 0;
}

.status-tag {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 4px;
}

.status-tag.pending {
  background: #fff7e6;
  color: #ff976a;
}

.status-tag.published {
  background: #e6f7ff;
  color: #1890ff;
}

.status-tag.offline {
  background: #f5f5f5;
  color: #999;
}

.skill-stats {
  display: flex;
  justify-content: space-around;
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 12px;
}

.stat {
  text-align: center;
}

.stat .num {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.stat .label {
  font-size: 12px;
  color: #999;
  margin-left: 4px;
}

.skill-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state p {
  margin-top: 10px;
}

.fab {
  position: fixed;
  right: 20px;
  bottom: 80px;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.fab .van-icon {
  font-size: 28px;
  color: white;
}
</style>
