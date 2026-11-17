<template>
  <div v-if="order">
    <section class="card">
      <div class="meta">
        <h2>{{ statusLabel(order.status) }}</h2>
        <el-tag>{{ order.orderNo }}</el-tag>
      </div>
      <p class="muted">收货：{{ order.receiverName }} {{ order.receiverPhone }}</p>
      <p class="muted">{{ order.receiverAddress }}</p>
      <p class="amount">应付 ¥{{ Number(order.payAmount).toFixed(2) }}</p>
    </section>

    <section class="card block">
      <h3>商品明细</h3>
      <div v-for="item in order.items || []" :key="item.id" class="line">
        <span>{{ item.productName }} {{ item.specDesc }}</span>
        <span class="muted">x{{ item.quantity }}</span>
        <b>¥{{ Number(item.totalPrice).toFixed(2) }}</b>
      </div>
    </section>

    <div class="actions">
      <el-button v-if="order.status === 'UNPAID'" type="danger" @click="$router.push(`/payment/${order.orderNo}`)">
        去支付
      </el-button>
      <el-button v-if="order.status === 'UNPAID'" @click="cancel">取消订单</el-button>
      <el-button v-if="order.status === 'SHIPPED'" type="primary" @click="confirm">确认收货</el-button>
      <el-button @click="$router.push('/orders')">返回列表</el-button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelOrder, confirmOrder, getOrder } from '@/api/order'

const route = useRoute()
const order = ref(null)

const statusLabel = (s) =>
  ({ UNPAID: '待支付', PAID: '已支付待发货', SHIPPED: '已发货', COMPLETED: '已完成', CANCELLED: '已取消' }[s] || s)

const load = async () => {
  order.value = await getOrder(route.params.orderNo)
}

const cancel = async () => {
  await ElMessageBox.confirm('确认取消该订单？库存将回滚', '取消订单')
  await cancelOrder(order.value.orderNo, '用户取消')
  ElMessage.success('已取消')
  await load()
}

const confirm = async () => {
  await confirmOrder(order.value.orderNo)
  ElMessage.success('已确认收货')
  await load()
}

onMounted(load)
</script>

<style scoped lang="scss">
.meta { display: flex; gap: 10px; align-items: center; }
.amount { font-size: 22px; color: var(--brand-dark); font-weight: 800; }
.block { margin-top: 14px; }
.line {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid var(--line);
}
.actions { margin-top: 16px; display: flex; gap: 10px; flex-wrap: wrap; }
</style>
