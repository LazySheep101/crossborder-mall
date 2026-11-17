<template>
  <div>
    <div class="head">
      <h2>订单管理</h2>
      <el-radio-group v-model="status" size="small" @change="reload">
        <el-radio-button value="PAID">待发货</el-radio-button>
        <el-radio-button value="SHIPPED">已发货</el-radio-button>
        <el-radio-button value="UNPAID">待支付</el-radio-button>
        <el-radio-button value="">全部</el-radio-button>
      </el-radio-group>
    </div>
    <el-table :data="rows" stripe>
      <el-table-column prop="orderNo" label="订单号" min-width="160" />
      <el-table-column prop="receiverName" label="收货人" width="100" />
      <el-table-column prop="payAmount" label="金额" width="100" />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button link @click="openDetail(row)">详情</el-button>
          <el-button v-if="row.status === 'PAID'" link type="primary" @click="ship(row)">发货</el-button>
          <el-button
            v-if="row.status === 'UNPAID' || row.status === 'PAID'"
            link
            type="danger"
            @click="cancel(row)"
          >取消</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      class="pager"
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="size"
      v-model:current-page="page"
      @current-change="load"
    />

    <el-drawer v-model="drawer" title="订单详情" size="420px">
      <template v-if="detail">
        <p><b>{{ detail.orderNo }}</b> · {{ detail.status }}</p>
        <p class="muted">{{ detail.receiverName }} {{ detail.receiverPhone }}</p>
        <p class="muted">{{ detail.receiverAddress }}</p>
        <p>应付 ¥{{ Number(detail.payAmount).toFixed(2) }}</p>
        <div v-for="item in detail.items || []" :key="item.id" class="line">
          <span>{{ item.productName }} {{ item.specDesc }}</span>
          <span>x{{ item.quantity }}</span>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminCancelOrder, adminGetOrder, adminListOrders, adminShipOrder } from '@/api/order'

const status = ref('PAID')
const rows = ref([])
const page = ref(1)
const size = 10
const total = ref(0)
const drawer = ref(false)
const detail = ref(null)

const load = async () => {
  const data = await adminListOrders({
    status: status.value || undefined,
    page: page.value,
    size
  })
  rows.value = data?.records || []
  total.value = Number(data?.total || 0)
}

const reload = async () => {
  page.value = 1
  await load()
}

const openDetail = async (row) => {
  detail.value = await adminGetOrder(row.orderNo)
  drawer.value = true
}

const ship = async (row) => {
  await adminShipOrder(row.orderNo)
  ElMessage.success('已发货')
  await load()
}

const cancel = async (row) => {
  await ElMessageBox.confirm('确认取消该订单并回滚库存？', '取消订单')
  await adminCancelOrder(row.orderNo, '管理员取消')
  ElMessage.success('已取消')
  await load()
}

onMounted(load)
</script>

<style scoped lang="scss">
.head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; gap: 12px; flex-wrap: wrap; }
.pager { margin-top: 14px; justify-content: flex-end; }
.line {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid var(--line);
}
</style>
