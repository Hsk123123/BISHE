<template>
  <div class="fav-container">
    <van-nav-bar title="我的收藏" left-arrow fixed @click-left="router.back()" />

    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="loadMore"
      >
        <div class="skill-list" v-if="skills.length > 0">
          <div
            v-for="skill in skills"
            :key="skill.skillId"
            class="skill-card"
            @click="goToSkill(skill.skillId!)"
          >
            <van-image
              :src="getImage(skill.mediaUrls)"
              fit="cover"
              class="card-img"
            />
            <div class="card-body">
              <div class="card-title">{{ skill.title }}</div>
              <div class="card-meta">
                <span class="price">¥{{ skill.pricePerUnit }}</span>
                <span class="unit">/ {{ unitText(skill.unitType) }}</span>
              </div>
              <van-button
                size="mini"
                plain
                type="danger"
                round
                @click.stop="cancelFav(skill.skillId!)"
              >取消收藏</van-button>
            </div>
          </div>
        </div>

        <van-empty v-if="!loading && skills.length === 0" description="暂无收藏" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getFavoriteList, removeFavorite } from '@/api/favorite'

interface SkillItem {
  skillId?: number
  title?: string
  pricePerUnit?: number
  unitType?: number
  mediaUrls?: string
}

const router = useRouter()
const skills = ref<SkillItem[]>([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
let page = 1

const unitMap: Record<number, string> = { 1: '小时', 2: '次', 3: '单', 4: '月' }
const unitText = (t?: number) => unitMap[t ?? 2] ?? '次'
const getImage = (urls?: string) =>
  urls ? urls.split(',')[0] : 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'

const loadMore = async () => {
  if (loading.value || finished.value) return
  loading.value = true
  try {
    const data = await getFavoriteList({ page, size: 20 }) as any
    const list: SkillItem[] = data?.records ?? []
    skills.value.push(...list)
    page++
    if (list.length < 20) finished.value = true
  } catch {
    finished.value = true
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

const onRefresh = () => {
  skills.value = []
  page = 1
  finished.value = false
  loadMore()
}

const cancelFav = async (skillId: number) => {
  try {
    await removeFavorite(skillId)
    skills.value = skills.value.filter(s => s.skillId !== skillId)
    showToast('已取消收藏')
  } catch { /* interceptor */ }
}

const goToSkill = (id: number) => router.push(`/skill/${id}`)
</script>

<style scoped>
.fav-container {
  padding-top: 46px;
  min-height: 100vh;
  background: #f5f5f5;
}
.skill-list {
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.skill-card {
  display: flex;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.card-img {
  width: 100px;
  height: 100px;
  flex-shrink: 0;
}
.card-body {
  flex: 1;
  padding: 12px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #323233;
  line-height: 1.4;
  margin-bottom: 6px;
}
.card-meta {
  margin-bottom: 8px;
}
.price {
  font-size: 16px;
  font-weight: bold;
  color: #ff976a;
}
.unit {
  font-size: 12px;
  color: #969799;
  margin-left: 2px;
}
</style>
