<template>
  <div class="schedule-container">
    <van-nav-bar title="日程管理" left-arrow @click-left="router.back()" fixed />

    <div class="page-body">
      <div class="add-card">
        <div class="add-card-title">添加时间段</div>

        <div class="form-row">
          <span class="form-label">日期</span>
          <div class="form-value" @click="showDatePicker = true">
            <span :class="selectedDate ? 'date-text' : 'placeholder-text'">
              {{ selectedDate || '请选择日期' }}
            </span>
            <van-icon name="arrow" color="#c8c9cc" />
          </div>
        </div>

        <div class="form-row">
          <span class="form-label">时间段</span>
          <div class="slot-options">
            <div
              v-for="slot in timeSlots"
              :key="slot"
              class="slot-btn"
              :class="{ active: selectedSlot === slot }"
              @click="selectedSlot = slot"
            >
              {{ slot }}
            </div>
          </div>
        </div>

        <van-button
          type="primary"
          block
          round
          :loading="adding"
          :disabled="!selectedDate || !selectedSlot"
          @click="handleAdd"
        >
          添加时间段
        </van-button>
      </div>

      <div class="list-section">
        <div class="list-title">已有时间段</div>

        <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
          <div v-if="loading && !schedules.length" class="loading-wrap">
            <van-loading size="24" />
          </div>

          <div v-else-if="!schedules.length" class="empty-wrap">
            <van-empty description="暂无时间段，请添加" />
          </div>

          <div v-else>
            <div v-for="group in groupedSchedules" :key="group.date" class="date-group">
              <div class="date-label">{{ group.date }}</div>
              <div class="slot-list">
                <div v-for="item in group.items" :key="item.scheduleId" class="slot-row">
                  <div class="slot-info">
                    <van-tag
                      :type="statusType(item.status)"
                      size="medium"
                      class="status-tag"
                    >
                      {{ statusText(item.status) }}
                    </van-tag>
                    <span class="slot-name">{{ item.timeSlot }}</span>
                  </div>
                  <van-button
                    v-if="item.status === 0"
                    size="small"
                    type="danger"
                    plain
                    :loading="deletingId === item.scheduleId"
                    @click="handleDelete(item.scheduleId!)"
                  >
                    删除
                  </van-button>
                </div>
              </div>
            </div>
          </div>
        </van-pull-refresh>
      </div>
    </div>

    <van-popup v-model:show="showDatePicker" position="bottom" round>
      <van-date-picker
        v-model="pickerDate"
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'
import { getMySchedules, addSchedule, deleteSchedule, type ScheduleItem } from '@/api/schedule'

const router = useRouter()

const schedules = ref<ScheduleItem[]>([])
const loading = ref(false)
const refreshing = ref(false)
const adding = ref(false)
const deletingId = ref<number | null>(null)

const showDatePicker = ref(false)
const selectedDate = ref('')
const selectedSlot = ref('')

const today = new Date()
const minDate = new Date(today.getFullYear(), today.getMonth(), today.getDate())
const maxDate = new Date(today.getFullYear(), today.getMonth() + 3, today.getDate())

const pickerDate = ref([
  String(today.getFullYear()),
  String(today.getMonth() + 1).padStart(2, '0'),
  String(today.getDate()).padStart(2, '0')
])

const timeSlots = ['上午(09:00-12:00)', '下午(13:00-18:00)', '晚上(18:00-21:00)', '全天(09:00-21:00)']

const groupedSchedules = computed(() => {
  const map = new Map<string, ScheduleItem[]>()
  for (const s of schedules.value) {
    const d = s.date || ''
    if (!map.has(d)) map.set(d, [])
    map.get(d)!.push(s)
  }
  return Array.from(map.entries())
    .sort((a, b) => a[0].localeCompare(b[0]))
    .map(([date, items]) => ({ date, items }))
})

const statusText = (status?: number) => {
  if (status === 1) return '已预约'
  if (status === 2) return '锁定'
  return '空闲'
}

const statusType = (status?: number) => {
  if (status === 1) return 'primary'
  if (status === 2) return 'danger'
  return 'success'
}

const loadSchedules = async () => {
  loading.value = true
  try {
    const res = await getMySchedules()
    schedules.value = (res as any)?.data ?? res ?? []
  } catch {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

const onRefresh = async () => {
  await loadSchedules()
  refreshing.value = false
}

const onDateConfirm = ({ selectedValues }: { selectedValues: string[] }) => {
  selectedDate.value = selectedValues.join('-')
  showDatePicker.value = false
}

const handleAdd = async () => {
  if (!selectedDate.value || !selectedSlot.value) return
  adding.value = true
  try {
    await addSchedule({ date: selectedDate.value, timeSlot: selectedSlot.value })
    showSuccessToast('添加成功')
    selectedDate.value = ''
    selectedSlot.value = ''
    await loadSchedules()
  } catch {
    // error toast handled by interceptor
  } finally {
    adding.value = false
  }
}

const handleDelete = async (id: number) => {
  deletingId.value = id
  try {
    await deleteSchedule(id)
    showSuccessToast('删除成功')
    schedules.value = schedules.value.filter(s => s.scheduleId !== id)
  } catch {
    // error toast handled by interceptor
  } finally {
    deletingId.value = null
  }
}

onMounted(loadSchedules)
</script>

<style scoped>
.schedule-container {
  min-height: 100vh;
  padding-top: 46px;
  background: #f6f8fc;
}

.page-body {
  padding: 14px;
}

.add-card {
  background: #fff;
  border-radius: 18px;
  padding: 18px 16px;
  margin-bottom: 14px;
  box-shadow: 0 4px 16px rgba(31, 45, 61, 0.06);
}

.add-card-title {
  font-size: 16px;
  font-weight: 700;
  color: #1f2330;
  margin-bottom: 16px;
}

.form-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
}

.form-label {
  flex-shrink: 0;
  width: 48px;
  font-size: 14px;
  color: #646566;
}

.form-value {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-radius: 10px;
  background: #f7f8fa;
  font-size: 14px;
}

.date-text {
  color: #1f2330;
}

.placeholder-text {
  color: #c8c9cc;
}

.slot-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  flex: 1;
}

.slot-btn {
  padding: 7px 12px;
  border-radius: 20px;
  font-size: 13px;
  border: 1px solid #e5e6eb;
  color: #646566;
  background: #f7f8fa;
  cursor: pointer;
  transition: all 0.15s;
}

.slot-btn.active {
  border-color: #7232dd;
  color: #7232dd;
  background: #f5ecff;
  font-weight: 600;
}

.list-section {
  background: #fff;
  border-radius: 18px;
  padding: 18px 16px;
  box-shadow: 0 4px 16px rgba(31, 45, 61, 0.06);
}

.list-title {
  font-size: 16px;
  font-weight: 700;
  color: #1f2330;
  margin-bottom: 12px;
}

.loading-wrap,
.empty-wrap {
  padding: 24px 0;
  display: flex;
  justify-content: center;
}

.date-group {
  margin-bottom: 14px;
}

.date-label {
  font-size: 13px;
  font-weight: 600;
  color: #7b8295;
  margin-bottom: 8px;
  padding-left: 2px;
}

.slot-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.slot-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  border-radius: 12px;
  background: #f7f8fa;
}

.slot-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.slot-name {
  font-size: 14px;
  color: #1f2330;
}

.status-tag {
  flex-shrink: 0;
}
</style>
