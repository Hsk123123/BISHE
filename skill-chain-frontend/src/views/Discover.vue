<template>
  <div class="discover-container">
    <van-nav-bar title="发现" fixed />

    <div class="redirect-state">
      <van-loading size="24px" vertical>正在进入技能列表...</van-loading>
    </div>

    <van-tabbar route fixed>
      <van-tabbar-item icon="home-o" to="/home">首页</van-tabbar-item>
      <van-tabbar-item icon="search" to="/discover">发现</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/orders">订单</van-tabbar-item>
      <van-tabbar-item icon="user-o" to="/profile">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

onMounted(() => {
  router.replace({
    path: '/browse',
    query: route.query
  })
})
</script>

<style scoped>
.discover-container {
  min-height: 100vh;
  padding-top: 46px;
  padding-bottom: 50px;
  background: #f5f5f5;
}

.redirect-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 96px);
}
</style>