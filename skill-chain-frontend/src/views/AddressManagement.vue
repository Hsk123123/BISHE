<template>
  <div class="address-container">
    <van-nav-bar title="地址管理" left-arrow fixed @click-left="goBack">
      <template #right>
        <van-button type="primary" size="small" @click="showAddPopup = true">添加地址</van-button>
      </template>
    </van-nav-bar>

    <div class="address-list" v-if="addresses.length > 0">
      <div
        v-for="(address, index) in addresses"
        :key="address.id"
        class="address-card"
        @click="selectAddress(address)"
      >
        <div class="address-header">
          <span class="name">{{ address.name }}</span>
          <span class="phone">{{ address.phone }}</span>
          <span v-if="address.isDefault" class="default-tag">默认</span>
        </div>
        <div class="address-detail">
          {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
        </div>
        <div class="address-actions">
          <div class="action-left">
            <van-checkbox
              :model-value="address.isDefault"
              @click.stop="setDefault(address)"
            >
              设为默认
            </van-checkbox>
          </div>
          <div class="action-right">
            <van-button size="small" plain type="primary" @click.stop="editAddress(address)">
              <van-icon name="edit" />编辑
            </van-button>
            <van-button size="small" plain type="danger" @click.stop="deleteAddress(address)">
              <van-icon name="delete" />删除
            </van-button>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      <van-icon name="location-o" size="64px" color="#ccc" />
      <p>暂无收货地址</p>
      <van-button type="primary" @click="showAddPopup = true">添加地址</van-button>
    </div>

    <van-popup v-model:show="showAddPopup" position="bottom" :style="{ height: '85%' }">
      <div class="address-form">
        <div class="form-header">
          <h3>{{ editingAddress ? '编辑地址' : '新增地址' }}</h3>
          <van-icon name="cross" @click="showAddPopup = false" />
        </div>

        <van-form @submit="saveAddress">
          <van-field
            v-model="form.name"
            name="name"
            label="收货人"
            placeholder="请输入姓名"
            :rules="[{ required: true, message: '请输入姓名' }]"
          />
          <van-field
            v-model="form.phone"
            name="phone"
            label="手机号"
            placeholder="请输入手机号"
            :rules="[{ required: true, message: '请输入手机号' }]"
          />
          <van-field
            v-model="form.detail"
            name="detail"
            label="详细地址"
            placeholder="请输入详细地址"
            :rules="[{ required: true, message: '请输入详细地址' }]"
          />
          <div class="submit-btn">
            <van-button type="primary" native-type="submit" block>保存地址</van-button>
          </div>
        </van-form>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast, showConfirmDialog } from 'vant'

interface Address {
  id: number
  name: string
  phone: string
  province: string
  city: string
  district: string
  detail: string
  isDefault: boolean
}

const router = useRouter()
const showAddPopup = ref(false)
const editingAddress = ref<Address | null>(null)

const addresses = ref<Address[]>([
  {
    id: 1,
    name: '张三',
    phone: '13800138000',
    province: '北京市',
    city: '北京市',
    district: '朝阳区',
    detail: '某某路某某号某某小区1号楼101',
    isDefault: true
  },
  {
    id: 2,
    name: '李四',
    phone: '13900139000',
    province: '上海市',
    city: '上海市',
    district: '浦东新区',
    detail: '某某大厦1808室',
    isDefault: false
  }
])

const form = reactive({
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: ''
})

const goBack = () => {
  router.back()
}

const selectAddress = (address: Address) => {
  router.back()
}

const setDefault = (address: Address) => {
  addresses.value.forEach(a => a.isDefault = false)
  address.isDefault = true
  showSuccessToast('已设为默认地址')
}

const editAddress = (address: Address) => {
  editingAddress.value = address
  Object.assign(form, address)
  showAddPopup.value = true
}

const deleteAddress = (address: Address) => {
  showConfirmDialog({
    title: '确认删除',
    message: '确定要删除这个地址吗？'
  }).then(() => {
    const index = addresses.value.findIndex(a => a.id === address.id)
    if (index > -1) {
      addresses.value.splice(index, 1)
      showSuccessToast('删除成功')
    }
  })
}

const saveAddress = () => {
  if (editingAddress.value) {
    Object.assign(editingAddress.value, form)
    showSuccessToast('修改成功')
  } else {
    addresses.value.push({
      id: Date.now(),
      name: form.name,
      phone: form.phone,
      province: form.province,
      city: form.city,
      district: form.district,
      detail: form.detail,
      isDefault: addresses.value.length === 0
    })
    showSuccessToast('添加成功')
  }
  showAddPopup.value = false
  editingAddress.value = null
  Object.keys(form).forEach(key => (form as any)[key] = '')
}
</script>

<style scoped>
.address-container {
  padding-top: 46px;
  min-height: 100vh;
  background: #f5f5f5;
}

.address-list {
  padding: 10px;
}

.address-card {
  background: white;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 10px;
}

.address-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.address-header .name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-right: 10px;
}

.address-header .phone {
  font-size: 14px;
  color: #666;
  flex: 1;
}

.default-tag {
  background: #ff976a;
  color: white;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
}

.address-detail {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  margin-bottom: 12px;
}

.address-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
}

.action-right {
  display: flex;
  gap: 10px;
}

.action-right .van-button {
  padding: 0 10px;
}

.action-right .van-button .van-icon {
  margin-right: 3px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
}

.empty-state p {
  color: #999;
  margin: 15px 0 20px 0;
}

.address-form {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.form-header h3 {
  margin: 0;
  font-size: 16px;
}

.van-form {
  flex: 1;
  overflow-y: auto;
}

.submit-btn {
  padding: 20px 15px;
}
</style>
