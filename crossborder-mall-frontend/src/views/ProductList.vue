<template>
  <div>
    <div class="toolbar card">
      <el-select v-model="categoryId" clearable placeholder="全部分类" style="width:160px" @change="reload">
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-input v-model="keyword" placeholder="搜索跨境好物" clearable style="max-width:280px" @keyup.enter="reload" />
      <el-select v-model="sortBy" style="width:140px" @change="reload">
        <el-option label="销量优先" value="sales" />
        <el-option label="最新上架" value="newest" />
        <el-option label="价格升序" value="price_asc" />
        <el-option label="价格降序" value="price_desc" />
      </el-select>
      <el-button type="primary" @click="reload">搜索</el-button>
    </div>

    <div class="grid">
      <article v-for="p in products" :key="p.id" class="card item" @click="$router.push(`/products/${p.id}`)">
        <div class="thumb" :style="{ backgroundImage: `url(${p.mainImage || ''})` }" />
        <h3>{{ p.name }}</h3>
        <p class="muted">{{ p.originCountry || '跨境直邮' }} · {{ p.subtitle }}</p>
        <p class="price">¥{{ Number(p.price).toFixed(2) }}</p>
      </article>
    </div>
    <el-empty v-if="!products.length" description="暂无商品" />
    <el-pagination
      class="pager"
      background
      layout="prev, pager, next, total"
      :total="total"
      :page-size="size"
      v-model:current-page="page"
      @current-change="load"
    />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { listCategories, listProducts } from '@/api/product'

const route = useRoute()
const categories = ref([])
const products = ref([])
const categoryId = ref(route.query.categoryId ? Number(route.query.categoryId) : undefined)
const keyword = ref('')
const sortBy = ref('sales')
const page = ref(1)
const size = 12
const total = ref(0)

const load = async () => {
  const data = await listProducts({
    categoryId: categoryId.value || undefined,
    keyword: keyword.value || undefined,
    sortBy: sortBy.value,
    page: page.value,
    size
  })
  products.value = data?.records || []
  total.value = Number(data?.total || 0)
}

const reload = async () => {
  page.value = 1
  await load()
}

onMounted(async () => {
  categories.value = (await listCategories()) || []
  await load()
})
</script>

<style scoped lang="scss">
.toolbar { display: flex; flex-wrap: wrap; gap: 10px; margin-bottom: 16px; }
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;
}
.item { cursor: pointer; }
.thumb {
  height: 180px;
  border-radius: 10px;
  background: #f0ebe4 center/cover no-repeat;
  margin-bottom: 10px;
  border: 1px solid var(--line);
}
h3 { margin: 0 0 6px; font-size: 15px; }
.price { color: var(--brand-dark); font-weight: 700; margin: 8px 0 0; }
.pager { margin-top: 18px; justify-content: center; }
</style>
