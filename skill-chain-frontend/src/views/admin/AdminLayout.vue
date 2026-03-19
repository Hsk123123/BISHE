<template>
  <el-container class="admin-layout">
    <el-aside width="200px">
      <div class="logo">技能链后台</div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>数据看板</span>
        </el-menu-item>
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/categories">
          <el-icon><Grid /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/skills">
          <el-icon><Document /></el-icon>
          <span>技能审核</span>
        </el-menu-item>
        <el-menu-item index="/admin/certifications">
          <el-icon><Postcard /></el-icon>
          <span>实名认证</span>
        </el-menu-item>
        <el-menu-item index="/admin/worker-approvals">
          <el-icon><UserFilled /></el-icon>
          <span>工作者审核</span>
        </el-menu-item>
        <el-menu-item index="/admin/reviews">
          <el-icon><ChatLineRound /></el-icon>
          <span>评价管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/finance">
          <el-icon><Money /></el-icon>
          <span>财务管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <el-icon><ShoppingCart /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/notices">
          <el-icon><Bell /></el-icon>
          <span>公告管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header>
        <div class="header-left">管理员</div>
        <div class="header-right">
          <el-button type="primary" @click="logout">退出登录</el-button>
        </div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Odometer, User, Document, ShoppingCart, Grid, Postcard, ChatLineRound, Money, Bell, UserFilled } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => route.path)

const logout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
}

.el-header {
  background-color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>