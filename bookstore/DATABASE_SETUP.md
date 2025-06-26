# 数据库配置说明

## 数据库要求

应用程序需要连接到MySQL数据库，配置如下：

- **数据库服务器**: localhost:3306
- **数据库名称**: bookstore
- **用户名**: root
- **密码**: 123456

## 设置步骤

1. **安装MySQL服务器**
   - 确保MySQL服务器已安装并运行在端口3306
   - 可以从 https://dev.mysql.com/downloads/mysql/ 下载

2. **创建数据库**
   ```sql
   CREATE DATABASE bookstore CHARACTER SET utf8 COLLATE utf8_general_ci;
   ```

3. **配置用户权限**
   ```sql
   -- 如果使用root用户，确保密码设置为'123456'
   ALTER USER 'root'@'localhost' IDENTIFIED BY '123456';
   FLUSH PRIVILEGES;
   ```

4. **创建数据表**
   - 需要根据应用程序的需求创建相应的数据表
   - 包括用户表、图书表、订单表等

## 当前状态

✅ **应用程序编译成功**
✅ **JavaFX运行时配置正确**
✅ **数据库连接成功**
✅ **已移除 MD5 密码加密，现在使用明文密码**

## 密码说明

**重要更新**: 系统已移除 MD5 密码加密，现在使用明文密码存储和验证。

### 默认用户账号
- **管理员**: 用户名 `admin`, 密码 `admin123` (系统管理员)
- **教师**: 用户名 `teacher001~teacher006`, 密码 `teacher123` (张教授、李老师、王副教授、刘老师、陈教授、赵老师)
- **学生**: 用户名 `student001~student006`, 密码 `student123` (张小明、李小红、王小强、刘小芳、陈小华、赵小军)

## 解决方案

### 方案1: 配置现有 MySQL
如果你已经安装了 MySQL，只需要:
1. 确保 MySQL 服务正在运行
2. 创建数据库: `CREATE DATABASE bookstore;`
3. 运行 `database/init.sql` 脚本初始化表和数据
4. 确认用户权限正确

### 方案2: 使用 Docker (推荐)
```bash
docker run --name mysql-bookstore -e MYSQL_ROOT_PASSWORD=123456 -e MYSQL_DATABASE=bookstore -p 3306:3306 -d mysql:5.7
```

### 方案3: 切换到 H2 数据库
如果不想安装 MySQL，可以修改代码使用 H2 内存数据库进行开发测试。

## 注意事项

- 确保MySQL服务正在运行
- 检查防火墙设置是否阻止了3306端口
- 验证用户名和密码是否正确