-- 数据库性能优化脚本
-- 为高校教材购销系统添加索引和优化配置

-- 1. 为图书表添加索引
-- 标题索引（用于搜索）
CREATE INDEX idx_book_title ON t_book(title);

-- 作者索引
CREATE INDEX idx_book_author ON t_book(author);

-- 出版社索引
CREATE INDEX idx_book_publisher ON t_book(publisher);

-- ISBN索引（如果不是主键的话）
CREATE INDEX idx_book_isbn ON t_book(isbn);

-- 价格索引（用于价格范围查询）
CREATE INDEX idx_book_price ON t_book(price);

-- 库存索引（用于库存查询）
CREATE INDEX idx_book_stock ON t_book(stock);

-- 2. 为订单表添加索引
-- 学生ID索引
CREATE INDEX idx_order_student_id ON t_order(student_id);

-- 订单状态索引
CREATE INDEX idx_order_status ON t_order(status);

-- 创建时间索引
CREATE INDEX idx_order_create_time ON t_order(create_time);

-- 订单号索引（如果不是唯一索引的话）
CREATE INDEX idx_order_number ON t_order(order_number);

-- 复合索引：学生ID + 状态（用于用户查看自己的特定状态订单）
CREATE INDEX idx_order_student_status ON t_order(student_id, status);

-- 复合索引：状态 + 创建时间（用于管理员按状态和时间查询）
CREATE INDEX idx_order_status_time ON t_order(status, create_time);

-- 3. 为订单详情表添加索引
-- 订单ID索引
CREATE INDEX idx_order_detail_order_id ON t_order_detail(order_id);

-- 图书ID索引
CREATE INDEX idx_order_detail_book_id ON t_order_detail(book_id);

-- 复合索引：订单ID + 图书ID
CREATE INDEX idx_order_detail_order_book ON t_order_detail(order_id, book_id);

-- 4. 为用户表添加索引
-- 用户名索引（如果不是唯一索引的话）
CREATE INDEX idx_user_username ON t_user(username);

-- 角色索引
CREATE INDEX idx_user_role ON t_user(role);

-- 学号索引
CREATE INDEX idx_user_student_id ON t_user(student_id);

-- 创建时间索引
CREATE INDEX idx_user_create_time ON t_user(create_time);

-- 5. 为购物车表添加索引（如果存在）
-- 用户ID索引
-- CREATE INDEX idx_cart_user_id ON t_cart(user_id);

-- 图书ID索引
-- CREATE INDEX idx_cart_book_id ON t_cart(book_id);

-- 复合索引：用户ID + 图书ID
-- CREATE INDEX idx_cart_user_book ON t_cart(user_id, book_id);

-- 6. MySQL性能优化配置建议
-- 以下配置需要在MySQL配置文件(my.cnf或my.ini)中设置

/*
# InnoDB缓冲池大小（建议设置为可用内存的70-80%）
innodb_buffer_pool_size = 1G

# 查询缓存大小
query_cache_size = 64M
query_cache_type = 1

# 连接数配置
max_connections = 200
max_connect_errors = 1000

# 慢查询日志
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2

# 二进制日志
log_bin = mysql-bin
expire_logs_days = 7

# InnoDB配置
innodb_log_file_size = 256M
innodb_log_buffer_size = 16M
innodb_flush_log_at_trx_commit = 2
innodb_file_per_table = 1

# 表缓存
table_open_cache = 2000

# 排序缓冲区
sort_buffer_size = 2M
read_buffer_size = 2M
read_rnd_buffer_size = 8M

# 临时表
tmp_table_size = 64M
max_heap_table_size = 64M
*/

-- 7. 分析表统计信息（定期执行）
ANALYZE TABLE t_book;
ANALYZE TABLE t_order;
ANALYZE TABLE t_order_detail;
ANALYZE TABLE t_user;

-- 8. 优化表（定期执行）
OPTIMIZE TABLE t_book;
OPTIMIZE TABLE t_order;
OPTIMIZE TABLE t_order_detail;
OPTIMIZE TABLE t_user;

-- 9. 查看索引使用情况的查询
-- 查看表的索引信息
-- SHOW INDEX FROM t_book;
-- SHOW INDEX FROM t_order;
-- SHOW INDEX FROM t_order_detail;
-- SHOW INDEX FROM t_user;

-- 查看慢查询
-- SHOW VARIABLES LIKE 'slow_query%';
-- SHOW STATUS LIKE 'Slow_queries';

-- 查看查询缓存状态
-- SHOW STATUS LIKE 'Qcache%';

-- 查看InnoDB状态
-- SHOW ENGINE INNODB STATUS;

-- 10. 常用性能分析查询
-- 查看正在执行的查询
-- SHOW PROCESSLIST;

-- 查看表大小
-- SELECT 
--     table_name AS '表名',
--     round(((data_length + index_length) / 1024 / 1024), 2) AS '大小(MB)'
-- FROM information_schema.tables 
-- WHERE table_schema = 'bookstore'
-- ORDER BY (data_length + index_length) DESC;

-- 查看索引使用统计
-- SELECT 
--     object_schema,
--     object_name,
--     index_name,
--     count_read,
--     count_write,
--     count_fetch,
--     count_insert,
--     count_update,
--     count_delete
-- FROM performance_schema.table_io_waits_summary_by_index_usage
-- WHERE object_schema = 'bookstore';

COMMIT;