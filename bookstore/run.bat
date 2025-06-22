@echo off
chcp 65001 >nul
echo 正在启动高校教材购销系统...
echo.

REM 切换到脚本所在目录
cd /d "%~dp0"

REM 检查是否已编译
if not exist "target\classes" (
    echo 正在编译项目...
    mvn clean compile
    if errorlevel 1 (
        echo 编译失败，请检查错误信息
        pause
        exit /b 1
    )
)

REM 使用Maven JavaFX插件运行应用程序
echo 启动应用程序...
mvn javafx:run

if errorlevel 1 (
    echo.
    echo 应用程序启动失败，尝试使用exec插件...
    mvn exec:java
)

echo.
echo 应用程序已退出
pause