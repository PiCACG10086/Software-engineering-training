-- 修复订单表缺少 order_number 字段的问题
USE bookstore;

-- 检查并添加 order_number 字段（如果不存在）
-- 注意：如果字段已存在，此语句会报错，可以忽略
-- ALTER TABLE t_order ADD COLUMN order_number VARCHAR(50) UNIQUE AFTER id;

-- 为现有订单生成订单号（如果订单号为空的话）
UPDATE t_order SET order_number = CONCAT('ORD', DATE_FORMAT(create_time, '%Y%m%d'), LPAD(id, 6, '0')) WHERE order_number IS NULL OR order_number = '';

-- 验证表结构
DESCRIBE t_order;

-- 显示修复结果
SELECT 'Order table fixed successfully!' as status;