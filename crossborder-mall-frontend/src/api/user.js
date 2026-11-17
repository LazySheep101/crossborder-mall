import request from './request'

export const getUser = (id) => request.get(`/users/${id}`)
export const getProfile = () => request.get('/users/profile')
export const updateProfile = (data) => request.put('/users/profile', data)
