<template>
  <div>
    <div class="head">
      <h2>收货地址</h2>
      <el-button type="primary" @click="openCreate">新增地址</el-button>
    </div>

    <article v-for="a in list" :key="a.id" class="card row">
      <div>
        <p>
          <b>{{ a.receiverName }}</b> {{ a.receiverPhone }}
          <el-tag v-if="a.isDefault === 1" size="small" type="danger">默认</el-tag>
        </p>
        <p class="muted">{{ a.province }}{{ a.city }}{{ a.district }}{{ a.detail }}</p>
      </div>
      <div class="ops">
        <el-button link @click="openEdit(a)">编辑</el-button>
        <el-button v-if="a.isDefault !== 1" link @click="onDefault(a)">设默认</el-button>
        <el-button link type="danger" @click="onDelete(a)">删除</el-button>
      </div>
    </article>
    <el-empty v-if="!list.length" description="暂无地址" />

    <el-dialog v-model="visible" :title="form.id ? '编辑地址' : '新增地址'" width="480px">
      <el-form label-width="80px">
        <el-form-item label="收货人"><el-input v-model="form.receiverName" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.receiverPhone" /></el-form-item>
        <el-form-item label="省"><el-input v-model="form.province" /></el-form-item>
        <el-form-item label="市"><el-input v-model="form.city" /></el-form-item>
        <el-form-item label="区"><el-input v-model="form.district" /></el-form-item>
        <el-form-item label="详细"><el-input v-model="form.detail" type="textarea" /></el-form-item>
        <el-form-item label="默认"><el-switch v-model="form.isDefault" /></el-form-item>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createAddress,
  deleteAddress,
  listAddresses,
  setDefaultAddress,
  updateAddress
} from '@/api/address'

const list = ref([])
const visible = ref(false)
const form = reactive({
  id: null,
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false
})

const load = async () => {
  list.value = (await listAddresses()) || []
}

const reset = () => {
  Object.assign(form, {
    id: null,
    receiverName: '',
    receiverPhone: '',
    province: '广东省',
    city: '深圳市',
    district: '南山区',
    detail: '',
    isDefault: false
  })
}

const openCreate = () => {
  reset()
  visible.value = true
}

const openEdit = (a) => {
  Object.assign(form, {
    id: a.id,
    receiverName: a.receiverName,
    receiverPhone: a.receiverPhone,
    province: a.province,
    city: a.city,
    district: a.district,
    detail: a.detail,
    isDefault: a.isDefault === 1
  })
  visible.value = true
}

const save = async () => {
  const payload = { ...form }
  if (form.id) {
    await updateAddress(form.id, payload)
  } else {
    await createAddress(payload)
  }
  ElMessage.success('已保存')
  visible.value = false
  await load()
}

const onDefault = async (a) => {
  await setDefaultAddress(a.id)
  await load()
}

const onDelete = async (a) => {
  await ElMessageBox.confirm('确认删除该地址？', '删除')
  await deleteAddress(a.id)
  await load()
}

onMounted(load)
</script>

<style scoped lang="scss">
.head { display: flex; justify-content: space-between; align-items: center; }
.row {
  margin-top: 12px;
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}
.ops { display: flex; gap: 4px; }
</style>
