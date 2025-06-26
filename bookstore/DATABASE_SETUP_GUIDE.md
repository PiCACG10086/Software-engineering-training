# 数据库设置指南

## 问题解决

### 已修复的问题
1. **数据库连接错误**：修正了数据库连接URL，从 `university_bookstore` 改为 `bookstore`
2. **表结构不匹配**：确保应用程序连接到正确的数据库

### 数据库配置

**当前配置**（位于 `src/main/java/com/university/bookstore/util/DBUtil.java`）：
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/bookstore?useUnicode=true&characterEncoding=utf8&useSSL=false";
private static final String DB_USERNAME = "root";
private static final String DB_PASSWORD = "123456";
```

### 数据库初始化步骤

1. **确保MySQL服务运行**
   - 启动MySQL服务
   - 确保端口3306可用

2. **创建数据库**
   ```sql
   -- 在MySQL中执行
   CREATE DATABASE IF NOT EXISTS bookstore DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
   ```

3. **执行初始化脚本**
   ```bash
   # 在命令行中执行
   mysql -u root -p123456 bookstore < database/init.sql
   ```

4. **验证表结构**
   ```sql
   USE bookstore;
   SHOW TABLES;
   DESCRIBE t_order;
   DESCRIBE t_order_item;
   ```

### 预期的表结构

**t_order表**：
- id (INT, PRIMARY KEY)
- user_id (INT, NOT NULL)
- total_amount (DECIMAL(10,2))
- status (ENUM)
- create_time (TIMESTAMP)
- update_time (DATETIME)

**t_order_item表**：
- id (INT, PRIMARY KEY)
- order_id (INT, NOT NULL)
- book_id (INT, NOT NULL)
- quantity (INT, NOT NULL)
- price (DECIMAL(10,2), NOT NULL)

### 故障排除

如果仍然遇到问题：

1. **检查数据库连接**
   - 确认MySQL用户名和密码正确
   - 确认数据库名称为 `bookstore`

2. **检查表是否存在**
   ```sql
   USE bookstore;
   SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'bookstore';
   ```

3. **重新初始化数据库**
   ```sql
   DROP DATABASE IF EXISTS bookstore;
   CREATE DATABASE bookstore DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
   ```
   然后重新执行 `database/init.sql`

### 测试数据

初始化脚本包含以下测试数据：

**用户账户：**
- 管理员账户：admin/admin123 (系统管理员)
- 教师账户：teacher001~teacher006/teacher123 (张教授、李老师、王副教授等)
- 学生账户：student001~student006/student123 (张小明、李小红、王小强等)

**院系信息：**
- 计算机科学与技术学院
- 数学与统计学院  
- 物理与电子工程学院
- 经济管理学院
- 外国语学院
- 文学与新闻传播学院

**课程信息：**
- 数据结构与算法 (CS101)
- 高等数学(一) (MATH101)
- 大学物理(一) (PHYS101)
- 程序设计基础 (CS102)
- 线性代数 (MATH102)
- 大学英语(一) (ENG101)
- 经济学原理 (ECON101)
- 中国近现代史纲要 (HIST101)

**教材数据：**
- 计算机类：数据结构、算法导论、Java核心技术、计算机网络等
- 数学类：高等数学、线性代数、概率论与数理统计等
- 物理类：大学物理、普通物理学等
- 英语类：新视野大学英语、大学英语综合教程等
- 经济管理类：西方经济学、管理学原理、会计学原理等
- 人文社科类：中国近现代史纲要、马克思主义基本原理等
- 专业选修：人工智能导论、机器学习、软件工程导论等

共计29本教材，涵盖各个学科领域，价格从25元到128元不等。

现在应用程序应该能够正常处理购物车结算功能。