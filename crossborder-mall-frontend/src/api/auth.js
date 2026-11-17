import request from './request'

export const sendCode = (phone) => request.post(`/auth/code?phone=${encodeURIComponent(phone)}`)
export const login = (data) => request.post('/auth/login', data)
export const logout = () => request.post('/auth/logout')
export const me = () => request.get('/auth/me')
