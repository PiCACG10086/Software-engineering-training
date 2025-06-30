package com.university.bookstore;

import com.university.bookstore.ui.LoginController;
import com.university.bookstore.util.DBUtil;
import com.university.bookstore.config.PerformanceConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * 高校教材购销系统主应用程序类
 */
public class BookstoreApplication extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // 测试数据库连接
            if (!DBUtil.testConnection()) {
                showErrorAlert("数据库连接失败", "无法连接到数据库，请检查数据库配置。");
                return;
            }
            
            // 加载登录界面
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);
            
            primaryStage.setTitle("高校教材购销系统 - 登录");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
            
            // 在界面显示后初始化性能配置（异步执行）
            new Thread(() -> {
                PerformanceConfig.initialize();
            }).start();
            
            // 设置关闭事件
            primaryStage.setOnCloseRequest(event -> {
                // 打印性能建议
                PerformanceConfig.printPerformanceRecommendations();
                
                // 关闭性能配置
                PerformanceConfig.shutdown();
                
                System.exit(0);
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("应用程序启动失败", "启动应用程序时发生错误：" + e.getMessage());
        }
    }
    
    /**
     * 显示错误提示框
     */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * 应用程序入口点
     */
    public static void main(String[] args) {
        launch(args);
    }
}