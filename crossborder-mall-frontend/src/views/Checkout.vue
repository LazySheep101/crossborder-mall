<template>
  <div>
    <h2>确认订单</h2>

    <section class="card">
      <div class="head">
        <h3>收货地址</h3>
        <el-button link type="primary" @click="$router.push('/addresses')">管理地址</el-button>
      </div>
      <el-radio-group v-model="addressId" class="addrs">
        <el-radio v-for="a in addresses" :key="a.id" :value="a.id" border>
          {{ a.receiverName }} {{ a.receiverPhone }} ·
          {{ a.province }}{{ a.city }}{{ a.district }}{{ a.detail }}
        </el-radio>
      </el-radio-group>
      <el-empty v-if="!addresses.length" description="请先添加收货地址" />
    </section>

    <section class="card block">
      <h3>商品</h3>
      <div v-for="item in checkedItems" :key="item.id" class="line">
        <span>{{ item.productName }} {{ item.specDesc }}</span>
        <span class="muted">x{{ item.quantity }}</span>
        <b>¥{{ (Number(item.unitPrice) * item.quantity).toFixed(2) }}</b>
      </div>
      <el-input v-model="remark" type="textarea" :rows="2" placeholder="备注（可选）" class="remark" />
    </section>

    <div class="bar card">
      <span>应付 <b>¥{{ total.toFixed(2) }}</b></span>
      <el-button type="danger" :loading="submitting" :disabled="!addressId || !checkedItems.length" @click="submit">
        提交订单
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listAddresses } from '@/api/address'
import { listCart } from '@/api/cart'
import { createOrder } from '@/api/order'

const router = useRouter()
const addresses = ref([])
const items = ref([])
const addressId = ref(null)
const remark = ref('')
const submitting = ref(false)

const checkedItems = computed(() => items.value.filter((i) => i.checked === 1))
const total = computed(() =>
  checkedItems.value.reduce((s, i) => s + Number(i.unitPrice) * i.quantity, 0)
)

const submit = async () => {
  submitting.value = true
  try {
    const order = await createOrder({ addressId: addressId.value, remark: remark.value })
    ElMessage.success('下单成功')
    router.replace(`/orders/${order.orderNo}`)
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  addresses.value = (await listAddresses()) || []
  const def = addresses.value.find((a) => a.isDefault === 1)
  addressId.value = def?.id || addresses.value[0]?.id || null
  items.value = (await listCart()) || []
})
</script>

<style scoped lang="scss">
.head { display: flex; justify-content: space-between; align-items: center; }
.addrs { display: flex; flex-direction: column; gap: 8px; align-items: stretch; width: 100%; }
.block { margin-top: 14px; }
.line {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid var(--line);
}
.remark { margin-top: 12px; }
.bar {
  margin-top: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
b { color: var(--brand-dark); }
</style>
