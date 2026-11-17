-- 跨境商城 · 交易域表（D2）
-- 库：crossborder_mall

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `cb_address` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `receiver_name` VARCHAR(64) NOT NULL,
  `receiver_phone` VARCHAR(20) NOT NULL,
  `province` VARCHAR(64) NOT NULL DEFAULT '',
  `city` VARCHAR(64) NOT NULL DEFAULT '',
  `district` VARCHAR(64) NOT NULL DEFAULT '',
  `detail` VARCHAR(255) NOT NULL,
  `is_default` TINYINT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址';

CREATE TABLE IF NOT EXISTS `cb_cart_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `product_id` BIGINT NOT NULL,
  `sku_id` BIGINT NOT NULL DEFAULT 0,
  `quantity` INT NOT NULL DEFAULT 1,
  `checked` TINYINT NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product_sku` (`user_id`, `product_id`, `sku_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车';

CREATE TABLE IF NOT EXISTS `cb_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_no` VARCHAR(64) NOT NULL,
  `user_id` BIGINT NOT NULL,
  `total_amount` DECIMAL(12,2) NOT NULL,
  `pay_amount` DECIMAL(12,2) NOT NULL,
  `freight_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `status` VARCHAR(20) NOT NULL COMMENT 'UNPAID/PAID/SHIPPED/COMPLETED/CANCELLED',
  `source` TINYINT NOT NULL DEFAULT 0 COMMENT '0普通 1秒杀',
  `activity_id` BIGINT NOT NULL DEFAULT 0 COMMENT '秒杀活动ID，普通订单为0',
  `receiver_name` VARCHAR(64) NOT NULL,
  `receiver_phone` VARCHAR(20) NOT NULL,
  `receiver_address` VARCHAR(500) NOT NULL,
  `remark` VARCHAR(255) DEFAULT NULL,
  `pay_time` DATETIME DEFAULT NULL,
  `ship_time` DATETIME DEFAULT NULL,
  `finish_time` DATETIME DEFAULT NULL,
  `cancel_time` DATETIME DEFAULT NULL,
  `cancel_reason` VARCHAR(100) DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单头';

CREATE TABLE IF NOT EXISTS `cb_order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `order_no` VARCHAR(64) NOT NULL,
  `product_id` BIGINT NOT NULL,
  `sku_id` BIGINT NOT NULL DEFAULT 0,
  `product_name` VARCHAR(200) NOT NULL,
  `product_image` VARCHAR(500) DEFAULT NULL,
  `spec_desc` VARCHAR(200) DEFAULT '',
  `unit_price` DECIMAL(10,2) NOT NULL,
  `quantity` INT NOT NULL,
  `total_price` DECIMAL(12,2) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单行';

CREATE TABLE IF NOT EXISTS `cb_payment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `payment_no` VARCHAR(64) NOT NULL,
  `order_no` VARCHAR(64) NOT NULL,
  `user_id` BIGINT NOT NULL,
  `amount` DECIMAL(12,2) NOT NULL,
  `channel` VARCHAR(32) NOT NULL DEFAULT 'MOCK' COMMENT 'MOCK/ALIPAY/WECHAT',
  `status` VARCHAR(20) NOT NULL COMMENT 'PENDING/SUCCESS/FAILED/CLOSED',
  `trade_no` VARCHAR(64) DEFAULT NULL,
  `paid_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付单';
