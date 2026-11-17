-- 已有库补秒杀活动列。新库若已用带 activity_id 的 cb_mall_trade.sql 建表，不要重复执行。
ALTER TABLE `cb_order`
  ADD COLUMN `activity_id` BIGINT NOT NULL DEFAULT 0 COMMENT '秒杀活动ID，普通订单为0' AFTER `source`;
