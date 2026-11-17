-- 跨境商城 · 交易域表（D1 商品）
-- 库：crossborder_mall

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `cb_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `parent_id` BIGINT NOT NULL DEFAULT 0,
  `sort_order` INT NOT NULL DEFAULT 0,
  `icon` VARCHAR(500) DEFAULT NULL,
  `status` TINYINT NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类';

CREATE TABLE IF NOT EXISTS `cb_product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `category_id` BIGINT NOT NULL,
  `name` VARCHAR(200) NOT NULL,
  `subtitle` VARCHAR(500) DEFAULT NULL,
  `main_image` VARCHAR(500) DEFAULT NULL,
  `images` TEXT,
  `detail` TEXT,
  `origin_country` VARCHAR(64) DEFAULT NULL COMMENT '原产国/地区',
  `price` DECIMAL(10,2) NOT NULL,
  `stock` INT NOT NULL DEFAULT 0 COMMENT '无SKU时使用',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '0下架 1上架',
  `sales_count` INT NOT NULL DEFAULT 0,
  `has_sku` TINYINT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SPU';

CREATE TABLE IF NOT EXISTS `cb_product_sku` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT NOT NULL,
  `sku_code` VARCHAR(64) DEFAULT NULL,
  `spec_desc` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '规格文案，如 红色/256G',
  `price` DECIMAL(10,2) NOT NULL,
  `stock` INT NOT NULL DEFAULT 0,
  `status` TINYINT NOT NULL DEFAULT 1,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU';

-- 种子分类
INSERT INTO `cb_category` (`id`, `name`, `parent_id`, `sort_order`, `status`) VALUES
(1, '美妆个护', 0, 1, 1),
(2, '数码电器', 0, 2, 1),
(3, '母婴用品', 0, 3, 1),
(4, '食品保健', 0, 4, 1),
(5, '服饰箱包', 0, 5, 1)
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`);

-- 种子商品（跨境）
INSERT INTO `cb_product` (`id`, `category_id`, `name`, `subtitle`, `main_image`, `detail`, `origin_country`, `price`, `stock`, `status`, `sales_count`, `has_sku`) VALUES
(1, 1, '日本资生堂红腰子精华', '跨境直邮 · 修护屏障', '/imgs/products/shiseido.jpg', '原装进口精华，适合敏感肌日常修护。', '日本', 289.00, 0, 1, 128, 1),
(2, 2, '戴森吹风机 HD08', '英产跨境 · 速干护发', '/imgs/products/dyson.jpg', '负离子护发，多档温控。', '英国', 2890.00, 0, 1, 56, 1),
(3, 3, '德国爱他美奶粉 2段', '保税仓发货', '/imgs/products/aptamil.jpg', '适合6-12月龄。', '德国', 268.00, 200, 1, 320, 0),
(4, 4, '澳洲 Swisse 维生素C泡腾片', '跨境保税', '/imgs/products/swisse.jpg', '日常补充维C。', '澳大利亚', 89.00, 500, 1, 890, 0),
(5, 5, '意大利 Michael Kors 托特包', '海外直邮', '/imgs/products/mk-bag.jpg', '经典托特，通勤百搭。', '意大利', 1299.00, 0, 1, 42, 1)
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`), `main_image`=VALUES(`main_image`);

INSERT INTO `cb_product_sku` (`id`, `product_id`, `sku_code`, `spec_desc`, `price`, `stock`, `status`) VALUES
(1, 1, 'SH-RED-30', '30ml', 289.00, 120, 1),
(2, 1, 'SH-RED-50', '50ml', 429.00, 80, 1),
(3, 2, 'DY-HD08-PK', '粉色', 2890.00, 15, 1),
(4, 2, 'DY-HD08-BK', '黑色', 2890.00, 20, 1),
(5, 5, 'MK-TOT-BR', '棕色', 1299.00, 30, 1),
(6, 5, 'MK-TOT-BK', '黑色', 1299.00, 25, 1)
ON DUPLICATE KEY UPDATE `spec_desc`=VALUES(`spec_desc`);
