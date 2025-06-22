# 数据库配置说明

## 数据库要求

应用程序需要连接到MySQL数据库，配置如下：

- **数据库服务器**: localhost:3306
- **数据库名称**: university_bookstore
- **用户名**: root
- **密码**: 123456

## 设置步骤

1. **安装MySQL服务器**
   - 确保MySQL服务器已安装并运行在端口3306
   - 可以从 https://dev.mysql.com/downloads/mysql/ 下载

2. **创建数据库**
   ```sql
   CREATE DATABASE university_bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
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
❌ **数据库连接失败** - 需要设置MySQL数据库

## 解决方案

如果不想设置MySQL数据库，可以考虑：
1. 修改代码使用H2内存数据库进行测试
2. 使用Docker快速启动MySQL容器
3. 修改数据库连接配置指向现有的MySQL实例

## 注意事项

- 确保MySQL服务正在运行
- 检查防火墙设置是否阻止了3306端口
- 验证用户名和密码是否正确