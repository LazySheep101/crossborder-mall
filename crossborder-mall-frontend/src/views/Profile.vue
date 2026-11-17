<template>
  <div class="card">
    <h2>个人资料</h2>
    <el-form label-width="90px" style="max-width: 480px; margin-top: 16px">
      <el-form-item label="手机号">
        <el-input :model-value="form.phone" disabled />
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="form.nickName" maxlength="32" />
      </el-form-item>
      <el-form-item label="头像URL">
        <el-input v-model="form.icon" placeholder="可选，图片链接" />
      </el-form-item>
      <el-form-item label="角色">
        <el-tag>{{ form.role || 'USER' }}</el-tag>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getProfile, updateProfile } from '@/api/user'
import { useAuthStore } from '@/store/auth'
import { setUser } from '@/utils/storage'

const auth = useAuthStore()
const saving = ref(false)
const form = reactive({
  phone: '',
  nickName: '',
  icon: '',
  role: 'USER'
})

const load = async () => {
  const user = await getProfile()
  Object.assign(form, {
    phone: user.phone || '',
    nickName: user.nickName || '',
    icon: user.icon || '',
    role: user.role || 'USER'
  })
}

const save = async () => {
  saving.value = true
  try {
    const user = await updateProfile({ nickName: form.nickName, icon: form.icon })
    auth.user = user
    setUser(user)
    ElMessage.success('资料已更新')
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>
