-- MySQL 5.5.40+

-- 创建数据库
CREATE DATABASE IF NOT EXISTS bookstore DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE bookstore;

-- 1. 用户表 (t_user)
CREATE TABLE t_user (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    role ENUM('STUDENT', 'TEACHER', 'ADMIN') NOT NULL COMMENT '用户角色',
    name VARCHAR(100) NOT NULL COMMENT '真实姓名',
    student_id VARCHAR(20) COMMENT '学号(仅学生使用)',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';




-- 4. 教材表 (t_book)
CREATE TABLE t_book (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '图书ID',
    title VARCHAR(200) NOT NULL COMMENT '书名',
    author VARCHAR(100) NOT NULL COMMENT '作者',
    publisher VARCHAR(100) NOT NULL COMMENT '出版社',
    isbn VARCHAR(20) UNIQUE COMMENT 'ISBN号',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    stock INT DEFAULT 0 COMMENT '库存数量',
    description TEXT COMMENT '图书描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='图书表';



-- 6. 订单表 (t_order)
CREATE TABLE t_order (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_number VARCHAR(50) UNIQUE COMMENT '订单号',
    user_id INT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status ENUM('PENDING', 'PAID', 'CONFIRMED', 'SHIPPED', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING' COMMENT '订单状态',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES t_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单表';

-- 7. 订单详情表 (t_order_item)
CREATE TABLE t_order_item (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID',
    order_id INT NOT NULL COMMENT '订单ID',
    book_id INT NOT NULL COMMENT '图书ID',
    quantity INT NOT NULL COMMENT '数量',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    FOREIGN KEY (order_id) REFERENCES t_order(id),
    FOREIGN KEY (book_id) REFERENCES t_book(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单详情表';

-- 8. 购物车表 (t_cart)
CREATE TABLE t_cart (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车ID',
    user_id INT NOT NULL COMMENT '用户ID',
    book_id INT NOT NULL COMMENT '图书ID',
    quantity INT NOT NULL COMMENT '数量',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES t_user(id),
    FOREIGN KEY (book_id) REFERENCES t_book(id),
    UNIQUE KEY unique_user_book (user_id, book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='购物车表';

-- 插入初始数据

-- 插入管理员用户
INSERT INTO t_user (username, password, role, name) VALUES
('admin', 'admin123', 'ADMIN', '系统管理员');

-- 插入教师用户
INSERT INTO t_user (username, password, role, name) VALUES
('teacher001', 'teacher123', 'TEACHER', '张教授'),
('teacher002', 'teacher123', 'TEACHER', '李老师'),
('teacher003', 'teacher123', 'TEACHER', '王副教授'),
('teacher004', 'teacher123', 'TEACHER', '刘老师'),
('teacher005', 'teacher123', 'TEACHER', '陈教授'),
('teacher006', 'teacher123', 'TEACHER', '赵老师');

-- 插入学生用户
INSERT INTO t_user (username, password, role, name, student_id) VALUES
('student001', 'student123', 'STUDENT', '张小明', '2021001'),
('student002', 'student123', 'STUDENT', '李小红', '2021002'),
('student003', 'student123', 'STUDENT', '王小强', '2021003'),
('student004', 'student123', 'STUDENT', '刘小芳', '2021004'),
('student005', 'student123', 'STUDENT', '陈小华', '2021005'),
('student006', 'student123', 'STUDENT', '赵小军', '2021006');



-- 插入图书数据
INSERT INTO t_book (title, author, publisher, isbn, price, stock, description) VALUES
('数据结构(C语言版)', '严蔚敏', '清华大学出版社', '9787302147510', 45.00, 100, '经典的数据结构教材，适合计算机专业学生'),
('算法导论(原书第3版)', '托马斯·科尔曼', '机械工业出版社', '9787111407010', 128.00, 80, '算法领域的权威教材'),
('Java核心技术 卷I', '凯·霍斯特曼', '机械工业出版社', '9787111213826', 119.00, 60, 'Java编程经典教材'),
('计算机网络(第7版)', '谢希仁', '电子工业出版社', '9787121302954', 59.00, 90, '计算机网络基础教材'),
('操作系统概念(原书第9版)', '亚伯拉罕·西尔伯沙茨', '高等教育出版社', '9787040396630', 89.00, 70, '操作系统理论与实践'),
('数据库系统概论(第5版)', '王珊', '高等教育出版社', '9787040406641', 65.00, 85, '数据库系统经典教材'),
('高等数学(上册)', '同济大学数学系', '高等教育出版社', '9787040396621', 56.80, 120, '高等数学经典教材上册'),
('高等数学(下册)', '同济大学数学系', '高等教育出版社', '9787040396631', 52.30, 110, '高等数学经典教材下册'),
('线性代数(第六版)', '同济大学数学系', '高等教育出版社', '9787040396645', 39.20, 100, '线性代数标准教材'),
('概率论与数理统计', '盛骤', '高等教育出版社', '9787040238969', 42.50, 95, '概率统计基础教材'),
('数学分析(上册)', '华东师范大学数学系', '高等教育出版社', '9787040183184', 48.60, 75, '数学分析理论教材'),
('大学物理(上册)', '张三慧', '清华大学出版社', '9787302112174', 45.00, 90, '大学物理基础教材上册'),
('大学物理(下册)', '张三慧', '清华大学出版社', '9787302112181', 43.00, 85, '大学物理基础教材下册'),
('普通物理学(第七版)', '程守洙', '高等教育出版社', '9787040396652', 89.00, 70, '普通物理学经典教材'),
('新视野大学英语1', '郑树棠', '外语教学与研究出版社', '9787513533348', 39.90, 150, '大学英语基础教材第一册'),
('新视野大学英语2', '郑树棠', '外语教学与研究出版社', '9787513533355', 42.90, 140, '大学英语基础教材第二册'),
('大学英语综合教程1', '李荫华', '上海外语教育出版社', '9787544627023', 45.00, 130, '大学英语综合训练教材'),
('西方经济学(微观部分)', '高鸿业', '中国人民大学出版社', '9787300248967', 48.00, 80, '微观经济学经典教材'),
('西方经济学(宏观部分)', '高鸿业', '中国人民大学出版社', '9787300248974', 46.00, 75, '宏观经济学经典教材'),
('管理学原理与方法', '周三多', '复旦大学出版社', '9787309132052', 52.00, 70, '管理学基础理论教材'),
('会计学原理', '葛家澍', '中国人民大学出版社', '9787300234567', 55.00, 65, '会计学入门教材'),
('中国近现代史纲要', '本书编写组', '高等教育出版社', '9787040396659', 28.00, 200, '思政课必修教材'),
('马克思主义基本原理概论', '本书编写组', '高等教育出版社', '9787040396666', 25.00, 180, '马克思主义理论教材'),
('大学语文', '徐中玉', '华东师范大学出版社', '9787561789456', 35.00, 120, '大学语文经典教材'),
('中国文学史', '袁行霈', '高等教育出版社', '9787040183191', 68.00, 60, '中国文学发展史教材'),
('人工智能导论', '李德毅', '中国科学技术大学出版社', '9787312045678', 58.00, 50, '人工智能基础理论'),
('机器学习', '周志华', '清华大学出版社', '9787302423287', 88.00, 45, '机器学习经典教材'),
('软件工程导论', '张海藩', '清华大学出版社', '9787302234567', 65.00, 55, '软件工程基础教材'),
('计算机图形学', '孙家广', '清华大学出版社', '9787302345678', 72.00, 40, '计算机图形学理论与实践'),
('编译原理', '陈火旺', '国防工业出版社', '9787118456789', 69.00, 35, '编译器设计原理'),
('数字信号处理', '奥本海姆', '电子工业出版社', '9787121567890', 95.00, 30, '数字信号处理经典教材');

-- 数据库初始化完成
-- 注意：当前系统主要使用 t_user、t_book、t_order、t_order_item、t_cart 这5个核心表
-- 其他表（t_course、t_course_book）为扩展功能预留，当前未使用