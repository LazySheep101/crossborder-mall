-- 1. 参数列表
-- 1.1. 秒杀活动 id
local activityId = ARGV[1]
-- 1.2. 用户id
local userId = ARGV[2]

-- 2. 数据key
-- 2.1. 库存key
local stockKey = 'seckill:stock:' .. activityId
-- 2.2. 已下单用户集合（一人一单）
local orderKey = 'seckill:order:' .. activityId

-- 3. 脚本业务
-- 3.1. 判断库存是否充足（未预热视为无库存）
local stock = tonumber(redis.call('get', stockKey))
if (stock == nil or stock <= 0) then
    -- 3.2. 库存不足，返回1
    return 1
end
-- 3.2. 判断用户是否下单
if (redis.call('sismember', orderKey, userId) == 1) then
    -- 3.3. 存在，说明是重复下单，返回2
    return 2
end
-- 3.4. 扣库存
redis.call('incrby', stockKey, -1)
-- 3.5. 下单（保存用户）
redis.call('sadd', orderKey, userId)
-- 3.6. 成功，返回0
return 0
