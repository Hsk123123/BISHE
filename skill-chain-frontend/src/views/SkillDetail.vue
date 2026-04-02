<template>
  <div class="skill-detail-container">
    <van-nav-bar
      title="技能详情"
      left-arrow
      fixed
      @click-left="goBack"
    />

    <section class="detail-hero">
      <div class="hero-content">
        <div class="hero-badge">Skill Detail</div>
        <h1>{{ skill.title || '技能详情' }}</h1>
        <p>查看服务介绍、时间安排和预约信息，快速完成下单预约</p>
      </div>
      <div class="hero-glow hero-glow-1"></div>
      <div class="hero-glow hero-glow-2"></div>
    </section>

    <section class="content-section">
      <div class="section-card skill-overview-card">
        <van-image
          :src="skill.image"
          fit="cover"
          class="skill-image"
        />

        <div class="skill-info">
          <div class="title-row">
            <h2 class="skill-title">{{ skill.title }}</h2>
            <div class="skill-price">
              <span class="price">¥{{ skill.price }}</span>
              <span class="unit">/ {{ skill.unit }}</span>
            </div>
          </div>

          <div class="meta-row">
            <span class="meta-pill">
              <van-icon name="user-o" />
              {{ skill.provider }}
            </span>
            <span class="meta-pill light">
              <van-icon name="location-o" />
              {{ skill.location || '线上/待确认' }}
            </span>
          </div>
        </div>
      </div>

      <div class="section-card">
        <div class="section-title">服务详情</div>
        <p class="description">{{ skill.description || '暂无详细描述' }}</p>
      </div>

      <div class="section-card">
        <div class="section-title">技能简介</div>
        <div class="introduction-content">
          <p>{{ skill.introduction || '暂无技能简介' }}</p>
        </div>

        <div
          v-if="skill.serviceContent && skill.serviceContent.length > 0"
          class="sub-section"
        >
          <h4>服务内容</h4>
          <ul class="service-list">
            <li v-for="(item, index) in skill.serviceContent" :key="index">
              {{ item }}
            </li>
          </ul>
        </div>

        <div v-if="skill.notice" class="notice-box">
          <h4>注意事项</h4>
          <p>{{ skill.notice }}</p>
        </div>
      </div>

      <div class="section-card">
        <div class="section-title">可用时间段</div>
        <div class="time-slots" v-if="skill.timeSlots.length > 0">
          <van-tag
            v-for="slot in skill.timeSlots"
            :key="slot.id"
            :type="slot.available ? 'primary' : 'default'"
            round
            size="large"
            class="time-slot"
          >
            {{ slot.time }}
          </van-tag>
        </div>
        <div v-else class="empty-inline">请选择日期后查看可用时段</div>
      </div>

      <div class="section-card" v-if="skill.reviews && skill.reviews.length > 0">
        <div class="section-title">用户评价</div>
        <div class="reviews">
          <div v-for="review in skill.reviews" :key="review.id" class="review-item">
            <div class="review-header">
              <div class="review-left">
                <van-image :src="review.avatar" round class="review-avatar" />
                <div class="review-user">
                  <span class="review-name">{{ review.name }}</span>
                  <van-rate :model-value="review.rating" readonly size="14px" />
                </div>
              </div>
              <span class="review-date">{{ review.date }}</span>
            </div>
            <p class="review-content">{{ review.content }}</p>
          </div>
        </div>
      </div>
    </section>

    <div class="bottom-action">
      <van-button
        type="primary"
        icon="calendar"
        class="action-btn primary"
        round
        @click="showBookingPopup"
      >
        立即预约
      </van-button>
    </div>

    <van-popup
      v-model:show="showBooking"
      position="bottom"
      round
      :style="{ height: '85%' }"
    >
      <div class="booking-popup">
        <div class="popup-header">
          <div>
            <h2>选择预约时间</h2>
            <p>确认服务日期、时间与时长</p>
          </div>
          <van-icon name="cross" @click="showBooking = false" />
        </div>

        <div class="booking-content">
          <div class="provider-card">
            <van-image :src="skill.image" class="provider-avatar" />
            <div class="provider-detail">
              <h3>{{ skill.title }}</h3>
              <p>¥{{ skill.price }} / {{ skill.unit }}</p>
              <p class="provider-name">{{ skill.provider }}</p>
            </div>
          </div>

          <div class="booking-section">
            <h4>选择日期</h4>

            <div class="calendar-wrapper">
              <van-calendar
                :show="showCalendar"
                :min-date="minDate"
                :max-date="maxDate"
                :show-confirm="false"
                @confirm="onCalendarConfirm"
                @close="showCalendar = false"
              />
            </div>

            <div
              class="selected-date"
              v-if="selectedDate"
              @click="showCalendar = true"
            >
              <van-icon name="checked" color="#1989fa" />
              <span>已选择：{{ formatDate(selectedDate) }}</span>
              <van-icon name="edit" color="#1989fa" size="14px" />
            </div>

            <van-button
              v-else
              type="primary"
              plain
              size="small"
              round
              @click="showCalendar = true"
              icon="calendar-o"
            >
              选择日期
            </van-button>
          </div>

          <div class="booking-section" v-if="selectedDate">
            <h4>选择时间段</h4>

            <div v-if="scheduleLoading" class="inline-loading">
              <van-loading size="20px" />
              <span>加载时间段中...</span>
            </div>

            <div v-else class="time-selector">
              <div
                v-for="slot in availableSlots"
                :key="slot.id"
                :class="['time-option', { active: selectedTimeSlot === slot.id, disabled: !slot.available }]"
                @click="slot.available && (selectedTimeSlot = slot.id)"
              >
                {{ slot.time }}
                <van-icon
                  v-if="!slot.available"
                  name="close"
                  size="12px"
                  color="#999"
                />
              </div>
            </div>

            <p class="time-hint" v-if="!selectedTimeSlot && !scheduleLoading">
              请选择可用时间段
            </p>
          </div>

          <div class="booking-section">
            <h4>服务时长</h4>
            <div class="duration-group">
              <van-radio-group v-model="selectedDuration" direction="horizontal">
                <van-radio :name="1">1 {{ skill.unit }}</van-radio>
                <van-radio :name="2">2 {{ skill.unit }}</van-radio>
                <van-radio :name="3">3 {{ skill.unit }}</van-radio>
              </van-radio-group>
            </div>
          </div>

          <div class="booking-section">
            <h4>备注信息</h4>
            <van-field
              v-model="remark"
              type="textarea"
              placeholder="请输入您的特殊需求（如有）"
              rows="3"
              autosize
            />
          </div>

          <div class="summary-card">
            <h4>预约信息</h4>
            <div class="summary-item">
              <span>服务</span>
              <span>{{ skill.title }}</span>
            </div>
            <div class="summary-item" v-if="selectedDate">
              <span>日期</span>
              <span>{{ formatDate(selectedDate) }}</span>
            </div>
            <div class="summary-item" v-if="selectedTimeSlot">
              <span>时间</span>
              <span>{{ getSelectedTimeSlotText() }}</span>
            </div>
            <div class="summary-item">
              <span>时长</span>
              <span>{{ selectedDuration }} {{ skill.unit }}</span>
            </div>
            <div class="summary-item total">
              <span>合计</span>
              <span class="total-price">¥{{ calculateTotal() }}</span>
            </div>
          </div>
        </div>

        <div class="popup-footer">
          <div class="footer-price">
            <span>应付：</span>
            <span class="price">¥{{ calculateTotal() }}</span>
          </div>
          <van-button
            type="primary"
            round
            @click="submitOrder"
            :disabled="!canSubmit"
            :loading="isSubmitting"
          >
            {{ canSubmit ? '确认预约' : '请选择日期和时间' }}
          </van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showSuccessToast, showDialog } from 'vant'
import dayjs from 'dayjs'
import { getSkillDetail, getSkillSchedule } from '@/api/skill'
import { createOrder } from '@/api/order'

interface TimeSlot {
  id: number
  scheduleId?: number
  time: string
  available: boolean
}

interface Review {
  id: number
  name: string
  avatar: string
  rating: number
  date: string
  content: string
}

interface Skill {
  id: number
  skillId?: number
  title: string
  description: string
  introduction?: string
  price: number
  unit: string
  provider: string
  location: string
  image: string
  serviceContent?: string[]
  notice?: string
  timeSlots: TimeSlot[]
  reviews?: Review[]
}

interface SkillDetailResponse {
  skillId?: number
  title?: string
  description?: string
  pricePerUnit?: number
  unitType?: number
  mediaUrls?: string
  providerId?: number
  providerName?: string
}

interface RawSchedule {
  scheduleId?: number
  timeSlot?: string
  status?: number
  location?: string
}

interface ApiError {
  response?: {
    data?: {
      message?: string
    }
  }
  message?: string
}

const router = useRouter()
const route = useRoute()

const unitMap: Record<number, string> = {
  1: '小时',
  2: '次',
  3: '单',
  4: '月'
}

const defaultImage = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'

const skill = ref<Skill>({
  id: 0,
  title: '',
  description: '',
  introduction: '',
  price: 0,
  unit: '次',
  provider: '服务者',
  location: '',
  image: defaultImage,
  serviceContent: [],
  notice: '',
  timeSlots: [],
  reviews: []
})

const skillLoading = ref(true)
const showBooking = ref(false)
const showCalendar = ref(false)
const selectedDate = ref<Date | null>(null)
const selectedTimeSlot = ref<number | null>(null)
const selectedDuration = ref(1)
const remark = ref('')
const isSubmitting = ref(false)
const scheduleLoading = ref(false)

const minDate = new Date()
const maxDate = new Date(Date.now() + 30 * 24 * 60 * 60 * 1000)

const availableSlots = computed(() => skill.value.timeSlots)

const canSubmit = computed(() => {
  return selectedDate.value !== null && selectedTimeSlot.value !== null
})

const loadSkill = async () => {
  const idParam = route.params.id
  const id = Array.isArray(idParam) ? idParam[0] : idParam
  if (!id) return

  skillLoading.value = true
  try {
    const data = await getSkillDetail(id) as SkillDetailResponse

    skill.value = {
      id: data?.skillId ?? 0,
      skillId: data?.skillId,
      title: data?.title ?? '',
      description: data?.description ?? '',
      introduction: data?.description ?? '',
      price: Number(data?.pricePerUnit ?? 0),
      unit: unitMap[Number(data?.unitType)] ?? '次',
      provider: data?.providerName ?? '服务者',
      location: '',
      image:
        (typeof data?.mediaUrls === 'string' && data.mediaUrls
          ? data.mediaUrls.split(',')[0]
          : '') || defaultImage,
      serviceContent: [],
      notice: '',
      timeSlots: [],
      reviews: []
    }
  } catch (err) {
    console.error('[skill-detail] loadSkill error =', err)
    showToast('加载失败')
  } finally {
    skillLoading.value = false
  }
}

watch(selectedDate, async (date) => {
  if (!date || !skill.value.skillId) return

  scheduleLoading.value = true
  skill.value.timeSlots = []

  try {
    const dateStr = dayjs(date).format('YYYY-MM-DD')
    const slots = await getSkillSchedule(skill.value.skillId, dateStr) as RawSchedule[]

    console.log('[skill-detail] schedule response =', slots)

    skill.value.timeSlots = (slots ?? []).map((s, i) => ({
      id: s.scheduleId ?? i + 1,
      scheduleId: s.scheduleId,
      time: s.timeSlot ?? '',
      available: (s.status ?? 0) === 0
    }))

    if (!skill.value.location) {
      const firstLocation = (slots ?? []).find((s) => s.location)?.location
      if (firstLocation) {
        skill.value.location = firstLocation
      }
    }

    selectedTimeSlot.value = null
  } catch (err) {
    console.error('[skill-detail] load schedule error =', err)
    skill.value.timeSlots = []
  } finally {
    scheduleLoading.value = false
  }
})

const goBack = () => {
  router.back()
}

const formatDate = (date: Date) => {
  return dayjs(date).format('YYYY年MM月DD日')
}

const formatDateApi = (date: Date) => {
  return dayjs(date).format('YYYY-MM-DD')
}

const getSelectedTimeSlotText = () => {
  const slot = skill.value.timeSlots.find((s) => s.id === selectedTimeSlot.value)
  return slot ? slot.time : ''
}

const getSelectedTimeSlotValue = () => {
  const slot = skill.value.timeSlots.find((s) => s.id === selectedTimeSlot.value)
  return slot ? slot.time : ''
}

const calculateTotal = () => {
  return skill.value.price * selectedDuration.value
}

const showBookingPopup = () => {
  showBooking.value = true
}

const onCalendarConfirm = (value: Date | Date[]) => {
  const date = Array.isArray(value) ? value[0] : value
  selectedDate.value = date
  showCalendar.value = false
}

const submitOrder = async () => {
  if (!canSubmit.value || !selectedDate.value) {
    showToast('请选择预约日期和时间')
    return
  }

  isSubmitting.value = true
  try {
    const confirmed = await showDialog({
      title: '确认预约',
      message: `确认预约 ${skill.value.title}？\n\n日期：${formatDate(selectedDate.value)}\n时间：${getSelectedTimeSlotText()}\n时长：${selectedDuration.value} ${skill.value.unit}\n合计：¥${calculateTotal()}`,
      confirmButtonText: '确认支付',
      cancelButtonText: '取消'
    })
      .then(() => true)
      .catch(() => false)

    if (!confirmed) return

    const payload = {
      skillId: skill.value.skillId ?? skill.value.id,
      scheduleDate: formatDateApi(selectedDate.value),
      timeSlot: getSelectedTimeSlotValue()
    }

    console.log('[skill-detail] create order payload =', payload)

    await createOrder(payload)

    showSuccessToast('预约成功！订单已提交')
    showBooking.value = false
    router.push('/orders')
  } catch (err) {
    console.error('[skill-detail] submitOrder error =', err)
    const error = err as ApiError
    const backendMessage =
      error?.response?.data?.message || error?.message || '预约失败'
    showToast(backendMessage)
  } finally {
    isSubmitting.value = false
  }
}

onMounted(() => {
  loadSkill()
})
</script>

<style scoped>
.skill-detail-container {
  min-height: 100vh;
  padding-top: 46px;
  padding-bottom: 74px;
  background:
    radial-gradient(circle at top right, rgba(102, 126, 234, 0.12), transparent 28%),
    linear-gradient(180deg, #f6f8fc 0%, #eef2f7 100%);
}

.detail-hero {
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

.detail-hero h1 {
  margin: 0 0 8px;
  font-size: 25px;
  font-weight: 700;
  line-height: 1.3;
}

.detail-hero p {
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

.section-card {
  margin-bottom: 12px;
  padding: 16px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 10px 24px rgba(31, 45, 61, 0.05);
}

.skill-overview-card {
  padding: 0;
  overflow: hidden;
}

.skill-image {
  width: 100%;
  height: 220px;
}

.skill-info {
  padding: 16px;
}

.title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 12px;
}

.skill-title {
  margin: 0;
  flex: 1;
  font-size: 22px;
  font-weight: 700;
  line-height: 1.35;
  color: #1f2330;
}

.skill-price {
  flex-shrink: 0;
  text-align: right;
}

.skill-price .price {
  font-size: 24px;
  font-weight: 700;
  color: #ff976a;
}

.skill-price .unit {
  font-size: 13px;
  color: #9aa1b3;
}

.meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.meta-pill {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 10px;
  font-size: 12px;
  color: #667eea;
  background: rgba(102, 126, 234, 0.08);
  border-radius: 999px;
}

.meta-pill.light {
  color: #6f7688;
  background: #eef1f6;
}

.section-title {
  margin-bottom: 12px;
  font-size: 16px;
  font-weight: 700;
  color: #1f2330;
}

.description,
.introduction-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.8;
  color: #5f6678;
}

.sub-section {
  margin-top: 16px;
}

.sub-section h4,
.notice-box h4 {
  margin: 0 0 10px;
  font-size: 14px;
  font-weight: 700;
  color: #2a3142;
}

.service-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.service-list li {
  position: relative;
  padding: 8px 0 8px 18px;
  font-size: 14px;
  line-height: 1.7;
  color: #5f6678;
}

.service-list li::before {
  content: '';
  position: absolute;
  left: 0;
  top: 16px;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #667eea;
}

.notice-box {
  margin-top: 16px;
  padding: 14px;
  border-radius: 16px;
  background: #fff7e9;
}

.notice-box p {
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  color: #6a6170;
}

.time-slots {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.time-slot {
  margin: 0;
}

.empty-inline {
  font-size: 13px;
  color: #98a0b3;
}

.reviews {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.review-item {
  padding-bottom: 14px;
  border-bottom: 1px solid #eef1f6;
}

.review-item:last-child {
  padding-bottom: 0;
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 10px;
}

.review-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.review-avatar {
  width: 42px;
  height: 42px;
}

.review-user {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.review-name {
  font-size: 14px;
  font-weight: 600;
  color: #2a3142;
}

.review-date {
  font-size: 12px;
  color: #98a0b3;
}

.review-content {
  margin: 0;
  font-size: 14px;
  line-height: 1.7;
  color: #5f6678;
}

.bottom-action {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 12px 14px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 -6px 20px rgba(31, 45, 61, 0.08);
  backdrop-filter: blur(12px);
}

.action-btn {
  width: 100%;
  height: 44px;
}

.action-btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #7b61ff 100%);
  border: none;
}

.booking-popup {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f7f9fd;
}

.popup-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 16px 16px 14px;
  background: #fff;
  border-bottom: 1px solid #eef1f6;
}

.popup-header h2 {
  margin: 0 0 4px;
  font-size: 18px;
  font-weight: 700;
  color: #1f2330;
}

.popup-header p {
  margin: 0;
  font-size: 12px;
  color: #8a90a2;
}

.booking-content {
  flex: 1;
  overflow-y: auto;
  padding: 14px 14px 6px;
}

.provider-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px;
  margin-bottom: 16px;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 8px 20px rgba(31, 45, 61, 0.04);
}

.provider-avatar {
  width: 64px;
  height: 64px;
  border-radius: 14px;
  overflow: hidden;
  flex-shrink: 0;
}

.provider-detail h3 {
  margin: 0 0 5px;
  font-size: 16px;
  font-weight: 700;
  color: #1f2330;
}

.provider-detail p {
  margin: 0;
  font-size: 13px;
  line-height: 1.6;
  color: #7a8193;
}

.provider-name {
  color: #667eea !important;
}

.booking-section {
  padding: 14px;
  margin-bottom: 12px;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 8px 20px rgba(31, 45, 61, 0.04);
}

.booking-section h4 {
  margin: 0 0 14px;
  font-size: 15px;
  font-weight: 700;
  color: #1f2330;
}

.calendar-wrapper {
  margin: 8px 0;
}

.selected-date {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  padding: 10px 12px;
  border-radius: 12px;
  background: #edf5ff;
  color: #1989fa;
}

.inline-loading {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #8a90a2;
  font-size: 13px;
}

.time-selector {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.time-option {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  min-height: 44px;
  padding: 10px;
  text-align: center;
  font-size: 13px;
  color: #2a3142;
  border: 1px solid #dbe2ee;
  border-radius: 12px;
  transition: all 0.25s ease;
}

.time-option.active {
  color: #fff;
  background: linear-gradient(135deg, #667eea 0%, #7b61ff 100%);
  border-color: transparent;
}

.time-option.disabled {
  color: #c0c5d0;
  background: #f4f6fb;
  border-color: #eef1f6;
}

.time-hint {
  margin: 10px 0 0;
  font-size: 12px;
  color: #98a0b3;
  text-align: center;
}

.duration-group {
  padding-top: 4px;
}

.summary-card {
  padding: 14px;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 8px 20px rgba(31, 45, 61, 0.04);
}

.summary-card h4 {
  margin: 0 0 14px;
  font-size: 15px;
  font-weight: 700;
  color: #1f2330;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding: 8px 0;
  font-size: 14px;
  color: #5f6678;
}

.summary-item.total {
  margin-top: 8px;
  padding-top: 14px;
  border-top: 1px solid #eef1f6;
  font-weight: 700;
}

.total-price {
  font-size: 20px;
  color: #ff976a;
}

.popup-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 15px;
  background: #fff;
  border-top: 1px solid #eef1f6;
}

.footer-price {
  font-size: 14px;
  color: #667085;
}

.footer-price .price {
  font-size: 24px;
  font-weight: 700;
  color: #ff976a;
}

.popup-footer :deep(.van-button) {
  min-width: 152px;
  background: linear-gradient(135deg, #667eea 0%, #7b61ff 100%);
  border: none;
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