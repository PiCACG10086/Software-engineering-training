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

## IDE部署指南

### IntelliJ IDEA 部署指南

#### 1. 环境准备
- 安装 IntelliJ IDEA 2020.3 或更高版本
- 确保已安装 JDK 1.8（项目使用JDK 1.8）
- 安装 Maven 3.6+ 或使用IDEA内置Maven
- 安装 MySQL 5.5.40+

#### 2. 导入项目
1. 打开 IntelliJ IDEA
2. 选择 `File` → `Open` 或 `Import Project`
3. 选择项目根目录 `bookstore` 文件夹
4. 选择 `Import project from external model` → `Maven`
5. 点击 `Next`，保持默认设置
6. 等待Maven依赖下载完成

#### 3. 配置项目SDK
1. 打开 `File` → `Project Structure` (Ctrl+Alt+Shift+S)
2. 在 `Project` 选项卡中：
   - `Project SDK`: 选择 JDK 1.8
   - `Project language level`: 选择 `8 - Lambdas, type annotations etc.`
3. 在 `Modules` 选项卡中确认模块SDK为JDK 1.8
4. 点击 `Apply` 和 `OK`

#### 4. 配置Maven
1. 打开 `File` → `Settings` (Ctrl+Alt+S)
2. 导航到 `Build, Execution, Deployment` → `Build Tools` → `Maven`
3. 确认Maven配置：
   - `Maven home directory`: 使用IDEA内置Maven或指定本地Maven路径
   - `User settings file`: 可选择自定义settings.xml
   - `Local repository`: 确认本地仓库路径
4. 在 `Maven` → `Importing` 中：
   - 勾选 `Import Maven projects automatically`
   - 勾选 `Automatically download sources and documentation`

#### 5. 配置数据库连接
1. 在IDEA中打开 `src/main/java/com/university/bookstore/util/DBUtil.java`
2. 修改数据库连接参数：
   ```java
   private static final String DB_URL = "jdbc:mysql://localhost:3306/university_bookstore";
   private static final String DB_USERNAME = "root";  // 你的数据库用户名
   private static final String DB_PASSWORD = "123456";  // 你的数据库密码
   ```

#### 6. 运行配置

**方法一：使用Maven运行配置（推荐）**
1. 打开 `Run` → `Edit Configurations`
2. 点击 `+` → `Maven`
3. 配置如下：
   - `Name`: JavaFX Run
   - `Working directory`: 项目根目录
   - `Command line`: `javafx:run`
4. 点击 `Apply` 和 `OK`
5. 点击运行按钮或按 `Shift+F10`

**方法二：使用Application运行配置**
1. 打开 `Run` → `Edit Configurations`
2. 点击 `+` → `Application`
3. 配置如下：
   - `Name`: Bookstore App
   - `Main class`: `com.university.bookstore.Launcher`
   - `VM options`: `--module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml`
   - `Working directory`: 项目根目录
4. 点击 `Apply` 和 `OK`

#### 7. 调试配置
1. 在代码中设置断点
2. 使用 `Debug` 模式运行配置
3. 使用IDEA的调试工具进行代码调试

#### 8. 常见问题解决

**问题1：JavaFX运行时组件缺失**
- 解决方案：使用Maven运行 `mvn javafx:run`
- 或者在VM options中添加JavaFX模块路径

**问题2：编码问题**
1. 打开 `File` → `Settings` → `Editor` → `File Encodings`
2. 设置所有编码为 `UTF-8`

**问题3：Maven依赖下载失败**
1. 检查网络连接
2. 尝试使用国内Maven镜像
3. 在 `pom.xml` 中添加阿里云镜像配置

### Visual Studio Code 部署指南

#### 1. 环境准备
- 安装 Visual Studio Code 最新版本
- 安装 JDK 1.8
- 安装 Maven 3.6+
- 安装 MySQL 5.5.40+

#### 2. 安装必要扩展
在VSCode扩展市场中安装以下扩展：
1. **Extension Pack for Java** (Microsoft) - Java开发必备扩展包
   - Language Support for Java(TM) by Red Hat
   - Debugger for Java
   - Test Runner for Java
   - Maven for Java
   - Project Manager for Java
   - Visual Studio IntelliCode

2. **JavaFX Support** - JavaFX开发支持
   - 搜索并安装 "JavaFX" 相关扩展

#### 3. 打开项目
1. 启动 VSCode
2. 选择 `File` → `Open Folder`
3. 选择项目根目录 `bookstore` 文件夹
4. VSCode会自动识别Maven项目并开始初始化

#### 4. 配置Java环境
1. 按 `Ctrl+Shift+P` 打开命令面板
2. 输入 `Java: Configure Java Runtime`
3. 确认JDK 1.8路径配置正确
4. 如需修改，点击 `File` → `Preferences` → `Settings`
5. 搜索 `java.home` 并设置JDK路径

#### 5. 配置Maven
1. 打开 `File` → `Preferences` → `Settings`
2. 搜索 `maven`
3. 配置以下设置：
   - `Java › Configuration › Maven › User Settings`: 指定Maven settings.xml路径（可选）
   - `Maven › Executable › Path`: 指定Maven可执行文件路径（如果不使用内置Maven）

#### 6. 项目构建
1. 按 `Ctrl+Shift+P` 打开命令面板
2. 输入 `Java: Reload Projects` 重新加载项目
3. 或者在终端中运行：
   ```bash
   mvn clean compile
   ```

#### 7. 配置数据库连接
1. 在VSCode中打开 `src/main/java/com/university/bookstore/util/DBUtil.java`
2. 修改数据库连接参数：
   ```java
   private static final String DB_URL = "jdbc:mysql://localhost:3306/university_bookstore";
   private static final String DB_USERNAME = "root";  // 你的数据库用户名
   private static final String DB_PASSWORD = "123456";  // 你的数据库密码
   ```

#### 8. 运行配置

**方法一：使用集成终端运行Maven命令（推荐）**
1. 按 `Ctrl+`` 打开集成终端
2. 运行以下命令：
   ```bash
   mvn javafx:run
   ```

**方法二：配置launch.json**
1. 在项目根目录创建 `.vscode` 文件夹（如果不存在）
2. 在 `.vscode` 文件夹中创建 `launch.json` 文件：
   ```json
   {
       "version": "0.2.0",
       "configurations": [
           {
               "type": "java",
               "name": "Launch Bookstore App",
               "request": "launch",
               "mainClass": "com.university.bookstore.Launcher",
               "projectName": "bookstore",
               "vmArgs": "--module-path \"path/to/javafx/lib\" --add-modules javafx.controls,javafx.fxml"
           }
       ]
   }
   ```
3. 按 `F5` 或点击运行按钮启动应用

**方法三：使用tasks.json配置Maven任务**
1. 在 `.vscode` 文件夹中创建 `tasks.json` 文件：
   ```json
   {
       "version": "2.0.0",
       "tasks": [
           {
               "label": "maven-javafx-run",
               "type": "shell",
               "command": "mvn",
               "args": ["javafx:run"],
               "group": {
                   "kind": "build",
                   "isDefault": true
               },
               "presentation": {
                   "echo": true,
                   "reveal": "always",
                   "focus": false,
                   "panel": "shared"
               },
               "problemMatcher": []
           }
       ]
   }
   ```
2. 按 `Ctrl+Shift+P` 输入 `Tasks: Run Task`
3. 选择 `maven-javafx-run` 任务

#### 9. 调试配置
1. 在代码中设置断点（点击行号左侧）
2. 按 `F5` 启动调试模式
3. 使用VSCode的调试面板进行代码调试

#### 10. 常见问题解决

**问题1：Java扩展无法正常工作**
- 解决方案：重新安装Java扩展包，确保JDK路径配置正确

**问题2：Maven依赖无法解析**
1. 按 `Ctrl+Shift+P` 输入 `Java: Reload Projects`
2. 或在终端运行 `mvn clean install`

**问题3：JavaFX运行时错误**
- 使用Maven命令运行：`mvn javafx:run`
- 确保JavaFX依赖正确配置

**问题4：中文显示乱码**
1. 打开 `File` → `Preferences` → `Settings`
2. 搜索 `encoding` 设置为 `UTF-8`

#### 11. 推荐的VSCode设置
在 `.vscode/settings.json` 中添加以下配置：
```json
{
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.compile.nullAnalysis.mode": "automatic",
    "java.format.settings.url": "https://raw.githubusercontent.com/google/styleguide/gh-pages/eclipse-java-google-style.xml",
    "files.encoding": "utf8",
    "java.saveActions.organizeImports": true,
    "editor.formatOnSave": true
}
```

### 1. 数据库配置

1. 安装MySQL数据库
2. 启动MySQL服务
3. 创建数据库：
   ```sql
   CREATE DATABASE bookstore;
   ```
4. 执行数据库初始化脚本：
   ```bash
   mysql -u root -p bookstore < database/init.sql
   ```
   
   或者登录MySQL后执行：
   ```sql
   USE bookstore;
   SOURCE database/init.sql;
   ```

### 2. 配置数据库连接

修改 `src/main/java/com/university/bookstore/util/DBUtil.java` 文件中的数据库连接参数：

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/bookstore";
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
- 姓名：系统管理员

### 教师账号
- 用户名：`teacher001` ~ `teacher006`
- 密码：`teacher123`
- 角色：教师
- 姓名：张教授、李老师、王副教授、刘老师、陈教授、赵老师

### 学生账号
- 用户名：`student001` ~ `student006`
- 密码：`student123`
- 角色：学生
- 姓名：张小明、李小红、王小强、刘小芳、陈小华、赵小军
- 学号：2021001 ~ 2021006

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
- 确保数据库 `bookstore` 已创建

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