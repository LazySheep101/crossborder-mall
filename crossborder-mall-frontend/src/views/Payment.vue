<template>
  <div v-if="order">
    <section class="card">
      <h2>选择支付方式</h2>
      <p class="muted">订单 {{ order.orderNo }} · 应付
        <b>¥{{ Number(order.payAmount).toFixed(2) }}</b>
      </p>
    </section>

    <div class="methods">
      <button
        v-for="m in methods"
        :key="m.channel"
        class="card method"
        :class="{ active: channel === m.channel }"
        @click="channel = m.channel"
      >
        <strong>{{ m.title }}</strong>
        <p class="muted">{{ m.desc }}</p>
      </button>
    </div>

    <el-button type="danger" size="large" :loading="paying" :disabled="!channel" @click="pay">
      确认支付（演示）
    </el-button>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrder, mockPay } from '@/api/order'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const channel = ref('ALIPAY')
const paying = ref(false)

const methods = [
  { channel: 'ALIPAY', title: '支付宝', desc: '演示渠道 · 实际走模拟支付落库' },
  { channel: 'WECHAT', title: '微信支付', desc: '演示渠道 · 实际走模拟支付落库' },
  { channel: 'STRIPE', title: 'Stripe', desc: '演示渠道 · 实际走模拟支付落库' }
]

const pay = async () => {
  paying.value = true
  try {
    await mockPay(order.value.orderNo, channel.value)
    ElMessage.success('支付成功')
    router.replace(`/orders/${order.value.orderNo}`)
  } finally {
    paying.value = false
  }
}

onMounted(async () => {
  order.value = await getOrder(route.params.orderNo)
  if (order.value.status !== 'UNPAID') {
    ElMessage.info('订单无需支付')
    router.replace(`/orders/${order.value.orderNo}`)
  }
})
</script>

<style scoped lang="scss">
.methods {
  display: grid;
  gap: 12px;
  margin: 16px 0;
}
.method {
  text-align: left;
  cursor: pointer;
  border: 2px solid var(--line);
  background: #fff;
}
.method.active { border-color: var(--brand); }
b { color: var(--brand-dark); }
</style>
