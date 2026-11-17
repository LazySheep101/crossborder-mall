import request from './request'

export const listSeckillActivities = () => request.get('/seckill-activities')
export const getSeckillActivity = (id) => request.get(`/seckill-activities/${id}`)
export const placeSeckillOrder = (id, addressId) =>
  request.post(`/seckill-activities/${id}/orders`, { addressId })
export const seckillResult = (id) => request.get(`/seckill-activities/${id}/result`)
