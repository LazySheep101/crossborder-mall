-- 去掉黑马点评里不用于跨境商城的教学表。保留 tb_user（手机验证码登录）。
DROP TABLE IF EXISTS `tb_blog_comments`;
DROP TABLE IF EXISTS `tb_blog`;
DROP TABLE IF EXISTS `tb_follow`;
DROP TABLE IF EXISTS `tb_voucher_order`;
DROP TABLE IF EXISTS `tb_seckill_voucher`;
DROP TABLE IF EXISTS `tb_voucher`;
DROP TABLE IF EXISTS `tb_shop`;
DROP TABLE IF EXISTS `tb_shop_type`;
DROP TABLE IF EXISTS `tb_user_info`;
DROP TABLE IF EXISTS `tb_sign`;
