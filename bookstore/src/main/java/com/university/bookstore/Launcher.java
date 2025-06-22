package com.university.bookstore;

/**
 * 应用程序启动器类
 * 用于解决直接运行JavaFX应用时的模块路径问题
 * 这个类不继承Application，可以直接运行
 */
public class Launcher {
    
    /**
     * 主入口点
     * 通过反射启动JavaFX应用程序，避免模块路径问题
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        try {
            // 检查JavaFX是否可用
            Class.forName("javafx.application.Application");
            
            // 启动BookstoreApplication
            BookstoreApplication.main(args);
            
        } catch (ClassNotFoundException e) {
            System.err.println("错误: 缺少JavaFX运行时组件");
            System.err.println("请使用以下命令之一运行应用程序:");
            System.err.println("1. mvn javafx:run");
            System.err.println("2. mvn exec:java");
            System.err.println("3. 运行 run.bat 脚本");
            System.err.println();
            System.err.println("或者确保JavaFX模块在类路径中:");
            System.err.println("java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp target/classes com.university.bookstore.BookstoreApplication");
            
            System.exit(1);
        } catch (Exception e) {
            System.err.println("启动应用程序时发生错误: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}