# 高校教材购销系统

## 项目简介

高校教材购销系统是一个基于JavaFX和MySQL的桌面应用程序，用于管理高校教材的购买和销售。系统支持学生、教师和管理员三种角色，提供教材浏览、购买、订单管理、用户管理等功能。

## 技术栈

- **Java**: JDK 18
- **UI框架**: JavaFX 18.0.2
- **数据库**: MySQL 5.5.40+
- **连接池**: HikariCP 3.4.5
- **构建工具**: Maven 3.6+
- **数据库驱动**: MySQL Connector/J 5.1.47

## 系统功能

### 学生功能
- 浏览教材信息
- 搜索教材（按书名、作者、ISBN等）
- 添加教材到购物车
- 提交订单和结算
- 查看订单历史
- 取消未确认订单
- 修改个人密码

### 教师功能
- 浏览所有教材
- 推荐教材给学生
- 查看学生信息
- 查看学生订单
- 查看统计信息
- 修改个人密码

### 管理员功能
- 教材管理（增删改查）
- 订单管理（查看、确认、发货、取消）
- 用户管理（查看、删除、重置密码）
- 库存管理
- 统计信息查看
- 修改个人密码

## 项目结构

```
bookstore/
├── src/main/java/com/university/bookstore/
│   ├── BookstoreApplication.java          # 主应用程序入口
│   ├── dao/                               # 数据访问层
│   │   ├── BookDAO.java                   # 教材数据访问接口
│   │   ├── OrderDAO.java                  # 订单数据访问接口
│   │   ├── UserDAO.java                   # 用户数据访问接口
│   │   └── impl/                          # DAO实现类
│   │       ├── BookDAOImpl.java
│   │       ├── OrderDAOImpl.java
│   │       └── UserDAOImpl.java
│   ├── model/                             # 实体类
│   │   ├── Book.java                      # 教材实体
│   │   ├── CartItem.java                  # 购物车项实体
│   │   ├── Course.java                    # 课程实体
│   │   ├── Department.java                # 院系实体
│   │   ├── Order.java                     # 订单实体
│   │   ├── OrderDetail.java               # 订单详情实体
│   │   └── User.java                      # 用户实体
│   ├── service/                           # 业务逻辑层
│   │   ├── BookService.java               # 教材业务接口
│   │   ├── OrderService.java              # 订单业务接口
│   │   ├── UserService.java               # 用户业务接口
│   │   └── impl/                          # Service实现类
│   │       ├── BookServiceImpl.java
│   │       ├── OrderServiceImpl.java
│   │       └── UserServiceImpl.java
│   ├── ui/                                # 用户界面控制器
│   │   ├── AdminMainController.java       # 管理员主界面控制器
│   │   ├── BaseController.java            # 基础控制器
│   │   ├── LoginController.java           # 登录界面控制器
│   │   ├── RegisterController.java        # 注册界面控制器
│   │   ├── StudentMainController.java     # 学生主界面控制器
│   │   └── TeacherMainController.java     # 教师主界面控制器
│   └── util/                              # 工具类
│       └── DBUtil.java                    # 数据库连接工具
├── src/main/resources/fxml/               # FXML界面文件
│   ├── admin_main.fxml                    # 管理员主界面
│   ├── login.fxml                         # 登录界面
│   ├── register.fxml                      # 注册界面
│   ├── student_main.fxml                  # 学生主界面
│   └── teacher_main.fxml                  # 教师主界面
├── database/
│   └── init.sql                           # 数据库初始化脚本
├── pom.xml                                # Maven配置文件
└── README.md                              # 项目说明文档
```

## 环境要求

- JDK 18 或更高版本
- MySQL 5.5.40 或更高版本
- Maven 3.6 或更高版本

## 安装和运行

### 1. 数据库配置

1. 安装MySQL数据库
2. 启动MySQL服务
3. 创建数据库：
   ```sql
   CREATE DATABASE university_bookstore;
   ```
4. 执行数据库初始化脚本：
   ```bash
   mysql -u root -p university_bookstore < database/init.sql
   ```
   
   或者登录MySQL后执行：
   ```sql
   USE university_bookstore;
   SOURCE database/init.sql;
   ```

### 2. 配置数据库连接

修改 `src/main/java/com/university/bookstore/util/DBUtil.java` 文件中的数据库连接参数：

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/university_bookstore";
private static final String DB_USERNAME = "root";  // 修改为你的数据库用户名
private static final String DB_PASSWORD = "123456";  // 修改为你的数据库密码
```

### 3. 编译和运行

#### 方法一：使用Maven插件（推荐）

1. 使用Maven编译项目：
   ```bash
   mvn clean compile
   ```

2. 运行应用程序：
   ```bash
   mvn javafx:run
   ```

#### 方法二：使用启动脚本（Windows）

直接双击运行 `run.bat` 脚本，或在命令行中执行：
```bash
run.bat
```

#### 方法三：使用Launcher类

如果直接运行BookstoreApplication遇到JavaFX运行时组件问题，可以运行Launcher类：
```bash
java -cp target/classes com.university.bookstore.Launcher
```

#### 方法四：使用exec插件

```bash
mvn exec:java
```

## 默认用户账号

**重要更新**: 系统已移除MD5密码加密，现在使用明文密码存储和验证。

系统初始化后会创建以下默认用户：

### 管理员账号
- 用户名：`admin`
- 密码：`admin123`
- 角色：管理员

### 教师账号
- 用户名：`teacher001` / `teacher002` / `teacher003`
- 密码：`teacher123`
- 角色：教师

### 学生账号
- 用户名：`student001` / `student002` / `student003`
- 密码：`student123`
- 角色：学生

## 数据库表结构

### 主要数据表

1. **t_user** - 用户表
   - 存储用户基本信息、角色、登录凭证

2. **t_department** - 院系表
   - 存储院系信息

3. **t_course** - 课程表
   - 存储课程信息及其与院系、教师的关联

4. **t_book** - 教材表
   - 存储教材详细信息、价格、库存

5. **t_course_book** - 课程教材关联表
   - 存储课程与教材的多对多关系

6. **t_order** - 订单表
   - 存储订单基本信息、状态

7. **t_order_detail** - 订单详情表
   - 存储订单中的具体教材信息

## 开发说明

### 架构设计

系统采用经典的三层架构：
- **表示层（UI Layer）**: JavaFX控制器和FXML文件
- **业务逻辑层（Service Layer）**: 处理业务规则和逻辑
- **数据访问层（DAO Layer）**: 负责数据库操作

### 设计模式

- **DAO模式**: 数据访问对象模式，封装数据库操作
- **Service模式**: 业务服务模式，封装业务逻辑
- **MVC模式**: 模型-视图-控制器模式，分离界面和业务逻辑

### 安全特性

- **密码存储**: 明文密码存储（已移除MD5加密）
- **角色权限控制**: 支持管理员、教师、学生三种角色
- **SQL注入防护**: 使用PreparedStatement防止SQL注入
- **输入验证**: 对用户输入进行数据校验

## 重要更新说明

### 密码加密移除

**版本更新**: 系统已移除MD5密码加密功能，现在使用明文密码存储。

**影响范围**:
- 用户登录验证改为明文密码比较
- 数据库中的密码字段存储明文
- 密码修改、重置功能使用明文处理

**安全提醒**: 此更改仅适用于开发和学习环境，生产环境建议重新启用密码加密。

## 常见问题

### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 确认数据库连接参数是否正确（用户名: root, 密码: 123456）
- 检查防火墙设置
- 确保数据库 `university_bookstore` 已创建

### 2. JavaFX运行时错误

**错误信息**: `错误: 缺少 JavaFX 运行时组件, 需要使用该组件来运行此应用程序`

**解决方案**:
- **推荐方式**: 使用 `mvn javafx:run` 命令运行应用程序
- **备选方式1**: 运行 `run.bat` 启动脚本
- **备选方式2**: 使用 `mvn exec:java` 命令
- **备选方式3**: 运行Launcher类 `java -cp target/classes com.university.bookstore.Launcher`
- 项目已配置JavaFX 18.0.2，使用JDK 18
- 确保Maven配置正确

### 3. 中文乱码问题
- 确保数据库字符集为UTF-8
- 检查IDE和系统编码设置

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目仅用于教学和学习目的。

## 联系方式

如有问题或建议，请联系项目维护者。