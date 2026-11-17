import request from './request'

export const listCart = () => request.get('/cart')
export const cartSummary = () => request.get('/cart/summary')
export const addCart = (data) => request.post('/cart', data)
export const updateCartQuantity = (id, quantity) =>
  request.put(`/cart/${id}/quantity`, null, { params: { quantity } })
export const updateCartChecked = (id, checked) =>
  request.put(`/cart/${id}/checked`, null, { params: { checked } })
export const checkAllCart = (checked) =>
  request.put('/cart/checked', null, { params: { checked } })
export const removeCart = (id) => request.delete(`/cart/${id}`)
