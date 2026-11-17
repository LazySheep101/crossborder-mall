<template>
  <div v-if="activity" class="card">
    <div class="thumb" :style="{ backgroundImage: `url(${activity.productImage || ''})` }" />
    <h1>{{ activity.title }}</h1>
    <p class="muted">{{ activity.productName }} · {{ activity.specDesc }}</p>
    <p class="price">秒杀价 ¥{{ Number(activity.seckillPrice).toFixed(2) }}</p>
    <p class="muted">原价 ¥{{ Number(activity.originPrice || 0).toFixed(2) }} · 剩余 {{ activity.stock }}</p>

    <el-form label-width="80px" class="form">
      <el-form-item label="地址">
        <el-select v-model="addressId" placeholder="选择收货地址" style="width: 100%">
          <el-option
            v-for="a in addresses"
            :key="a.id"
            :label="`${a.receiverName} ${a.receiverPhone} ${a.detail}`"
            :value="a.id"
          />
        </el-select>
      </el-form-item>
    </el-form>

    <el-button type="danger" :loading="submitting" :disabled="!addressId" @click="grab">
      立即秒杀
    </el-button>
    <el-button @click="$router.push('/addresses')">管理地址</el-button>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSeckillActivity, placeSeckillOrder, seckillResult } from '@/api/seckill'
import { listAddresses } from '@/api/address'

const route = useRoute()
const router = useRouter()
const activity = ref(null)
const addresses = ref([])
const addressId = ref(null)
const submitting = ref(false)

const grab = async () => {
  submitting.value = true
  try {
    const queued = await placeSeckillOrder(activity.value.id, addressId.value)
    ElMessage.info('已进入排队，正在生成订单')
    const orderNo = queued.orderNo && queued.status === 'SUCCESS'
      ? queued.orderNo
      : await pollResult(activity.value.id)
    ElMessage.success('秒杀成功，请支付')
    router.replace(`/payment/${orderNo}`)
  } catch (error) {
    if (error instanceof Error && !error.response) {
      ElMessage.error(error.message)
    }
  } finally {
    submitting.value = false
  }
}

const pollResult = async (id) => {
  for (let i = 0; i < 20; i += 1) {
    await new Promise((resolve) => setTimeout(resolve, 500))
    const res = await seckillResult(id)
    if (res.status === 'SUCCESS' && res.orderNo) {
      return res.orderNo
    }
    if (res.status === 'FAIL') {
      throw new Error(res.message || '秒杀失败')
    }
  }
  throw new Error('排队超时，请到订单里查看')
}

onMounted(async () => {
  activity.value = await getSeckillActivity(route.params.id)
  addresses.value = (await listAddresses()) || []
  addressId.value = addresses.value.find((a) => a.isDefault === 1)?.id || addresses.value[0]?.id || null
})
</script>

<style scoped lang="scss">
.thumb {
  height: 220px;
  border-radius: 12px;
  background: #f0ebe4 center/cover no-repeat;
  margin-bottom: 12px;
}
.price { font-size: 28px; color: var(--brand-dark); font-weight: 800; }
.form { margin: 16px 0; }
</style>
