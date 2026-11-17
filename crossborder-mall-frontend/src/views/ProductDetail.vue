<template>
  <div v-if="product" class="detail">
    <section class="card main">
      <div class="thumb" :style="{ backgroundImage: `url(${product.mainImage || ''})` }" />
      <div>
        <h1>{{ product.name }}</h1>
        <p class="muted">{{ product.originCountry }} · {{ product.subtitle }}</p>
        <p class="price">¥{{ displayPrice }}</p>

        <div v-if="product.skus?.length" class="skus">
          <span class="muted">规格：</span>
          <el-radio-group v-model="skuId">
            <el-radio-button v-for="s in product.skus" :key="s.id" :value="s.id">
              {{ s.specDesc }}（库存{{ s.stock }}）
            </el-radio-button>
          </el-radio-group>
        </div>
        <p v-else class="muted">库存 {{ product.stock }}</p>

        <div class="qty">
          <span class="muted">数量</span>
          <el-input-number v-model="quantity" :min="1" :max="99" />
        </div>

        <div class="actions">
          <el-button type="primary" :loading="adding" @click="addToCart">加入购物车</el-button>
          <el-button type="danger" :loading="buying" @click="buyNow">立即购买</el-button>
        </div>
      </div>
    </section>
    <section class="card">
      <h3>商品详情</h3>
      <p>{{ product.detail }}</p>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProduct } from '@/api/product'
import { addCart, checkAllCart, updateCartChecked } from '@/api/cart'
import { getToken } from '@/utils/storage'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const skuId = ref(null)
const quantity = ref(1)
const adding = ref(false)
const buying = ref(false)

const displayPrice = computed(() => {
  if (!product.value) return '0.00'
  const sku = product.value.skus?.find((s) => s.id === skuId.value)
  return Number(sku?.price ?? product.value.price).toFixed(2)
})

watch(
  () => product.value?.skus,
  (skus) => {
    if (skus?.length) skuId.value = skus[0].id
  }
)

const ensureLogin = () => {
  if (!getToken()) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return false
  }
  return true
}

const payload = () => ({
  productId: product.value.id,
  skuId: product.value.hasSku === 1 ? skuId.value : 0,
  quantity: quantity.value
})

const addToCart = async () => {
  if (!ensureLogin()) return
  adding.value = true
  try {
    await addCart(payload())
    ElMessage.success('已加入购物车')
  } finally {
    adding.value = false
  }
}

const buyNow = async () => {
  if (!ensureLogin()) return
  buying.value = true
  try {
    const item = await addCart(payload())
    await checkAllCart(false)
    await updateCartChecked(item.id, true)
    router.push('/checkout')
  } finally {
    buying.value = false
  }
}

onMounted(async () => {
  product.value = await getProduct(route.params.id)
})
</script>

<style scoped lang="scss">
.main {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 20px;
}
@media (max-width: 720px) {
  .main { grid-template-columns: 1fr; }
}
.thumb {
  height: 280px;
  border-radius: 12px;
  background: #f0ebe4 center/cover no-repeat;
}
.price {
  font-size: 28px;
  color: var(--brand-dark);
  font-weight: 800;
}
.skus { margin: 16px 0; }
.qty { display: flex; align-items: center; gap: 10px; margin: 12px 0; }
.actions { display: flex; gap: 10px; margin-top: 20px; }
</style>
