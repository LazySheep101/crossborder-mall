<template>
  <div class="login-page">
    <div class="panel card">
      <h1>跨境商城</h1>
      <p class="muted">短信验证码登录（验证码看后端日志）</p>
      <el-form @submit.prevent="onLogin">
        <el-form-item>
          <el-input v-model="form.phone" placeholder="手机号" size="large" />
        </el-form-item>
        <el-form-item>
          <div class="code-row">
            <el-input v-model="form.code" placeholder="验证码" size="large" />
            <el-button :disabled="countdown > 0" @click="onSend">
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="onLogin">
          登录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/store/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()
const form = reactive({ phone: '', code: '' })
const loading = ref(false)
const countdown = ref(0)
let timer

const onSend = async () => {
  if (!/^1\d{10}$/.test(form.phone)) {
    ElMessage.warning('请输入正确手机号')
    return
  }
  await auth.sendCode(form.phone)
  ElMessage.success('验证码已发送（见后端日志）')
  countdown.value = 60
  timer = setInterval(() => {
    countdown.value -= 1
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
}

const onLogin = async () => {
  loading.value = true
  try {
    await auth.login(form)
    ElMessage.success('登录成功')
    router.replace(route.query.redirect || '/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
}
.panel {
  width: min(420px, 100%);
  h1 {
    margin: 0 0 8px;
    color: var(--brand-dark);
  }
}
.code-row {
  display: flex;
  gap: 8px;
  width: 100%;
}
</style>
