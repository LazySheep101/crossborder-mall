<template>
  <div>
    <div class="head">
      <h2>分类管理</h2>
      <el-button type="primary" @click="openCreate">新增分类</el-button>
    </div>
    <el-table :data="rows" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="sortOrder" label="排序" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">{{ row.status === 1 ? '启用' : '停用' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="form.id ? '编辑分类' : '新增分类'" width="420px">
      <el-form label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminDeleteCategory, adminListCategories, adminSaveCategory } from '@/api/admin'

const rows = ref([])
const visible = ref(false)
const form = reactive({ id: null, name: '', sortOrder: 0, status: 1 })
const enabled = computed({
  get: () => form.status === 1,
  set: (v) => { form.status = v ? 1 : 0 }
})

const load = async () => {
  rows.value = (await adminListCategories()) || []
}

const openCreate = () => {
  Object.assign(form, { id: null, name: '', sortOrder: 0, status: 1 })
  visible.value = true
}

const openEdit = (row) => {
  Object.assign(form, { id: row.id, name: row.name, sortOrder: row.sortOrder, status: row.status })
  visible.value = true
}

const save = async () => {
  await adminSaveCategory({ ...form })
  ElMessage.success('已保存')
  visible.value = false
  await load()
}

const onDelete = async (row) => {
  await ElMessageBox.confirm(`删除分类「${row.name}」？`, '删除')
  await adminDeleteCategory(row.id)
  await load()
}

onMounted(load)
</script>

<style scoped lang="scss">
.head { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
</style>
