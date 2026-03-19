<template>
  <div class="skill-detail-container">
    <van-nav-bar 
      title="技能详情" 
      left-arrow 
      fixed 
      @click-left="goBack"
    />
    
    <div class="content">
      <div class="skill-header">
        <van-image 
          :src="skill.image" 
          fit="cover"
          class="skill-image"
        />
        <div class="skill-info">
          <h1 class="skill-title">{{ skill.title }}</h1>
          <div class="skill-price">
            <span class="price">¥{{ skill.price }}</span>
            <span class="unit">/ {{ skill.unit }}</span>
          </div>
          <div class="skill-meta">
            <span class="provider">
              <van-icon name="user-o" /> {{ skill.provider }}
            </span>
            <span class="location">
              <van-icon name="location-o" /> {{ skill.location }}
            </span>
          </div>
        </div>
      </div>

      <div class="skill-section">
        <h3>服务详情</h3>
        <p class="description">{{ skill.description }}</p>
      </div>

      <div class="skill-section">
        <h3>技能简介</h3>
        <div class="introduction-content">
          <p>{{ skill.introduction }}</p>
        </div>
        
        <div v-if="skill.serviceContent && skill.serviceContent.length > 0" class="service-content">
          <h4>服务内容</h4>
          <ul>
            <li v-for="(item, index) in skill.serviceContent" :key="index">{{ item }}</li>
          </ul>
        </div>
        
        <div v-if="skill.notice" class="service-notice">
          <h4>注意事项</h4>
          <p>{{ skill.notice }}</p>
        </div>
      </div>

      <div class="skill-section">
        <h3>可用时间段</h3>
        <div class="time-slots">
          <van-tag 
            v-for="slot in skill.timeSlots" 
            :key="slot.id"
            :type="slot.available ? 'primary' : 'default'"
            size="large"
            class="time-slot"
          >
            {{ slot.time }}
          </van-tag>
        </div>
      </div>

      <div class="skill-section" v-if="skill.reviews && skill.reviews.length > 0">
        <h3>用户评价</h3>
        <div class="reviews">
          <div v-for="review in skill.reviews" :key="review.id" class="review-item">
            <div class="review-header">
              <van-image :src="review.avatar" round class="review-avatar" />
              <div class="review-user">
                <span class="review-name">{{ review.name }}</span>
                <van-rate :model-value="review.rating" readonly size="14px" />
              </div>
              <span class="review-date">{{ review.date }}</span>
            </div>
            <p class="review-content">{{ review.content }}</p>
          </div>
        </div>
      </div>
    </div>

    <div class="bottom-action">
      <van-button type="primary" icon="calendar" class="action-btn primary" @click="showBookingPopup">
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
          <h2>选择预约时间</h2>
          <van-icon name="cross" @click="showBooking = false" />
        </div>
        
        <div class="booking-content">
          <div class="provider-info">
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
            <div class="selected-date" v-if="selectedDate" @click="showCalendar = true">
              <van-icon name="checked" color="#1989fa" />
              <span>已选择：{{ formatDate(selectedDate) }}</span>
              <van-icon name="edit" color="#1989fa" size="14px" />
            </div>
            <van-button 
              v-else 
              type="primary" 
              plain 
              size="small" 
              @click="showCalendar = true"
              icon="calendar-o"
            >
              选择日期
            </van-button>
          </div>

          <div class="booking-section" v-if="selectedDate">
            <h4>选择时间段</h4>
            <div class="time-selector">
              <div 
                v-for="slot in availableSlots" 
                :key="slot.id"
                :class="['time-option', { active: selectedTimeSlot === slot.id, disabled: !slot.available }]"
                @click="slot.available && (selectedTimeSlot = slot.id)"
              >
                {{ slot.time }}
                <van-icon v-if="!slot.available" name="close" size="12px" color="#999" />
              </div>
            </div>
            <p class="time-hint" v-if="!selectedTimeSlot">请选择可用时间段</p>
          </div>

          <div class="booking-section">
            <h4>服务时长</h4>
            <van-radio-group v-model="selectedDuration" direction="horizontal">
              <van-radio :name="1">1 {{ skill.unit }}</van-radio>
              <van-radio :name="2">2 {{ skill.unit }}</van-radio>
              <van-radio :name="3">3 {{ skill.unit }}</van-radio>
            </van-radio-group>
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

          <div class="booking-summary">
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

const router = useRouter()
const route = useRoute()

const skill = ref<Skill>({
  id: 0,
  title: '',
  description: '',
  introduction: '',
  price: 0,
  unit: '次',
  provider: '服务者',
  location: '',
  image: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
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
  const id = route.params.id
  if (!id) return
  skillLoading.value = true
  try {
    const data = await getSkillDetail(id) as { skillId?: number; title?: string; description?: string; pricePerUnit?: number; unitType?: number; mediaUrls?: string; providerId?: number }
    skill.value = {
      id: data?.skillId ?? 0,
      skillId: data?.skillId,
      title: data?.title ?? '',
      description: data?.description ?? '',
      introduction: data?.description ?? '',
      price: Number(data?.pricePerUnit ?? 0),
      unit: (data?.unitType === 1 ? '次' : data?.unitType === 2 ? '小时' : '次'),
      provider: '服务者',
      location: '',
      image: (data?.mediaUrls && (typeof data.mediaUrls === 'string' ? data.mediaUrls.split(',')[0] : '')) || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
      serviceContent: [],
      notice: '',
      timeSlots: [],
      reviews: []
    }
  } catch {
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
    const slots = await getSkillSchedule(skill.value.skillId, dateStr) as Array<{ scheduleId?: number; timeSlot?: string; status?: number }>
    skill.value.timeSlots = (slots ?? []).map((s: any, i: number) => ({
      id: s.scheduleId ?? i + 1,
      scheduleId: s.scheduleId,
      time: s.timeSlot ?? '',
      available: (s.status ?? 0) === 0
    }))
    selectedTimeSlot.value = null
  } catch {
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
  const slot = skill.value.timeSlots.find(s => s.id === selectedTimeSlot.value)
  return slot ? slot.time : ''
}

const getSelectedTimeSlotValue = () => {
  const slot = skill.value.timeSlots.find(s => s.id === selectedTimeSlot.value)
  return slot ? slot.time : ''
}

const calculateTotal = () => {
  return skill.value.price * selectedDuration.value
}

const showBookingPopup = () => {
  showBooking.value = true
}

const onCalendarConfirm = (date: Date) => {
  selectedDate.value = date
  showCalendar.value = false
}

const submitOrder = async () => {
  if (!canSubmit.value) {
    showToast('请选择预约日期和时间')
    return
  }

  isSubmitting.value = true
  try {
    const confirmed = await showDialog({
      title: '确认预约',
      message: `确认预约 ${skill.value.title}？\n\n日期：${formatDate(selectedDate.value!)}\n时间：${getSelectedTimeSlotText()}\n时长：${selectedDuration.value} ${skill.value.unit}\n合计：¥${calculateTotal()}`,
      confirmButtonText: '确认支付',
      cancelButtonText: '取消'
    }).then(() => true).catch(() => false)

    if (confirmed) {
      await createOrder({
        skillId: skill.value.skillId ?? skill.value.id,
        scheduleDate: formatDateApi(selectedDate.value!),
        timeSlot: getSelectedTimeSlotValue()
      })
      showSuccessToast('预约成功！订单已提交')
      showBooking.value = false
      router.push('/orders')
    }
  } catch (err) {
    showToast((err as Error)?.message || '预约失败')
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
  background: #f5f5f5;
  padding-top: 46px;
  padding-bottom: 60px;
}

.content {
  padding: 15px;
}

.skill-header {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 10px;
}

.skill-image {
  width: 100%;
  height: 200px;
}

.skill-info {
  padding: 15px;
}

.skill-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px;
}

.skill-price {
  margin-bottom: 10px;
}

.skill-price .price {
  font-size: 24px;
  font-weight: 700;
  color: #ff6b6b;
}

.skill-price .unit {
  font-size: 14px;
  color: #999;
}

.skill-meta {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 14px;
}

.skill-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.skill-section {
  background: white;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 10px;
}

.skill-section h3 {
  font-size: 16px;
  color: #333;
  margin: 0 0 15px;
  border-left: 3px solid #1989fa;
  padding-left: 10px;
}

.description {
  color: #666;
  line-height: 1.8;
  margin: 0;
}

.introduction-content {
  color: #666;
  line-height: 1.8;
  margin-bottom: 15px;
}

.service-content h4, .service-notice h4 {
  font-size: 14px;
  color: #333;
  margin: 15px 0 10px;
}

.service-content ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.service-content li {
  padding: 8px 0;
  padding-left: 20px;
  position: relative;
  color: #666;
  line-height: 1.6;
}

.service-content li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #1989fa;
}

.service-notice {
  background: #fff7e6;
  border-radius: 8px;
  padding: 12px;
  margin-top: 15px;
}

.service-notice h4 {
  color: #ff976a;
  margin: 0 0 8px;
}

.service-notice p {
  color: #666;
  font-size: 13px;
  line-height: 1.6;
  margin: 0;
}

.time-slots {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.time-slot {
  margin: 0;
}

.reviews {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.review-item {
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;
}

.review-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.review-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.review-avatar {
  width: 40px;
  height: 40px;
  margin-right: 10px;
}

.review-user {
  flex: 1;
}

.review-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-right: 8px;
}

.review-date {
  font-size: 12px;
  color: #999;
}

.review-content {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin: 0;
}

.bottom-action {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  padding: 10px 15px;
  background: white;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
}

.action-btn {
  flex: 1;
}

.action-btn.primary {
  margin-left: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.booking-popup {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.popup-header h2 {
  font-size: 18px;
  margin: 0;
}

.booking-content {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.provider-info {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #f8f8f8;
  border-radius: 12px;
  margin-bottom: 20px;
}

.provider-avatar {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  margin-right: 15px;
}

.provider-detail h3 {
  font-size: 16px;
  margin: 0 0 5px;
}

.provider-detail p {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.provider-name {
  color: #1989fa !important;
  margin-top: 5px !important;
}

.booking-section {
  margin-bottom: 20px;
}

.booking-section h4 {
  font-size: 15px;
  color: #333;
  margin: 0 0 15px;
}

.selected-date {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  padding: 10px;
  background: #e6f7ff;
  border-radius: 8px;
  color: #1989fa;
}

.time-selector {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.time-option {
  padding: 12px;
  text-align: center;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.time-option:hover {
  border-color: #1989fa;
}

.time-option.active {
  background: #1989fa;
  color: white;
  border-color: #1989fa;
}

.time-option.disabled {
  background: #f5f5f5;
  color: #ccc;
  cursor: not-allowed;
}

.booking-summary {
  background: #f8f8f8;
  border-radius: 12px;
  padding: 15px;
}

.booking-summary h4 {
  font-size: 15px;
  margin: 0 0 15px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  color: #666;
  font-size: 14px;
}

.summary-item.total {
  border-top: 1px solid #ddd;
  margin-top: 10px;
  padding-top: 15px;
  font-weight: 600;
}

.total-price {
  font-size: 20px;
  color: #ff6b6b;
}

.popup-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  border-top: 1px solid #eee;
  background: white;
}

.footer-price {
  font-size: 14px;
  color: #666;
}

.footer-price .price {
  font-size: 24px;
  font-weight: 700;
  color: #ff6b6b;
}

.popup-footer :deep(.van-button) {
  width: 150px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.calendar-wrapper {
  margin: 10px 0;
}

.time-hint {
  font-size: 12px;
  color: #999;
  margin: 10px 0 0;
  text-align: center;
}
</style>