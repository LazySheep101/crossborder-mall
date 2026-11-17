import request from './request'

export const listAddresses = () => request.get('/addresses')
export const getDefaultAddress = () => request.get('/addresses/default')
export const createAddress = (data) => request.post('/addresses', data)
export const updateAddress = (id, data) => request.put(`/addresses/${id}`, data)
export const deleteAddress = (id) => request.delete(`/addresses/${id}`)
export const setDefaultAddress = (id) => request.put(`/addresses/${id}/default`)
