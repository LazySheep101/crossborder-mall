<template>
  <div>
    <div class="head">
      <h2>购物车</h2>
      <el-checkbox :model-value="allChecked" @change="onCheckAll">全选</el-checkbox>
    </div>

    <el-empty v-if="!items.length" description="购物车空空的，去逛逛跨境好物吧" />

    <article v-for="item in items" :key="item.id" class="card row">
      <el-checkbox :model-value="item.checked === 1" @change="(v) => onCheck(item, v)" />
      <div class="thumb" :style="{ backgroundImage: `url(${item.productImage || ''})` }" />
      <div class="info">
        <h4>{{ item.productName }}</h4>
        <p class="muted">{{ item.specDesc || '默认规格' }}</p>
        <p class="price">¥{{ Number(item.unitPrice).toFixed(2) }}</p>
      </div>
      <el-input-number
        :model-value="item.quantity"
        :min="1"
        :max="item.stock || 99"
        @change="(v) => onQty(item, v)"
      />
      <el-button link type="danger" @click="onRemove(item)">删除</el-button>
    </article>

    <div v-if="items.length" class="bar card">
      <span>已选 {{ summary.checkedCount || 0 }} 件，合计
        <b>¥{{ Number(summary.checkedAmount || 0).toFixed(2) }}</b>
      </span>
      <el-button type="primary" :disabled="!summary.checkedCount" @click="$router.push('/checkout')">
        去结算
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  checkAllCart,
  listCart,
  removeCart,
  updateCartChecked,
  updateCartQuantity,
  cartSummary
} from '@/api/cart'

const items = ref([])
const summary = ref({})

const allChecked = computed(
  () => items.value.length > 0 && items.value.every((i) => i.checked === 1)
)

const load = async () => {
  items.value = (await listCart()) || []
  summary.value = (await cartSummary()) || {}
}

const onCheck = async (item, checked) => {
  await updateCartChecked(item.id, !!checked)
  await load()
}

const onCheckAll = async (checked) => {
  await checkAllCart(!!checked)
  await load()
}

const onQty = async (item, quantity) => {
  if (!quantity) return
  await updateCartQuantity(item.id, quantity)
  await load()
}

const onRemove = async (item) => {
  await removeCart(item.id)
  ElMessage.success('已删除')
  await load()
}

onMounted(load)
</script>

<style scoped lang="scss">
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.row {
  display: grid;
  grid-template-columns: auto 72px 1fr auto auto;
  gap: 12px;
  align-items: center;
  margin-top: 12px;
}
.thumb {
  width: 72px;
  height: 72px;
  border-radius: 10px;
  background: #f0ebe4 center/cover no-repeat;
}
.info h4 { margin: 0 0 4px; }
.price { color: var(--brand-dark); font-weight: 700; margin: 4px 0 0; }
.bar {
  margin-top: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  bottom: 12px;
}
@media (max-width: 720px) {
  .row { grid-template-columns: auto 56px 1fr; }
}
</style>
