package com.university.bookstore.ui;

import com.university.bookstore.model.User;
import com.university.bookstore.service.UserService;
import com.university.bookstore.service.impl.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 登录界面控制器
 */
public class LoginController implements Initializable {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    // 移除角色选择框，系统将自动识别用户角色
    // @FXML
    // private ComboBox<String> roleComboBox;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Label messageLabel;
    
    private UserService userService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = new UserServiceImpl();
        
        // 移除角色下拉框初始化，系统将自动识别用户角色
        
        // 设置按钮事件
        loginButton.setOnAction(event -> handleLogin());
        registerButton.setOnAction(event -> handleRegister());
        
        // 设置回车键登录
        passwordField.setOnAction(event -> handleLogin());
    }
    
    /**
     * 处理登录事件
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // 验证输入
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("请填写用户名和密码", false);
            return;
        }
        
        // 执行登录（自动识别角色）
        User user = userService.login(username, password);
        if (user != null) {
            showMessage("登录成功", true);
            openMainWindow(user);
        } else {
            showMessage("用户名或密码不正确", false);
        }
    }
    
    /**
     * 处理注册事件
     */
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Scene scene = new Scene(loader.load(), 450, 400);
            
            Stage registerStage = new Stage();
            registerStage.setTitle("用户注册");
            registerStage.setScene(scene);
            registerStage.setResizable(false);
            registerStage.centerOnScreen();
            registerStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("无法打开注册窗口", false);
        }
    }
    
    /**
     * 打开主窗口
     */
    private void openMainWindow(User user) {
        try {
            String fxmlFile;
            String title;
            
            switch (user.getRole()) {
                case STUDENT:
                    fxmlFile = "/fxml/student_main.fxml";
                    title = "学生系统";
                    break;
                case TEACHER:
                    fxmlFile = "/fxml/teacher_main.fxml";
                    title = "教师系统";
                    break;
                case ADMIN:
                    fxmlFile = "/fxml/admin_main.fxml";
                    title = "管理员系统";
                    break;
                default:
                    showMessage("未知的用户角色", false);
                    return;
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 1000, 700);
            
            // 获取控制器并设置当前用户
            Object controller = loader.getController();
            if (controller instanceof BaseController) {
                ((BaseController) controller).setCurrentUser(user);
            }
            
            Stage mainStage = new Stage();
            mainStage.setTitle("高校教材购销系统 - " + title + " - 欢迎 " + user.getName());
            mainStage.setScene(scene);
            mainStage.centerOnScreen();
            mainStage.show();
            
            // 关闭登录窗口
            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();
            
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("无法打开主窗口", false);
        }
    }
    
    /**
     * 转换角色字符串为枚举
     */
    private User.UserRole convertRole(String roleStr) {
        switch (roleStr) {
            case "学生":
                return User.UserRole.STUDENT;
            case "教师":
                return User.UserRole.TEACHER;
            case "管理员":
                return User.UserRole.ADMIN;
            default:
                return null;
        }
    }
    
    /**
     * 显示消息
     */
    private void showMessage(String message, boolean isSuccess) {
        messageLabel.setText(message);
        messageLabel.setStyle(isSuccess ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        
        // 3秒后清除消息
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> messageLabel.setText(""));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}