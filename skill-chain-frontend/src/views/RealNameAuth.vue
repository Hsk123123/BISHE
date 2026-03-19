<template>
  <div class="auth-container">
    <van-nav-bar title="实名认证" left-arrow fixed @click-left="goBack" />

    <div class="auth-content">
      <div v-if="authStatus === 'none'" class="auth-form">
        <div class="auth-icon">
          <van-icon name="certificate" size="64px" color="#667eea" />
        </div>
        <h3>实名认证</h3>
        <p>为保障账户安全，请完成实名认证</p>

        <van-form @submit="submitAuth">
          <van-field
            v-model="form.realName"
            name="realName"
            label="真实姓名"
            placeholder="请输入您的真实姓名"
            :rules="[{ required: true, message: '请输入真实姓名' }]"
          />
          <van-field
            v-model="form.idCard"
            name="idCard"
            label="身份证号"
            placeholder="请输入身份证号码"
            :rules="[
              { required: true, message: '请输入身份证号' },
              { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[1-2]\d|3[0-1])\d{3}(\d|X|x)$/, message: '请输入正确的身份证号' }
            ]"
          />
          <van-field
            v-model="form.idCardFront"
            name="idCardFront"
            label="身份证正面"
          >
            <template #input>
              <van-uploader
                :after-read="(file: any) => uploadImage(file, 'front')"
                :max-count="1"
              >
                <div class="upload-box" v-if="!form.idCardFront">
                  <van-icon name="photograph" />
                  <span>上传身份证正面</span>
                </div>
                <van-image
                  v-else
                  :src="form.idCardFront"
                  class="preview-image"
                />
              </van-uploader>
            </template>
          </van-field>
          <van-field
            v-model="form.idCardBack"
            name="idCardBack"
            label="身份证反面"
          >
            <template #input>
              <van-uploader
                :after-read="(file: any) => uploadImage(file, 'back')"
                :max-count="1"
              >
                <div class="upload-box" v-if="!form.idCardBack">
                  <van-icon name="photograph" />
                  <span>上传身份证反面</span>
                </div>
                <van-image
                  v-else
                  :src="form.idCardBack"
                  class="preview-image"
                />
              </van-uploader>
            </template>
          </van-field>

          <div class="submit-area">
            <van-button type="primary" native-type="submit" block :loading="isSubmitting">
              提交认证
            </van-button>
          </div>
        </van-form>
      </div>

      <div v-else-if="authStatus === 'pending'" class="auth-status">
        <div class="status-icon">
          <van-icon name="clock" size="64px" color="#ff976a" />
        </div>
        <h3>认证审核中</h3>
        <p>您的实名认证信息已提交，请耐心等待审核</p>
        <div class="auth-info">
          <div class="info-item">
            <span>姓名</span>
            <span>{{ form.realName }}</span>
          </div>
          <div class="info-item">
            <span>身份证号</span>
            <span>{{ form.idCard }}</span>
          </div>
        </div>
      </div>

      <div v-else-if="authStatus === 'success'" class="auth-status">
        <div class="status-icon success">
          <van-icon name="checked" size="64px" color="#07c160" />
        </div>
        <h3>实名认证成功</h3>
        <div class="auth-info">
          <div class="info-item">
            <span>姓名</span>
            <span>{{ form.realName }}</span>
          </div>
          <div class="info-item">
            <span>身份证号</span>
            <span>{{ form.idCard }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'

const router = useRouter()
const isSubmitting = ref(false)
const authStatus = ref<'none' | 'pending' | 'success'>('none')

const form = reactive({
  realName: '',
  idCard: '',
  idCardFront: '',
  idCardBack: ''
})

const goBack = () => {
  router.back()
}

const uploadImage = (file: any, type: string) => {
  showToast('图片上传中...')
  setTimeout(() => {
    if (type === 'front') {
      form.idCardFront = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'
    } else {
      form.idCardBack = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'
    }
    showSuccessToast('上传成功')
  }, 500)
}

const submitAuth = async () => {
  isSubmitting.value = true
  await new Promise(resolve => setTimeout(resolve, 1500))
  isSubmitting.value = false

  showSuccessToast('认证信息已提交')
  authStatus.value = 'pending'
}
</script>

<style scoped>
.auth-container {
  padding-top: 46px;
  min-height: 100vh;
  background: #f5f5f5;
}

.auth-content {
  padding: 20px;
}

.auth-form {
  background: white;
  border-radius: 12px;
  padding: 20px;
}

.auth-icon {
  text-align: center;
  padding: 20px 0;
}

.auth-form h3 {
  text-align: center;
  font-size: 18px;
  margin: 0 0 10px 0;
}

.auth-form > p {
  text-align: center;
  color: #999;
  font-size: 14px;
  margin: 0 0 20px 0;
}

.upload-box {
  width: 100%;
  height: 120px;
  border: 1px dashed #ddd;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 13px;
  gap: 8px;
}

.preview-image {
  width: 100%;
  height: 120px;
  border-radius: 8px;
}

.submit-area {
  margin-top: 30px;
}

.auth-status {
  background: white;
  border-radius: 12px;
  padding: 30px 20px;
  text-align: center;
}

.status-icon {
  padding: 20px 0;
}

.status-icon.success {
  color: #07c160;
}

.auth-status h3 {
  font-size: 18px;
  margin: 0 0 10px 0;
}

.auth-status > p {
  color: #999;
  font-size: 14px;
  margin: 0 0 20px 0;
}

.auth-info {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 15px;
  text-align: left;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item span:first-child {
  color: #999;
}

.info-item span:last-child {
  color: #333;
}

:deep(.van-cell__title) {
  width: 80px;
}
</style>
