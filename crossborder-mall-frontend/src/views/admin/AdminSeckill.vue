<template>
  <div>
    <div class="head">
      <h2>秒杀活动</h2>
      <el-button type="primary" @click="openCreate">新建活动</el-button>
    </div>
    <el-table :data="rows" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="productName" label="商品" />
      <el-table-column prop="seckillPrice" label="秒杀价" width="100" />
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">{{ statusLabel(row.status) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button v-if="row.status !== 1" link type="primary" @click="setStatus(row, 1)">上线</el-button>
          <el-button v-else link type="warning" @click="setStatus(row, 2)">下线</el-button>
          <el-button link @click="preheat(row)">预热库存</el-button>
          <el-button link @click="openEdit(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="form.id ? '编辑活动' : '新建活动'" width="520px">
      <el-form label-width="100px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="商品ID"><el-input-number v-model="form.productId" :min="1" /></el-form-item>
        <el-form-item label="SKU ID"><el-input-number v-model="form.skuId" :min="0" /></el-form-item>
        <el-form-item label="秒杀价"><el-input-number v-model="form.seckillPrice" :min="0.01" :step="0.01" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
        <el-form-item label="开始"><el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
        <el-form-item label="结束"><el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { adminListSeckill, adminPreheatSeckill, adminSaveSeckill, adminUpdateSeckillStatus } from '@/api/admin'

const rows = ref([])
const visible = ref(false)
const form = reactive({
  id: null,
  title: '',
  productId: 1,
  skuId: 0,
  seckillPrice: 99,
  stock: 50,
  startTime: '',
  endTime: '',
  status: 0
})

const statusLabel = (s) => ({ 0: '草稿', 1: '上线', 2: '下线' }[s] || s)

const load = async () => {
  rows.value = (await adminListSeckill()) || []
}

const openCreate = () => {
  const start = new Date()
  const end = new Date(Date.now() + 7 * 86400000)
  Object.assign(form, {
    id: null,
    title: '',
    productId: 1,
    skuId: 0,
    seckillPrice: 99,
    stock: 50,
    startTime: toLocal(start),
    endTime: toLocal(end),
    status: 0
  })
  visible.value = true
}

const openEdit = (row) => {
  Object.assign(form, {
    id: row.id,
    title: row.title,
    productId: row.productId,
    skuId: row.skuId || 0,
    seckillPrice: Number(row.seckillPrice),
    stock: row.stock,
    startTime: (row.startTime || '').replace(' ', 'T').slice(0, 19),
    endTime: (row.endTime || '').replace(' ', 'T').slice(0, 19),
    status: row.status
  })
  visible.value = true
}

const toLocal = (d) => {
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`
}

const save = async () => {
  await adminSaveSeckill({ ...form })
  ElMessage.success('已保存')
  visible.value = false
  await load()
}

const setStatus = async (row, status) => {
  await adminUpdateSeckillStatus(row.id, status)
  await load()
}

const preheat = async (row) => {
  await adminPreheatSeckill(row.id)
  ElMessage.success('Redis 库存已预热')
}

onMounted(load)
</script>

<style scoped lang="scss">
.head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
</style>
