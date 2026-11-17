<template>
  <div>
    <section class="card profile">
      <div>
        <h2>{{ auth.user?.nickName || '用户' }}</h2>
        <p class="muted">ID: {{ auth.user?.id }} · {{ auth.user?.role || 'USER' }}</p>
      </div>
    </section>

    <section class="card links">
      <router-link to="/orders">我的订单</router-link>
      <router-link to="/cart">购物车</router-link>
      <router-link to="/addresses">收货地址</router-link>
      <router-link to="/profile">个人资料</router-link>
      <router-link to="/seckills">限时秒杀</router-link>
      <template v-if="auth.isAdmin">
        <router-link to="/admin">管理看板</router-link>
        <router-link to="/admin/products">商品管理</router-link>
        <router-link to="/admin/categories">分类管理</router-link>
        <router-link to="/admin/orders">订单管理</router-link>
        <router-link to="/admin/seckill">秒杀管理</router-link>
      </template>
    </section>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from '@/store/auth'

const auth = useAuthStore()

onMounted(async () => {
  await auth.fetchMe()
})
</script>

<style scoped lang="scss">
.profile {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}
.links {
  margin-top: 14px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 10px;
  a {
    padding: 10px 12px;
    border: 1px solid var(--line);
    border-radius: 10px;
    text-align: center;
  }
}
</style>
