<template>
  <div class="layout">
    <header class="topbar">
      <router-link to="/" class="brand">跨境商城</router-link>
      <nav>
        <router-link to="/">首页</router-link>
        <router-link to="/products">商品</router-link>
        <router-link to="/seckills">秒杀</router-link>
        <router-link to="/cart">购物车</router-link>
        <router-link to="/orders">订单</router-link>
        <router-link to="/me">我的</router-link>
      </nav>
      <div class="actions">
        <template v-if="auth.isLoggedIn">
          <span class="muted">{{ auth.user?.nickName }}</span>
          <el-button link type="danger" @click="logout">退出</el-button>
        </template>
        <router-link v-else to="/login">登录</router-link>
      </div>
    </header>
    <main class="page">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const auth = useAuthStore()
const router = useRouter()

const logout = async () => {
  await auth.logout()
  router.push('/login')
}
</script>

<style scoped lang="scss">
.topbar {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 14px 20px;
  border-bottom: 1px solid var(--line);
  background: rgba(255, 255, 255, 0.86);
  backdrop-filter: blur(8px);
  position: sticky;
  top: 0;
  z-index: 20;
}
.brand {
  font-weight: 800;
  font-size: 20px;
  color: var(--brand-dark);
  letter-spacing: 0.04em;
}
nav {
  display: flex;
  gap: 16px;
  flex: 1;
  a.router-link-active {
    color: var(--brand);
    font-weight: 600;
  }
}
.actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
