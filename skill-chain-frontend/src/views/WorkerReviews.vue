<template>
  <div class="reviews-container">
    <van-nav-bar title="评价管理" left-arrow @click-left="router.back()" fixed />

    <div class="page-body">
      <div v-if="loading" class="loading-wrap">
        <van-loading size="24" vertical>加载中...</van-loading>
      </div>

      <van-pull-refresh v-else v-model="refreshing" @refresh="onRefresh">
        <div v-if="reviews.length === 0" class="empty-wrap">
          <van-empty description="暂无评价" />
        </div>

        <div v-else class="review-list">
          <div v-for="item in reviews" :key="item.reviewId" class="review-card">
            <div class="card-header">
              <span class="skill-name">{{ item.skillTitle }}</span>
              <van-rate :model-value="item.rating" readonly size="14px" color="#ff976a" />
            </div>
            <p class="review-content">{{ item.content || '用户未填写评价内容' }}</p>
            <div class="card-footer">
              <span class="review-time">{{ formatTime(item.createTime) }}</span>
              <van-tag :type="ratingType(item.rating)" size="small">
                {{ item.rating }}星
              </van-tag>
            </div>
          </div>
        </div>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { useUserStore } from '@/store/user'
import { getMySkills } from '@/api/skill'
import { getReviewsBySkill } from '@/api/review'

interface ReviewItem {
  reviewId: number
  skillTitle: string
  rating: number
  content: string
  createTime: string
}

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const refreshing = ref(false)
const reviews = ref<ReviewItem[]>([])

const loadReviews = async () => {
  loading.value = true
  try {
    const userId = (userStore.userInfo as any)?.userId
    if (!userId) return

    const skills = await getMySkills(userId) as any[]
    if (!skills?.length) return

    const allReviews: ReviewItem[] = []
    await Promise.all(
      skills.map(async (skill: any) => {
        try {
          const res = await getReviewsBySkill(skill.skillId, { page: 1, size: 50 }) as any
          const records = res?.records ?? res ?? []
          for (const r of records) {
            allReviews.push({
              reviewId: r.reviewId ?? Math.random(),
              skillTitle: skill.title ?? '技能服务',
              rating: r.rating ?? 5,
              content: r.content ?? '',
              createTime: r.createTime ?? ''
            })
          }
        } catch {
          // 单个技能查询失败不影响整体
        }
      })
    )

    allReviews.sort((a, b) => b.createTime.localeCompare(a.createTime))
    reviews.value = allReviews
  } catch {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

const onRefresh = async () => {
  await loadReviews()
  refreshing.value = false
}

const formatTime = (t: string) => (t ? t.substring(0, 10) : '-')

const ratingType = (r: number) => {
  if (r >= 4) return 'success'
  if (r >= 3) return 'warning'
  return 'danger'
}

onMounted(loadReviews)
</script>

<style scoped>
.reviews-container {
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

.empty-wrap {
  padding: 24px 0;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.review-card {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 14px rgba(31, 45, 61, 0.06);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.skill-name {
  font-size: 14px;
  font-weight: 600;
  color: #667eea;
  flex: 1;
  margin-right: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.review-content {
  margin: 0 0 10px;
  font-size: 14px;
  line-height: 1.7;
  color: #3a4156;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.review-time {
  font-size: 12px;
  color: #98a0b3;
}
</style>
