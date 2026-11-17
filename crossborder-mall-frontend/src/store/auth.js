import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi, me as meApi, sendCode as sendCodeApi } from '@/api/auth'
import { clearAuth, getToken, getUser, setToken, setUser } from '@/utils/storage'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken(),
    user: getUser()
  }),
  getters: {
    isLoggedIn: (s) => !!s.token,
    isAdmin: (s) => s.user?.role === 'ADMIN'
  },

  actions: {
    async sendCode(phone) {
      await sendCodeApi(phone)
    },
    async login(form) {
      const data = await loginApi(form)
      this.token = data.token
      this.user = data.user
      setToken(data.token)
      setUser(data.user)
    },
    async fetchMe() {
      if (!this.token) return
      const user = await meApi()
      this.user = user
      setUser(user)
    },
    async logout() {
      try {
        if (this.token) await logoutApi()
      } catch {
        // 本地会话仍然清掉
      }
      this.token = null
      this.user = null
      clearAuth()
    }
  }
})
