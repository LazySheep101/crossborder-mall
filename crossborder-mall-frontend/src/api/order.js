import request from './request'

export const createOrder = (data) => request.post('/orders', data)
export const listOrders = (params) => request.get('/orders', { params })
export const getOrder = (orderNo) => request.get(`/orders/${orderNo}`)
export const cancelOrder = (orderNo, reason) =>
  request.put(`/orders/${orderNo}/cancel`, null, { params: { reason } })
export const confirmOrder = (orderNo) => request.put(`/orders/${orderNo}/confirm`)
export const mockPay = (orderNo, channel = 'MOCK') =>
  request.post('/payments/mock', { orderNo, channel })

export const adminListOrders = (params) => request.get('/admin/orders', { params })
export const adminGetOrder = (orderNo) => request.get(`/admin/orders/${orderNo}`)
export const adminShipOrder = (orderNo) => request.put(`/admin/orders/${orderNo}/ship`)
export const adminCancelOrder = (orderNo, reason) =>
  request.put(`/admin/orders/${orderNo}/cancel`, null, { params: { reason } })
export const adminDashboard = () => request.get('/admin/dashboard')
