<template>
  <div class="publish-skill-container">
    <van-nav-bar title="发布技能" left-arrow fixed @click-left="goBack" />

    <div class="form-content">
      <van-form @submit="onSubmit">
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
            :rules="[{ required: true, message: '请输入价格' }]"
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

        <div class="submit-area">
          <van-button type="primary" native-type="submit" block :loading="isSubmitting" loading-text="提交中...">
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
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showToast } from 'vant'
import { publishSkill as publishSkillApi } from '@/api/skill'

interface FormData {
  title: string
  categoryId: string
  categoryName: string
  price: number | null
  unit: string
  serviceMode: number
  description: string
  notice: string
}

const router = useRouter()
const isSubmitting = ref(false)
const showCategoryPicker = ref(false)

const form = reactive<FormData>({
  title: '',
  categoryId: '',
  categoryName: '',
  price: null,
  unit: '小时',
  serviceMode: 2,
  description: '',
  notice: ''
})

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
  form.categoryId = String(value.selectedValues[0])
  form.categoryName = String(value.selectedOptions[0].text)
  showCategoryPicker.value = false
}

const onSubmit = async () => {
  if (!form.title || !form.categoryId || !form.price) {
    showToast('请填写完整信息')
    return
  }

  isSubmitting.value = true
  try {
    await publishSkillApi({
      categoryId: Number(form.categoryId),
      title: form.title.trim(),
      description: [form.description.trim(), form.notice.trim()].filter(Boolean).join('\n') || form.title.trim(),
      pricePerUnit: Number(form.price),
      unitType: unitTypeMap[form.unit] ?? 1,
      serviceMode: form.serviceMode,
      mediaUrls: ''
    })

    showSuccessToast({
      message: '技能发布成功，等待管理员审核',
      duration: 2500
    })
    router.push('/profile')
  } catch {
    showToast('发布失败，请稍后再试')
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
}

.unit-text {
  color: #666;
  font-size: 12px;
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
</style>
