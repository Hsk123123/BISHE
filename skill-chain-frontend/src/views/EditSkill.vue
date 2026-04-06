<template>
  <div class="edit-skill-container">
    <van-nav-bar title="编辑技能" left-arrow fixed @click-left="goBack" />

    <div v-if="loading" class="loading-state">
      <van-loading size="24px" vertical>加载中...</van-loading>
    </div>

    <div v-else class="form-content">
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
              { validator: (v: string | number | undefined) => Number(v) > 0, message: '价格必须大于 0' }
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
            rows="5"
            maxlength="500"
            show-word-limit
          />
        </div>

        <div class="notice-box">
          <van-icon name="info-o" />
          <span>预约时间段不在此处管理，修改不影响已有时间段</span>
        </div>

        <div class="submit-area">
          <van-button
            type="primary"
            block
            :loading="isSubmitting"
            loading-text="保存中..."
            @click="onSubmit"
          >
            保存修改
          </van-button>
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
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showSuccessToast, showToast } from 'vant'
import { getSkillDetail, updateSkill } from '@/api/skill'

interface FormData {
  title: string
  categoryId: string
  categoryName: string
  price?: number
  unit: string
  serviceMode: number
  description: string
}

interface ApiError {
  response?: { data?: { message?: string } }
  message?: string
}

const router = useRouter()
const route = useRoute()
const skillId = route.params.id as string

const loading = ref(true)
const isSubmitting = ref(false)
const showCategoryPicker = ref(false)

const unitTypeMap: Record<string, number> = { 小时: 1, 次: 2, 单: 3, 月: 4 }
const unitNameMap: Record<number, string> = { 1: '小时', 2: '次', 3: '单', 4: '月' }

const categoryOptions = [
  { text: '家政服务', value: '1' },
  { text: '技能陪练', value: '2' },
  { text: '设计服务', value: '3' },
  { text: '咨询服务', value: '4' },
  { text: '教育培训', value: '5' },
  { text: '更多服务', value: '6' }
]

const categoryNameMap: Record<string, string> = Object.fromEntries(
  categoryOptions.map(o => [o.value, o.text])
)

const form = reactive<FormData>({
  title: '',
  categoryId: '',
  categoryName: '',
  price: undefined,
  unit: '小时',
  serviceMode: 2,
  description: ''
})

onMounted(async () => {
  try {
    const skill = await getSkillDetail(skillId)
    form.title = skill.title ?? ''
    form.categoryId = String(skill.categoryId ?? '')
    form.categoryName = categoryNameMap[String(skill.categoryId ?? '')] ?? ''
    form.price = skill.pricePerUnit ?? skill.price
    form.unit = unitNameMap[skill.unitType ?? 1] ?? '小时'
    form.serviceMode = skill.serviceMode ?? 2
    form.description = skill.description ?? ''
  } catch {
    showToast('加载技能信息失败')
    router.back()
  } finally {
    loading.value = false
  }
})

const goBack = () => router.back()

const onCategoryConfirm = (value: any) => {
  form.categoryId = String(value.selectedValues?.[0] ?? '')
  form.categoryName = String(value.selectedOptions?.[0]?.text ?? '')
  showCategoryPicker.value = false
}

const onSubmit = async () => {
  if (!form.title || !form.categoryId || form.price === undefined) {
    showToast('请填写完整信息')
    return
  }
  if (Number(form.price) <= 0) {
    showToast('价格必须大于 0')
    return
  }

  isSubmitting.value = true
  try {
    await updateSkill(skillId, {
      categoryId: Number(form.categoryId),
      title: form.title.trim(),
      description: form.description.trim() || form.title.trim(),
      pricePerUnit: Number(form.price),
      unitType: unitTypeMap[form.unit] ?? 1,
      serviceMode: form.serviceMode
    })
    showSuccessToast('修改已保存')
    router.replace('/my-skills')
  } catch (err) {
    const e = err as ApiError
    showToast(e?.response?.data?.message || e?.message || '保存失败，请稍后再试')
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.edit-skill-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-top: 46px;
}

.loading-state {
  display: flex;
  justify-content: center;
  padding: 60px 0;
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
}

.unit-text {
  color: #666;
  font-size: 12px;
}

.notice-box {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 12px;
  padding: 10px 14px;
  border-radius: 8px;
  background: #fffbe6;
  color: #888;
  font-size: 12px;
}

.submit-area {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}
</style>
