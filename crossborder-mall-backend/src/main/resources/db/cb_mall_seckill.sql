-- 跨境商城 · 商品秒杀活动（挂 SKU）
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `cb_seckill_activity` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `product_id` BIGINT NOT NULL,
  `sku_id` BIGINT NOT NULL DEFAULT 0,
  `seckill_price` DECIMAL(10,2) NOT NULL,
  `stock` INT NOT NULL DEFAULT 0,
  `sold` INT NOT NULL DEFAULT 0,
  `start_time` DATETIME NOT NULL,
  `end_time` DATETIME NOT NULL,
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0草稿 1上线 2下线',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_status_time` (`status`, `start_time`, `end_time`),
  KEY `idx_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品秒杀活动';

INSERT INTO `cb_seckill_activity`
(`id`, `title`, `product_id`, `sku_id`, `seckill_price`, `stock`, `sold`, `start_time`, `end_time`, `status`)
VALUES
(1, '资生堂红腰子 秒杀', 1, 1, 199.00, 50, 0, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 7 DAY), 1),
(2, 'Swisse 维C 秒杀', 4, 0, 59.00, 100, 0, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 7 DAY), 1)
ON DUPLICATE KEY UPDATE `title`=VALUES(`title`), `status`=VALUES(`status`);
