package com.university.bookstore.ui;

import com.university.bookstore.model.User;
import com.university.bookstore.service.UserService;
import com.university.bookstore.service.impl.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 注册界面控制器
 */
public class RegisterController extends BaseController implements Initializable {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField studentIdField;
    
    @FXML
    private ComboBox<String> roleComboBox;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Label messageLabel;
    
    private UserService userService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userService = new UserServiceImpl();
        
        // 初始化角色下拉框
        roleComboBox.getItems().addAll("学生", "教师");
        roleComboBox.setValue("学生");
        
        // 设置按钮事件
        registerButton.setOnAction(event -> handleRegister());
        cancelButton.setOnAction(event -> handleCancel());
        
        // 角色选择事件
        roleComboBox.setOnAction(event -> handleRoleChange());
        
        // 初始显示学号字段
        handleRoleChange();
    }
    
    /**
     * 处理角色变化事件
     */
    @FXML
    private void handleRoleChange() {
        String selectedRole = roleComboBox.getValue();
        if ("学生".equals(selectedRole)) {
            studentIdField.setVisible(true);
            studentIdField.setManaged(true);
        } else {
            studentIdField.setVisible(false);
            studentIdField.setManaged(false);
            studentIdField.clear();
        }
    }
    
    /**
     * 处理注册事件
     */
    @FXML
    private void handleRegister() {
        // 获取输入值
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String name = nameField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String roleStr = roleComboBox.getValue();
        
        // 验证输入
        if (!validateInput(username, password, confirmPassword, name, studentId, roleStr)) {
            return;
        }
        
        // 检查用户名是否可用
        if (!userService.isUsernameAvailable(username)) {
            showMessage("用户名已存在，请选择其他用户名", false);
            return;
        }
        
        // 创建用户对象
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setRole(convertRole(roleStr));
        
        // 如果是学生，设置学号
        if ("学生".equals(roleStr) && !studentId.isEmpty()) {
            user.setStudentId(studentId);
        }
        
        // 执行注册
        if (userService.register(user)) {
            showInfoAlert("注册成功", "用户注册成功！请使用新账号登录。");
            handleCancel(); // 关闭注册窗口
        } else {
            showMessage("注册失败，请重试", false);
        }
    }
    
    /**
     * 处理取消事件
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    /**
     * 验证输入
     */
    private boolean validateInput(String username, String password, String confirmPassword, 
                                 String name, String studentId, String roleStr) {
        
        // 验证用户名
        if (!validateNotEmpty(username, "用户名")) {
            return false;
        }
        
        if (username.length() < 3 || username.length() > 20) {
            showMessage("用户名长度必须在3-20个字符之间", false);
            return false;
        }
        
        // 验证密码
        if (!validateNotEmpty(password, "密码")) {
            return false;
        }
        
        if (password.length() < 6) {
            showMessage("密码长度不能少于6个字符", false);
            return false;
        }
        
        // 验证确认密码
        if (!password.equals(confirmPassword)) {
            showMessage("两次输入的密码不一致", false);
            return false;
        }
        
        // 验证姓名
        if (!validateNotEmpty(name, "姓名")) {
            return false;
        }
        
        // 验证学号（仅学生需要）
        if ("学生".equals(roleStr)) {
            if (!validateNotEmpty(studentId, "学号")) {
                return false;
            }
            
            if (studentId.length() < 6 || studentId.length() > 20) {
                showMessage("学号长度必须在6-20个字符之间", false);
                return false;
            }
        }
        
        return true;
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
            default:
                return User.UserRole.STUDENT;
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