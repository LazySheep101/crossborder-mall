<template>
  <div>
    <h2>管理看板</h2>
    <div class="stats">
      <div class="card stat"><p class="muted">商品数</p><b>{{ data.productCount || 0 }}</b></div>
      <div class="card stat"><p class="muted">用户数</p><b>{{ data.userCount || 0 }}</b></div>
      <div class="card stat"><p class="muted">订单数</p><b>{{ data.orderCount || 0 }}</b></div>
      <div class="card stat"><p class="muted">成交额</p><b>¥{{ Number(data.totalSales || 0).toFixed(2) }}</b></div>
      <div class="card stat"><p class="muted">待支付</p><b>{{ data.unpaidCount || 0 }}</b></div>
      <div class="card stat"><p class="muted">待发货</p><b>{{ data.paidCount || 0 }}</b></div>
      <div class="card stat"><p class="muted">已发货</p><b>{{ data.shippedCount || 0 }}</b></div>
    </div>
    <div class="links card">
      <router-link to="/admin/products">商品管理</router-link>
      <router-link to="/admin/categories">分类管理</router-link>
      <router-link to="/admin/orders">订单发货</router-link>
      <router-link to="/admin/seckill">秒杀管理</router-link>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { adminDashboard } from '@/api/order'

const data = ref({})
onMounted(async () => {
  data.value = (await adminDashboard()) || {}
})
</script>

<style scoped lang="scss">
.stats {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
  margin: 16px 0;
}
.stat {
  text-align: center;
  b { font-size: 22px; color: var(--brand-dark); }
  p { margin: 0 0 6px; }
}
.links {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  a {
    padding: 8px 14px;
    border: 1px solid var(--line);
    border-radius: 10px;
  }
}
</style>
