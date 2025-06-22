-- 高校教材购销系统数据库初始化脚本
-- MySQL 5.5.40+

-- 创建数据库
CREATE DATABASE IF NOT EXISTS university_bookstore DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE university_bookstore;

-- 1. 用户表 (t_user)
CREATE TABLE t_user (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(加密后)',
    role ENUM('STUDENT', 'TEACHER', 'ADMIN') NOT NULL COMMENT '用户角色',
    name VARCHAR(100) NOT NULL COMMENT '真实姓名',
    student_id VARCHAR(20) COMMENT '学号(学生专用)',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

-- 2. 院系表 (t_department)
CREATE TABLE t_department (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '院系ID',
    name VARCHAR(100) NOT NULL COMMENT '院系名称'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='院系表';

-- 3. 课程表 (t_course)
CREATE TABLE t_course (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '课程ID',
    course_code VARCHAR(20) NOT NULL COMMENT '课程代码',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    department_id INT NOT NULL COMMENT '所属院系ID',
    teacher_id INT NOT NULL COMMENT '授课教师ID',
    FOREIGN KEY (department_id) REFERENCES t_department(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES t_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='课程表';

-- 4. 教材表 (t_book)
CREATE TABLE t_book (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '教材ID',
    isbn VARCHAR(20) NOT NULL UNIQUE COMMENT 'ISBN号',
    title VARCHAR(200) NOT NULL COMMENT '书名',
    author VARCHAR(100) NOT NULL COMMENT '作者',
    publisher VARCHAR(100) NOT NULL COMMENT '出版社',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    description TEXT COMMENT '教材描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='教材表';

-- 5. 课程-教材关联表 (t_course_book)
CREATE TABLE t_course_book (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    course_id INT NOT NULL COMMENT '课程ID',
    book_id INT NOT NULL COMMENT '教材ID',
    FOREIGN KEY (course_id) REFERENCES t_course(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES t_book(id) ON DELETE CASCADE,
    UNIQUE KEY uk_course_book (course_id, book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='课程教材关联表';

-- 6. 订单表 (t_order)
CREATE TABLE t_order (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_number VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    student_id INT NOT NULL COMMENT '学生ID',
    total_price DECIMAL(10,2) NOT NULL COMMENT '订单总价',
    status ENUM('PENDING', 'PAID', 'SHIPPED', 'CANCELLED') NOT NULL DEFAULT 'PENDING' COMMENT '订单状态',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (student_id) REFERENCES t_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单表';

-- 7. 订单详情表 (t_order_detail)
CREATE TABLE t_order_detail (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单详情ID',
    order_id INT NOT NULL COMMENT '订单ID',
    book_id INT NOT NULL COMMENT '教材ID',
    quantity INT NOT NULL COMMENT '购买数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '购买时单价',
    FOREIGN KEY (order_id) REFERENCES t_order(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES t_book(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单详情表';

-- 插入初始数据
-- 插入管理员用户
INSERT INTO t_user (username, password, role, name) VALUES 
('admin', 'admin123', 'ADMIN', '系统管理员'); -- 密码: admin123 (明文)

-- 插入院系数据
INSERT INTO t_department (name) VALUES 
('计算机科学与技术学院'),
('电子信息工程学院'),
('机械工程学院'),
('经济管理学院');

-- 插入教师用户
INSERT INTO t_user (username, password, role, name) VALUES 
('teacher001', 'teacher123', 'TEACHER', '张教授'), -- 密码: teacher123 (明文)
('teacher002', 'teacher123', 'TEACHER', '李教授'), -- 密码: teacher123 (明文)
('teacher003', 'teacher123', 'TEACHER', '王教授'); -- 密码: teacher123 (明文)

-- 插入学生用户
INSERT INTO t_user (username, password, role, name, student_id) VALUES 
('student001', 'student123', 'STUDENT', '张三', '2021001'), -- 密码: student123 (明文)
('student002', 'student123', 'STUDENT', '李四', '2021002'), -- 密码: student123 (明文)
('student003', 'student123', 'STUDENT', '王五', '2021003'); -- 密码: student123 (明文)

-- 插入课程数据
INSERT INTO t_course (course_code, course_name, department_id, teacher_id) VALUES 
('CS101', 'Java程序设计', 1, 2),
('CS102', '数据结构与算法', 1, 2),
('EE101', '电路分析', 2, 3),
('ME101', '机械制图', 3, 4);

-- 插入教材数据
INSERT INTO t_book (isbn, title, author, publisher, price, stock, description) VALUES 
('978-7-111-54742-6', 'Java核心技术 卷I', 'Cay S. Horstmann', '机械工业出版社', 89.00, 50, 'Java编程经典教材'),
('978-7-115-48936-2', '数据结构与算法分析', 'Mark Allen Weiss', '人民邮电出版社', 79.00, 30, '数据结构经典教材'),
('978-7-04-049876-3', '电路分析基础', '李瀚荪', '高等教育出版社', 65.00, 40, '电路分析基础教材'),
('978-7-111-56789-1', '机械制图', '何铭新', '机械工业出版社', 55.00, 25, '机械制图标准教材'),
('978-7-302-51234-5', 'MySQL数据库应用', '王珊', '清华大学出版社', 68.00, 35, '数据库应用教材');

-- 插入课程教材关联数据
INSERT INTO t_course_book (course_id, book_id) VALUES 
(1, 1), -- Java程序设计 -> Java核心技术
(2, 2), -- 数据结构与算法 -> 数据结构与算法分析
(3, 3), -- 电路分析 -> 电路分析基础
(4, 4), -- 机械制图 -> 机械制图
(2, 5); -- 数据结构与算法 -> MySQL数据库应用

COMMIT;