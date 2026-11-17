<template>
  <div>
    <div class="head">
      <h2>商品管理</h2>
      <el-button type="primary" @click="openCreate">新增商品</el-button>
    </div>
    <el-table :data="rows" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column label="SKU" width="70">
        <template #default="{ row }">{{ row.hasSku === 1 ? '有' : '无' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">{{ row.status === 1 ? '上架' : '下架' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button link @click="openEdit(row)">编辑</el-button>
          <el-button link type="primary" @click="openSku(row)">SKU</el-button>
          <el-button link type="warning" @click="toggle(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
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

    <el-dialog v-model="visible" :title="form.id ? '编辑商品' : '新增商品'" width="520px">
      <el-form label-width="90px">
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="副标题"><el-input v-model="form.subtitle" /></el-form-item>
        <el-form-item label="主图URL"><el-input v-model="form.mainImage" /></el-form-item>
        <el-form-item label="原产国"><el-input v-model="form.originCountry" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :step="0.01" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
        <el-form-item label="详情"><el-input v-model="form.detail" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="skuVisible" title="SKU / 库存" width="720px">
      <el-button size="small" @click="skus.push({ skuCode: '', specDesc: '', price: 99, stock: 10, status: 1 })">
        新增规格行
      </el-button>
      <el-table :data="skus" size="small" class="sku-table">
        <el-table-column label="规格文案">
          <template #default="{ row }"><el-input v-model="row.specDesc" size="small" /></template>
        </el-table-column>
        <el-table-column label="编码" width="120">
          <template #default="{ row }"><el-input v-model="row.skuCode" size="small" /></template>
        </el-table-column>
        <el-table-column label="价格" width="130">
          <template #default="{ row }">
            <el-input-number v-model="row.price" :min="0.01" :step="0.01" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="库存" width="120">
          <template #default="{ row }">
            <el-input-number v-model="row.stock" :min="0" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="" width="70">
          <template #default="{ $index }">
            <el-button link type="danger" @click="skus.splice($index, 1)">删</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="skuVisible = false">取消</el-button>
        <el-button type="primary" :loading="skuSaving" @click="saveSkus">保存 SKU</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  adminListCategories,
  adminListProducts,
  adminListSkus,
  adminSaveProduct,
  adminSaveSkus,
  adminUpdateProductStatus
} from '@/api/admin'

const rows = ref([])
const categories = ref([])
const total = ref(0)
const page = ref(1)
const size = 10
const visible = ref(false)
const skuVisible = ref(false)
const skuSaving = ref(false)
const currentProductId = ref(null)
const skus = ref([])
const form = reactive({
  id: null,
  categoryId: 1,
  name: '',
  subtitle: '',
  mainImage: '',
  originCountry: '',
  price: 99,
  stock: 100,
  detail: '',
  status: 1,
  hasSku: 0
})

const load = async () => {
  const data = await adminListProducts({ page: page.value, size })
  rows.value = data?.records || []
  total.value = Number(data?.total || 0)
}

const openCreate = () => {
  Object.assign(form, {
    id: null,
    categoryId: categories.value[0]?.id || 1,
    name: '',
    subtitle: '',
    mainImage: '/imgs/products/shiseido.jpg',
    originCountry: '日本',
    price: 99,
    stock: 100,
    detail: '',
    status: 1,
    hasSku: 0
  })
  visible.value = true
}

const openEdit = (row) => {
  Object.assign(form, {
    id: row.id,
    categoryId: row.categoryId,
    name: row.name,
    subtitle: row.subtitle,
    mainImage: row.mainImage,
    originCountry: row.originCountry,
    price: Number(row.price),
    stock: row.stock,
    detail: row.detail,
    status: row.status,
    hasSku: row.hasSku
  })
  visible.value = true
}

const save = async () => {
  await adminSaveProduct({ ...form })
  ElMessage.success('已保存')
  visible.value = false
  await load()
}

const toggle = async (row) => {
  await adminUpdateProductStatus(row.id, row.status === 1 ? 0 : 1)
  await load()
}

const openSku = async (row) => {
  currentProductId.value = row.id
  skus.value = (await adminListSkus(row.id)) || []
  if (!skus.value.length) {
    skus.value = [{ skuCode: '', specDesc: '默认', price: Number(row.price), stock: 10, status: 1 }]
  }
  skuVisible.value = true
}

const saveSkus = async () => {
  skuSaving.value = true
  try {
    await adminSaveSkus(currentProductId.value, skus.value)
    ElMessage.success('SKU 已保存')
    skuVisible.value = false
    await load()
  } finally {
    skuSaving.value = false
  }
}

onMounted(async () => {
  categories.value = (await adminListCategories()) || []
  await load()
})
</script>

<style scoped lang="scss">
.head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.pager { margin-top: 14px; justify-content: flex-end; }
.sku-table { margin-top: 12px; }
</style>
