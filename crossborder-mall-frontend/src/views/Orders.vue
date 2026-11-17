<template>
  <div>
    <div class="head">
      <h2>我的订单</h2>
      <el-radio-group v-model="status" size="small" @change="load">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="UNPAID">待支付</el-radio-button>
        <el-radio-button value="PAID">待发货</el-radio-button>
        <el-radio-button value="SHIPPED">待收货</el-radio-button>
        <el-radio-button value="COMPLETED">已完成</el-radio-button>
      </el-radio-group>
    </div>

    <article v-for="o in orders" :key="o.orderNo" class="card row" @click="$router.push(`/orders/${o.orderNo}`)">
      <div>
        <div class="meta">
          <b>{{ o.orderNo }}</b>
          <el-tag size="small">{{ statusLabel(o.status) }}</el-tag>
        </div>
        <p class="muted">{{ o.receiverName }} · ¥{{ Number(o.payAmount).toFixed(2) }}</p>
        <p class="muted">{{ (o.items || []).map((i) => i.productName).join('、') }}</p>
      </div>
      <el-icon><ArrowRight /></el-icon>
    </article>
    <el-empty v-if="!orders.length" description="暂无订单" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ArrowRight } from '@element-plus/icons-vue'
import { listOrders } from '@/api/order'

const status = ref('')
const orders = ref([])

const statusLabel = (s) =>
  ({ UNPAID: '待支付', PAID: '已支付', SHIPPED: '已发货', COMPLETED: '已完成', CANCELLED: '已取消' }[s] || s)

const load = async () => {
  const data = await listOrders({ status: status.value || undefined, page: 1, size: 50 })
  orders.value = data?.records || []
}

onMounted(load)
</script>

<style scoped lang="scss">
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.row {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}
.meta { display: flex; gap: 8px; align-items: center; margin-bottom: 6px; }
</style>
