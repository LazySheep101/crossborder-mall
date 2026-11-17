import request from './request'

export const adminListProducts = (params) => request.get('/admin/products', { params })
export const adminGetProduct = (id) => request.get(`/admin/products/${id}`)
export const adminSaveProduct = (data) => request.post('/admin/products', data)
export const adminUpdateProductStatus = (id, status) =>
  request.put(`/admin/products/${id}/status`, null, { params: { status } })
export const adminListSkus = (id) => request.get(`/admin/products/${id}/skus`)
export const adminSaveSkus = (id, skus) => request.put(`/admin/products/${id}/skus`, skus)
export const adminClearSkus = (id) => request.delete(`/admin/products/${id}/skus`)

export const adminListCategories = () => request.get('/admin/categories')
export const adminSaveCategory = (data) => request.post('/admin/categories', data)
export const adminDeleteCategory = (id) => request.delete(`/admin/categories/${id}`)

export const adminListSeckill = () => request.get('/admin/seckill')
export const adminSaveSeckill = (data) => request.post('/admin/seckill', data)
export const adminUpdateSeckillStatus = (id, status) =>
  request.put(`/admin/seckill/${id}/status`, null, { params: { status } })
export const adminPreheatSeckill = (id) => request.post(`/admin/seckill/${id}/preheat`)
