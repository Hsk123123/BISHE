<template>
  <div class="profile-edit-container">
    <van-nav-bar title="编辑资料" left-arrow fixed @click-left="goBack" />

    <div class="form-content">
      <van-form @submit="onSubmit">
        <div class="avatar-section">
          <div class="avatar-wrapper" @click="showAvatarPicker = true">
            <van-image round :src="form.avatar" class="avatar-image" />
            <div class="avatar-edit">
              <van-icon name="camera-o" />
              <span>更换</span>
            </div>
          </div>
        </div>

        <van-cell-group inset>
          <van-field
            v-model="form.nickname"
            name="nickname"
            label="昵称"
            placeholder="请输入昵称"
            :rules="[{ required: true, message: '请输入昵称' }]"
          />

          <van-field
            v-model="form.bio"
            name="bio"
            label="个人简介"
            type="textarea"
            rows="2"
            placeholder="介绍一下自己"
            maxlength="100"
            show-word-limit
          />

          <van-field
            v-model="form.phone"
            name="phone"
            label="手机号"
            placeholder="请输入手机号"
            :rules="[{ required: true, message: '请输入手机号' }]"
          />

          <van-field
            v-model="form.email"
            name="email"
            label="邮箱"
            placeholder="请输入邮箱"
          />

          <van-field
            v-model="form.gender"
            name="gender"
            label="性别"
            placeholder="请选择性别"
            is-link
            readonly
            @click="showGenderPicker = true"
          />
        </van-cell-group>

        <div class="submit-area">
          <van-button type="primary" native-type="submit" block :loading="isSubmitting">
            保存修改
          </van-button>
        </div>
      </van-form>
    </div>

    <van-popup v-model:show="showGenderPicker" position="bottom">
      <van-picker
        title="选择性别"
        :columns="genderOptions"
        @confirm="onGenderConfirm"
        @cancel="showGenderPicker = false"
      />
    </van-popup>

    <van-action-sheet
      v-model:show="showAvatarPicker"
      :actions="avatarActions"
      cancel-text="取消"
      @select="onAvatarSelect"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'

const router = useRouter()
const isSubmitting = ref(false)
const showGenderPicker = ref(false)
const showAvatarPicker = ref(false)

const genderOptions = [
  { text: '男', value: 'male' },
  { text: '女', value: 'female' },
  { text: '保密', value: 'secret' }
]

const avatarActions = [
  { name: '从相册选择', value: 'album' },
  { name: '拍照', value: 'camera' }
]

const form = reactive({
  nickname: '用户昵称',
  bio: '这是个人简介',
  phone: '138****8888',
  email: 'user@example.com',
  gender: '保密',
  avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'
})

const goBack = () => {
  router.back()
}

const onGenderConfirm = ({ selectedValues }: any) => {
  form.gender = selectedValues[0]
  showGenderPicker.value = false
}

const onAvatarSelect = ({ value }: any) => {
  showAvatarPicker.value = false
  showToast(`已选择${value === 'album' ? '相册' : '拍照'}`)
}

const onSubmit = async () => {
  isSubmitting.value = true
  await new Promise(resolve => setTimeout(resolve, 1000))
  isSubmitting.value = false
  showSuccessToast('保存成功')
  router.back()
}
</script>

<style scoped>
.profile-edit-container {
  padding-top: 46px;
  min-height: 100vh;
  background: #f5f5f5;
}

.form-content {
  padding: 15px;
}

.avatar-section {
  display: flex;
  justify-content: center;
  padding: 30px 0;
  background: white;
  margin-bottom: 15px;
  border-radius: 8px;
}

.avatar-wrapper {
  position: relative;
}

.avatar-image {
  width: 100px;
  height: 100px;
}

.avatar-edit {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.5);
  color: white;
  font-size: 11px;
  padding: 2px 10px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 3px;
}

.submit-area {
  margin: 30px 15px;
}

:deep(.van-cell__title) {
  width: 80px;
}
</style>
