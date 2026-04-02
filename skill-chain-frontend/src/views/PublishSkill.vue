<template>
  <div class="publish-skill-container">
    <van-nav-bar title="发布技能" left-arrow fixed @click-left="goBack" />

    <div class="form-content">
      <van-form>
        <div class="form-section">
          <div class="section-title">基本信息</div>

          <van-field
            v-model="form.title"
            name="title"
            label="技能名称"
            placeholder="请输入技能名称"
            :rules="[{ required: true, message: '请输入技能名称' }]"
          />

          <van-field
            v-model="form.categoryName"
            name="category"
            label="技能分类"
            placeholder="请选择技能分类"
            readonly
            is-link
            :rules="[{ required: true, message: '请选择技能分类' }]"
            @click="showCategoryPicker = true"
          />

          <van-field
            v-model.number="form.price"
            type="number"
            name="price"
            label="价格"
            placeholder="请输入价格"
            :rules="[
              { required: true, message: '请输入价格' },
              {
                validator: (value: string | number | undefined) => Number(value) > 0,
                message: '价格必须大于 0'
              }
            ]"
          >
            <template #right-icon>
              <span class="unit-text">元 / {{ form.unit }}</span>
            </template>
          </van-field>

          <van-field name="unit" label="计费单位">
            <template #input>
              <van-radio-group v-model="form.unit" direction="horizontal">
                <van-radio name="小时">小时</van-radio>
                <van-radio name="次">次</van-radio>
                <van-radio name="单">单</van-radio>
                <van-radio name="月">月</van-radio>
              </van-radio-group>
            </template>
          </van-field>

          <van-field name="serviceMode" label="服务方式">
            <template #input>
              <van-radio-group v-model="form.serviceMode" direction="horizontal">
                <van-radio :name="1">线上</van-radio>
                <van-radio :name="2">线下</van-radio>
              </van-radio-group>
            </template>
          </van-field>
        </div>

        <div class="form-section">
          <div class="section-title">技能描述</div>

          <van-field
            v-model="form.description"
            name="description"
            label="简介"
            type="textarea"
            placeholder="请描述你的技能"
            rows="4"
            maxlength="500"
            show-word-limit
          />

          <van-field
            v-model="form.notice"
            name="notice"
            label="注意事项"
            type="textarea"
            placeholder="可填写服务前说明"
            rows="3"
            maxlength="200"
            show-word-limit
          />
        </div>

        <div class="form-section">
          <div class="section-title">
            <span>可预约时间段</span>
            <span class="required-mark">*</span>
          </div>
          <p class="schedule-hint">至少添加一个可预约时间段，用户才能下单</p>

          <div
            v-for="(schedule, index) in schedules"
            :key="index"
            class="schedule-item"
          >
            <div class="schedule-header">
              <span class="schedule-index">时间段 {{ index + 1 }}</span>
              <van-button
                v-if="schedules.length > 1"
                native-type="button"
                size="mini"
                type="danger"
                plain
                round
                @click="removeSchedule(index)"
              >
                删除
              </van-button>
            </div>

            <van-field
              v-model="schedule.date"
              :name="`date-${index}`"
              label="日期"
              placeholder="点击选择日期"
              readonly
              is-link
              :rules="[{ required: true, message: '请选择日期' }]"
              @click="openDatePicker(index)"
            />

            <van-field
              v-model.trim="schedule.timeSlot"
              :name="`timeSlot-${index}`"
              label="时间段"
              placeholder="格式：09:00-10:00"
              :rules="[{ required: true, message: '请输入时间段' }]"
            >
              <template #extra>
                <span class="time-hint">HH:mm-HH:mm</span>
              </template>
            </van-field>

            <van-field
              v-model.trim="schedule.location"
              :name="`location-${index}`"
              label="地点"
              placeholder="例如：线上 或 具体地址（选填）"
            />
          </div>

          <div class="add-schedule-btn">
            <van-button
              native-type="button"
              type="primary"
              plain
              round
              size="small"
              icon="plus"
              @click="addSchedule"
            >
              添加时间段
            </van-button>
          </div>
        </div>

        <div class="submit-area">
          <van-button
            type="primary"
            block
            :loading="isSubmitting"
            loading-text="提交中..."
            @click="onSubmit"
          >
            提交审核
          </van-button>
          <p class="submit-tip">发布后需要管理员审核，审核通过后才会在列表展示</p>
        </div>
      </van-form>
    </div>

    <van-popup v-model:show="showCategoryPicker" position="bottom">
      <van-picker
        title="选择分类"
        :columns="categoryOptions"
        @confirm="onCategoryConfirm"
        @cancel="showCategoryPicker = false"
      />
    </van-popup>

    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker
        v-model="currentDatePicker"
        title="选择日期"
        :min-date="minDate"
        :max-date="maxDate"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showToast } from 'vant'
import dayjs from 'dayjs'
import { publishSkill as publishSkillApi, type ScheduleItem } from '@/api/skill'

interface FormData {
  title: string
  categoryId: string
  categoryName: string
  price?: number
  unit: string
  serviceMode: number
  description: string
  notice: string
}

interface ScheduleForm {
  date: string
  timeSlot: string
  location: string
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

const isSubmitting = ref(false)
const showCategoryPicker = ref(false)
const showDatePicker = ref(false)
const currentDateIndex = ref(0)

const minDate = new Date()
const maxDate = new Date(Date.now() + 365 * 24 * 60 * 60 * 1000)

const currentDatePicker = ref<string[]>([
  String(minDate.getFullYear()),
  String(minDate.getMonth() + 1).padStart(2, '0'),
  String(minDate.getDate()).padStart(2, '0')
])

const form = reactive<FormData>({
  title: '',
  categoryId: '',
  categoryName: '',
  price: undefined,
  unit: '小时',
  serviceMode: 2,
  description: '',
  notice: ''
})

const schedules = ref<ScheduleForm[]>([
  { date: '', timeSlot: '', location: '' }
])

const categoryOptions = [
  { text: '家政服务', value: '1' },
  { text: '技能陪练', value: '2' },
  { text: '设计服务', value: '3' },
  { text: '咨询服务', value: '4' },
  { text: '教育培训', value: '5' },
  { text: '更多服务', value: '6' }
]

const unitTypeMap: Record<string, number> = {
  小时: 1,
  次: 2,
  单: 3,
  月: 4
}

const goBack = () => {
  router.back()
}

const onCategoryConfirm = (value: any) => {
  form.categoryId = String(value.selectedValues?.[0] ?? '')
  form.categoryName = String(value.selectedOptions?.[0]?.text ?? '')
  showCategoryPicker.value = false
}

const addSchedule = () => {
  schedules.value.push({
    date: '',
    timeSlot: '',
    location: form.serviceMode === 1 ? '线上' : ''
  })
}

const removeSchedule = (index: number) => {
  schedules.value.splice(index, 1)
}

const normalizeDateParts = (dateStr: string): [string, string, string] => {
  const [year = '', month = '', day = ''] = dateStr.split('-')
  return [
    year,
    String(month).padStart(2, '0'),
    String(day).padStart(2, '0')
  ]
}

const openDatePicker = (index: number) => {
  currentDateIndex.value = index
  const currentDate = schedules.value[index]?.date

  if (currentDate) {
    currentDatePicker.value = normalizeDateParts(currentDate)
  } else {
    const now = new Date()
    currentDatePicker.value = [
      String(now.getFullYear()),
      String(now.getMonth() + 1).padStart(2, '0'),
      String(now.getDate()).padStart(2, '0')
    ]
  }

  showDatePicker.value = true
}

const onDateConfirm = (value: any) => {
  const dateStr = `${value.selectedValues[0]}-${value.selectedValues[1]}-${value.selectedValues[2]}`
  schedules.value[currentDateIndex.value].date = dateStr
  showDatePicker.value = false
}

const toMinutes = (time: string): number => {
  const [hours, minutes] = time.split(':').map(Number)
  return hours * 60 + minutes
}

const isFutureOrToday = (dateStr: string): boolean => {
  const picked = dayjs(dateStr, 'YYYY-MM-DD', true)
  if (!picked.isValid()) return false
  return picked.isSame(dayjs(), 'day') || picked.isAfter(dayjs(), 'day')
}

const isTimeSlotOverlap = (a: string, b: string): boolean => {
  const [aStart, aEnd] = a.split('-')
  const [bStart, bEnd] = b.split('-')

  const aStartMinutes = toMinutes(aStart)
  const aEndMinutes = toMinutes(aEnd)
  const bStartMinutes = toMinutes(bStart)
  const bEndMinutes = toMinutes(bEnd)

  return aStartMinutes < bEndMinutes && bStartMinutes < aEndMinutes
}

const validateSchedules = (): boolean => {
  if (schedules.value.length === 0) {
    showToast('请至少添加一个可预约时间段')
    return false
  }

  const datePattern = /^\d{4}-\d{2}-\d{2}$/
  const timePattern = /^\d{2}:\d{2}-\d{2}:\d{2}$/

  for (let i = 0; i < schedules.value.length; i++) {
    const s = schedules.value[i]
    const scheduleNo = i + 1

    if (!s.date || !s.timeSlot) {
      showToast(`时间段 ${scheduleNo} 的日期和时间段不能为空`)
      return false
    }

    if (!datePattern.test(s.date)) {
      showToast(`时间段 ${scheduleNo} 的日期格式不正确，请使用 YYYY-MM-DD 格式`)
      return false
    }

    if (!isFutureOrToday(s.date)) {
      showToast(`时间段 ${scheduleNo} 的日期不能早于今天`)
      return false
    }

    if (!timePattern.test(s.timeSlot)) {
      showToast(`时间段 ${scheduleNo} 的时间段格式不正确，请使用 HH:mm-HH:mm 格式`)
      return false
    }

    const [startTime, endTime] = s.timeSlot.split('-')
    if (toMinutes(startTime) >= toMinutes(endTime)) {
      showToast(`时间段 ${scheduleNo} 的开始时间必须早于结束时间`)
      return false
    }
  }

  const seen = new Set<string>()
  for (let i = 0; i < schedules.value.length; i++) {
    const s = schedules.value[i]
    const key = `${s.date}|${s.timeSlot}`

    if (seen.has(key)) {
      showToast(`时间段 ${i + 1} 与其他时间段重复`)
      return false
    }
    seen.add(key)
  }

  for (let i = 0; i < schedules.value.length; i++) {
    for (let j = i + 1; j < schedules.value.length; j++) {
      const a = schedules.value[i]
      const b = schedules.value[j]

      if (a.date === b.date && isTimeSlotOverlap(a.timeSlot, b.timeSlot)) {
        showToast(`时间段 ${i + 1} 与时间段 ${j + 1} 存在时间重叠`)
        return false
      }
    }
  }

  return true
}

const onSubmit = async () => {
  console.log('[publish] onSubmit triggered')
  console.log('[publish] form =', JSON.parse(JSON.stringify(form)))
  console.log('[publish] schedules =', JSON.parse(JSON.stringify(schedules.value)))

  if (!form.title || !form.categoryId || form.price === undefined) {
    showToast('请填写完整信息')
    return
  }

  if (Number(form.price) <= 0) {
    showToast('价格必须大于 0')
    return
  }

  if (!validateSchedules()) {
    return
  }

  const scheduleItems: ScheduleItem[] = schedules.value.map((s) => ({
    date: s.date,
    timeSlot: s.timeSlot.trim(),
    location: s.location.trim() || undefined
  }))

  const payload = {
    categoryId: Number(form.categoryId),
    title: form.title.trim(),
    description:
      [form.description.trim(), form.notice.trim()].filter(Boolean).join('\n') ||
      form.title.trim(),
    pricePerUnit: Number(form.price),
    unitType: unitTypeMap[form.unit] ?? 1,
    serviceMode: form.serviceMode,
    mediaUrls: '',
    schedules: scheduleItems
  }

  console.log('[publish] payload =', payload)

  isSubmitting.value = true
  try {
    const res = await publishSkillApi(payload)
    console.log('[publish] success =', res)

    showSuccessToast({
      message: '技能发布成功，等待管理员审核',
      duration: 2500
    })
    router.push('/profile')
  } catch (err) {
    console.error('[publish] error =', err)
    const error = err as ApiError
    showToast(error?.response?.data?.message || error?.message || '发布失败，请稍后再试')
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.publish-skill-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-top: 46px;
}

.form-content {
  padding: 12px;
}

.form-section {
  margin-bottom: 12px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.section-title {
  padding: 12px 14px;
  font-weight: 600;
  border-bottom: 1px solid #f2f2f2;
  display: flex;
  align-items: center;
}

.required-mark {
  color: #ee0a24;
  margin-left: 4px;
}

.unit-text {
  color: #666;
  font-size: 12px;
}

.schedule-hint {
  margin: 0;
  padding: 8px 14px;
  font-size: 12px;
  color: #999;
  background: #fafafa;
}

.schedule-item {
  border-bottom: 1px solid #f2f2f2;
  padding-bottom: 8px;
}

.schedule-item:last-of-type {
  border-bottom: none;
}

.schedule-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px 4px;
}

.schedule-index {
  font-size: 13px;
  font-weight: 500;
  color: #667eea;
}

.add-schedule-btn {
  padding: 12px 14px;
  text-align: center;
}

.submit-area {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}

.submit-tip {
  margin-top: 10px;
  color: #999;
  font-size: 12px;
  text-align: center;
}

.time-hint {
  color: #999;
  font-size: 11px;
}
</style>