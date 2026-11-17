import { createRouter, createWebHistory } from 'vue-router'
import { getToken, getUser } from '@/utils/storage'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      children: [
        { path: '', name: 'home', component: () => import('@/views/Home.vue'), meta: { title: '首页' } },
        { path: 'products', name: 'products', component: () => import('@/views/ProductList.vue'), meta: { title: '商品' } },
        { path: 'products/:id', name: 'product-detail', component: () => import('@/views/ProductDetail.vue'), meta: { title: '商品详情' } },
        { path: 'seckills', name: 'seckills', component: () => import('@/views/SeckillList.vue'), meta: { title: '秒杀' } },
        { path: 'seckills/:id', name: 'seckill-detail', component: () => import('@/views/SeckillDetail.vue'), meta: { title: '秒杀详情', requiresAuth: true } },
        { path: 'cart', name: 'cart', component: () => import('@/views/Cart.vue'), meta: { title: '购物车', requiresAuth: true } },
        { path: 'checkout', name: 'checkout', component: () => import('@/views/Checkout.vue'), meta: { title: '确认订单', requiresAuth: true } },
        { path: 'payment/:orderNo', name: 'payment', component: () => import('@/views/Payment.vue'), meta: { title: '支付', requiresAuth: true } },
        { path: 'orders', name: 'orders', component: () => import('@/views/Orders.vue'), meta: { title: '我的订单', requiresAuth: true } },
        { path: 'orders/:orderNo', name: 'order-detail', component: () => import('@/views/OrderDetail.vue'), meta: { title: '订单详情', requiresAuth: true } },
        { path: 'addresses', name: 'addresses', component: () => import('@/views/Addresses.vue'), meta: { title: '收货地址', requiresAuth: true } },
        { path: 'profile', name: 'profile', component: () => import('@/views/Profile.vue'), meta: { title: '个人资料', requiresAuth: true } },
        { path: 'admin', name: 'admin-dashboard', component: () => import('@/views/admin/AdminDashboard.vue'), meta: { title: '管理看板', requiresAuth: true, requiresAdmin: true } },
        { path: 'admin/products', name: 'admin-products', component: () => import('@/views/admin/AdminProducts.vue'), meta: { title: '商品管理', requiresAuth: true, requiresAdmin: true } },
        { path: 'admin/categories', name: 'admin-categories', component: () => import('@/views/admin/AdminCategories.vue'), meta: { title: '分类管理', requiresAuth: true, requiresAdmin: true } },
        { path: 'admin/orders', name: 'admin-orders', component: () => import('@/views/admin/AdminOrders.vue'), meta: { title: '订单管理', requiresAuth: true, requiresAdmin: true } },
        { path: 'admin/seckill', name: 'admin-seckill', component: () => import('@/views/admin/AdminSeckill.vue'), meta: { title: '秒杀管理', requiresAuth: true, requiresAdmin: true } },
        { path: 'me', name: 'me', component: () => import('@/views/Me.vue'), meta: { title: '我的', requiresAuth: true } }
      ]
    },
    { path: '/login', name: 'login', component: () => import('@/views/Login.vue'), meta: { title: '登录' } }
  ]
})

router.beforeEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} - 跨境商城` : '跨境商城'
  if (to.meta.requiresAuth && !getToken()) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requiresAdmin && getUser()?.role !== 'ADMIN') {
    return { name: 'home' }
  }
})

export default router
