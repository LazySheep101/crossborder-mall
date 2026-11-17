<template>
  <div>
    <section class="hero card">
      <h1>跨境商城</h1>
      <p class="muted">海外直邮 · 保税仓发货 · 正品好物 · 下单支付闭环</p>
      <div class="hero-actions">
        <el-button type="primary" @click="$router.push('/products')">去逛逛</el-button>
        <el-button type="danger" plain @click="$router.push('/seckills')">限时秒杀</el-button>
        <el-button @click="$router.push('/cart')">购物车</el-button>
      </div>
    </section>

    <section class="block">
      <h2>商品分类</h2>
      <div class="types">
        <button
          v-for="c in categories"
          :key="c.id"
          class="type-chip"
          @click="$router.push({ path: '/products', query: { categoryId: c.id } })"
        >
          {{ c.name }}
        </button>
      </div>
    </section>

    <section class="block">
      <div class="head">
        <h2>热卖推荐</h2>
        <router-link to="/products">查看全部</router-link>
      </div>
      <div class="grid">
        <article v-for="p in products" :key="p.id" class="card item" @click="$router.push(`/products/${p.id}`)">
          <div class="thumb" :style="{ backgroundImage: `url(${p.mainImage || ''})` }" />
          <h3>{{ p.name }}</h3>
          <p class="muted">{{ p.originCountry }}</p>
          <p class="price">¥{{ Number(p.price).toFixed(2) }}</p>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { listCategories, listProducts } from '@/api/product'

const categories = ref([])
const products = ref([])

onMounted(async () => {
  categories.value = (await listCategories()) || []
  const data = await listProducts({ page: 1, size: 8 })
  products.value = data?.records || []
})
</script>

<style scoped lang="scss">
.hero {
  margin-bottom: 20px;
  h1 { margin: 0 0 8px; font-size: 28px; }
}
.hero-actions { display: flex; gap: 10px; margin-top: 8px; }
.block { margin-top: 24px; }
.head {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}
.types { display: flex; flex-wrap: wrap; gap: 10px; }
.type-chip {
  border: 1px solid var(--line);
  background: #fff;
  border-radius: 999px;
  padding: 8px 14px;
  cursor: pointer;
}
.type-chip:hover { border-color: var(--brand); color: var(--brand); }
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}
.item { cursor: pointer; }
.thumb {
  height: 160px;
  border-radius: 10px;
  background: #f0ebe4 center/cover no-repeat;
  margin-bottom: 8px;
  border: 1px solid var(--line);
}
h3 { margin: 0 0 4px; font-size: 15px; }
.price { color: var(--brand-dark); font-weight: 700; }
</style>
