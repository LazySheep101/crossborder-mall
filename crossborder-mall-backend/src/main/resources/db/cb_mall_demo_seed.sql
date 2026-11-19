-- 跨境商城 · 演示商品种子（含外链图片，可直接刷新买家首页）
-- 库：crossborder_mall
SET NAMES utf8mb4;

INSERT INTO `cb_category` (`id`, `name`, `parent_id`, `sort_order`, `status`) VALUES
(1, '美妆个护', 0, 1, 1),
(2, '数码电器', 0, 2, 1),
(3, '母婴用品', 0, 3, 1),
(4, '食品保健', 0, 4, 1),
(5, '服饰箱包', 0, 5, 1)
ON DUPLICATE KEY UPDATE `name`=VALUES(`name`), `sort_order`=VALUES(`sort_order`), `status`=VALUES(`status`);

INSERT INTO `cb_product`
(`id`, `category_id`, `name`, `subtitle`, `main_image`, `images`, `detail`, `origin_country`, `price`, `stock`, `status`, `sales_count`, `has_sku`)
VALUES
(1, 1, '资生堂红腰子精华 30ml', '修护屏障 · 跨境直邮',
 'https://images.unsplash.com/photo-1620916565915-0128c63e2a77?auto=format&fit=crop&w=800&q=80',
 'https://images.unsplash.com/photo-1620916565915-0128c63e2a77?auto=format&fit=crop&w=800&q=80',
 '原装进口精华，适合敏感肌日常修护，质地轻盈好吸收。', '日本', 289.00, 0, 1, 1286, 1),

(2, 2, '戴森 Supersonic 吹风机', '速干护发 · 英产直邮',
 'https://images.unsplash.com/photo-1522338242992-e1a54906a8da?auto=format&fit=crop&w=800&q=80',
 'https://images.unsplash.com/photo-1522338242992-e1a54906a8da?auto=format&fit=crop&w=800&q=80',
 '负离子护发，多档温控，吹发更顺滑。', '英国', 2890.00, 0, 1, 562, 1),

(3, 3, '爱他美卓萃奶粉 2段', '保税仓发货 · 正品保障',
 'https://images.unsplash.com/photo-1555252333-9f8e92e65df9?auto=format&fit=crop&w=800&q=80',
 'https://images.unsplash.com/photo-1555252333-9f8e92e65df9?auto=format&fit=crop&w=800&q=80',
 '适合 6-12 月龄宝宝，保税仓发货更快。', '德国', 268.00, 200, 1, 3208, 0),

(4, 4, 'Swisse 维生素C泡腾片', '每日维C · 跨境保税',
 'https://images.unsplash.com/photo-1550572017-edd951b55104?auto=format&fit=crop&w=800&q=80',
 'https://images.unsplash.com/photo-1550572017-edd951b55104?auto=format&fit=crop&w=800&q=80',
 '清新橙味，日常补充维C，便携易冲泡。', '澳大利亚', 89.00, 500, 1, 8901, 0),

(5, 5, 'Michael Kors 经典托特包', '通勤百搭 · 海外直邮',
 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=800&q=80',
 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=800&q=80',
 '经典托特造型，容量大，适合日常通勤。', '意大利', 1299.00, 0, 1, 428, 1),

(6, 1, '兰蔻小黑瓶精华 50ml', '抗初老修护 · 法产直邮',
 'https://images.unsplash.com/photo-1571781926291-c77da0226f55?auto=format&fit=crop&w=800&q=80',
 'https://images.unsplash.com/photo-1571781926291-c77da0226f55?auto=format&fit=crop&w=800&q=80',
 '肌底修护精华，改善肤质暗沉粗糙。', '法国', 699.00, 80, 1, 2145, 0),

(7, 1, 'SK-II 神仙水 230ml', '经典护肤水 · 日本直邮',
 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?auto=format&fit=crop&w=800&q=80',
 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?auto=format&fit=crop&w=800&q=80',
 'Pitera 经典配方，提亮肤色、细腻肌肤。', '日本', 1199.00, 60, 1, 1876, 0),

(8, 2, '索尼 WH-1000XM5 降噪耳机', '旗舰降噪 · 长续航',
 'https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?auto=format&fit=crop&w=800&q=80',
 'https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?auto=format&fit=crop&w=800&q=80',
 '自适应降噪，出行通勤听歌更沉浸。', '日本', 2299.00, 45, 1, 933, 0),

(9, 2, '苹果 AirPods Pro 2', '主动降噪 · 空间音频',
 'https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?auto=format&fit=crop&w=800&q=80',
 'https://images.unsplash.com/photo-1600294037681-c80b4cb5b434?auto=format&fit=crop&w=800&q=80',
 '无线充耳机盒，通透模式切换更自由。', '美国', 1899.00, 120, 1, 4521, 0),

(10, 3, '贝亲宽口径奶瓶 240ml', '防胀气设计 · 母婴优选',
 'https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?auto=format&fit=crop&w=800&q=80',
 'https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?auto=format&fit=crop&w=800&q=80',
 '宽口径好清洗，仿母乳实感奶嘴。', '日本', 128.00, 300, 1, 2670, 0),

(11, 4, 'Blackmores 深海鱼油 400粒', '呵护心脑 · 澳洲进口',
 'https://images.unsplash.com/photo-1505751172876-fa1923c5c528?auto=format&fit=crop&w=800&q=80',
 'https://images.unsplash.com/photo-1505751172876-fa1923c5c528?auto=format&fit=crop&w=800&q=80',
 '高纯度 Omega-3，日常营养补充。', '澳大利亚', 168.00, 260, 1, 3412, 0),

(12, 5, 'Coach 迷你单肩包', '轻奢通勤 · 美国直邮',
 'https://images.unsplash.com/photo-1590874103328-eac38a57489b?auto=format&fit=crop&w=800&q=80',
 'https://images.unsplash.com/photo-1590874103328-eac38a57489b?auto=format&fit=crop&w=800&q=80',
 '小号单肩，轻便出街，皮质细腻。', '美国', 1599.00, 35, 1, 512, 0),

(13, 1, '雅诗兰黛小棕瓶精华', '修护抗老 · 美国直邮',
 'https://images.unsplash.com/photo-1611930022073-b7a4ba5fcccd?auto=format&fit=crop&w=800&q=80',
 'https://images.unsplash.com/photo-1611930022073-b7a4ba5fcccd?auto=format&fit=crop&w=800&q=80',
 '夜间修护精华，改善细纹与粗糙。', '美国', 780.00, 90, 1, 2988, 0),

(14, 4, '明治蛋白粉 巧克力味', '运动补给 · 日本进口',
 'https://images.unsplash.com/photo-1593095948071-474c1767860f?auto=format&fit=crop&w=800&q=80',
 'https://images.unsplash.com/photo-1593095948071-474c1767860f?auto=format&fit=crop&w=800&q=80',
 '高蛋白低脂配方，健身代餐好选择。', '日本', 198.00, 180, 1, 1560, 0),

(15, 2, 'Kindle Paperwhite 电子书', '护眼阅读 · 美亚直邮',
 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=800&q=80',
 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=800&q=80',
 '暖光护眼屏，防水机身，通勤阅读更轻松。', '美国', 999.00, 70, 1, 1204, 0)
ON DUPLICATE KEY UPDATE
  `category_id`=VALUES(`category_id`),
  `name`=VALUES(`name`),
  `subtitle`=VALUES(`subtitle`),
  `main_image`=VALUES(`main_image`),
  `images`=VALUES(`images`),
  `detail`=VALUES(`detail`),
  `origin_country`=VALUES(`origin_country`),
  `price`=VALUES(`price`),
  `stock`=VALUES(`stock`),
  `status`=VALUES(`status`),
  `sales_count`=VALUES(`sales_count`),
  `has_sku`=VALUES(`has_sku`);

INSERT INTO `cb_product_sku` (`id`, `product_id`, `sku_code`, `spec_desc`, `price`, `stock`, `status`) VALUES
(1, 1, 'SH-RED-30', '30ml', 289.00, 120, 1),
(2, 1, 'SH-RED-50', '50ml', 429.00, 80, 1),
(3, 2, 'DY-HD08-PK', '粉色', 2890.00, 15, 1),
(4, 2, 'DY-HD08-BK', '黑色', 2890.00, 20, 1),
(5, 5, 'MK-TOT-BR', '棕色', 1299.00, 30, 1),
(6, 5, 'MK-TOT-BK', '黑色', 1299.00, 25, 1)
ON DUPLICATE KEY UPDATE
  `spec_desc`=VALUES(`spec_desc`),
  `price`=VALUES(`price`),
  `stock`=VALUES(`stock`),
  `status`=VALUES(`status`);

INSERT INTO `cb_seckill_activity`
(`id`, `title`, `product_id`, `sku_id`, `seckill_price`, `stock`, `sold`, `start_time`, `end_time`, `status`)
VALUES
(1, '资生堂红腰子限时秒杀', 1, 1, 199.00, 50, 0, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 7 DAY), 1),
(2, 'Swisse 维C限时秒杀', 4, 0, 59.00, 100, 0, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 7 DAY), 1),
(3, '兰蔻小黑瓶限时秒杀', 6, 0, 499.00, 30, 0, DATE_SUB(NOW(), INTERVAL 30 MINUTE), DATE_ADD(NOW(), INTERVAL 3 DAY), 1)
ON DUPLICATE KEY UPDATE
  `title`=VALUES(`title`),
  `product_id`=VALUES(`product_id`),
  `sku_id`=VALUES(`sku_id`),
  `seckill_price`=VALUES(`seckill_price`),
  `stock`=VALUES(`stock`),
  `start_time`=VALUES(`start_time`),
  `end_time`=VALUES(`end_time`),
  `status`=VALUES(`status`);
