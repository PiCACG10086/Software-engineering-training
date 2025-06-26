package com.university.bookstore.ui;

import com.university.bookstore.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.util.Optional;

/**
 * 基础控制器类，提供公共功能
 */
public abstract class BaseController {
    
    protected User currentUser;
    
    /**
     * 设置当前登录用户
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        onUserSet();
    }
    
    /**
     * 获取当前登录用户
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * 用户设置后的回调方法，子类可以重写
     */
    protected void onUserSet() {
        // 默认实现为空，子类可以重写
    }
    
    /**
     * 显示信息提示框
     */
    protected void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * 显示警告提示框
     */
    protected void showWarningAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * 显示错误提示框
     */
    protected void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * 显示确认对话框
     */
    protected boolean showConfirmDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * 检查用户权限
     */
    protected boolean checkPermission(User.UserRole requiredRole) {
        if (currentUser == null) {
            showErrorAlert("权限错误", "用户未登录");
            return false;
        }
        
        if (currentUser.getRole() != requiredRole && currentUser.getRole() != User.UserRole.ADMIN) {
            showErrorAlert("权限不足", "您没有执行此操作的权限");
            return false;
        }
        
        return true;
    }
    
    /**
     * 检查是否为管理员
     */
    protected boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.UserRole.ADMIN;
    }
    
    /**
     * 检查是否为教师
     */
    protected boolean isTeacher() {
        return currentUser != null && currentUser.getRole() == User.UserRole.TEACHER;
    }
    
    /**
     * 检查是否为学生
     */
    protected boolean isStudent() {
        return currentUser != null && currentUser.getRole() == User.UserRole.STUDENT;
    }
    
    /**
     * 验证输入字段是否为空
     */
    protected boolean validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            showWarningAlert("输入验证", fieldName + "不能为空");
            return false;
        }
        return true;
    }
    
    /**
     * 验证数字输入
     */
    protected boolean validateNumber(String value, String fieldName) {
        if (!validateNotEmpty(value, fieldName)) {
            return false;
        }
        
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            showWarningAlert("输入验证", fieldName + "必须是有效的数字");
            return false;
        }
    }
    
    /**
     * 验证整数输入
     */
    protected boolean validateInteger(String value, String fieldName) {
        if (!validateNotEmpty(value, fieldName)) {
            return false;
        }
        
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            showWarningAlert("输入验证", fieldName + "必须是有效的整数");
            return false;
        }
    }
    
    /**
     * 验证正整数输入
     */
    protected boolean validatePositiveInteger(String value, String fieldName) {
        if (!validateInteger(value, fieldName)) {
            return false;
        }
        
        int intValue = Integer.parseInt(value);
        if (intValue <= 0) {
            showWarningAlert("输入验证", fieldName + "必须是正整数");
            return false;
        }
        
        return true;
    }
    
    /**
     * 格式化用户角色显示
     */
    protected String formatUserRole(User.UserRole role) {
        switch (role) {
            case STUDENT:
                return "学生";
            case TEACHER:
                return "教师";
            case ADMIN:
                return "管理员";
            default:
                return "未知";
        }
    }
    
    /**
     * 格式化订单状态显示
     */
    protected String formatOrderStatus(com.university.bookstore.model.Order.OrderStatus status) {
        switch (status) {
            case PENDING:
                return "待支付";
            case PAID:
                return "已支付";
            case CONFIRMED:
                return "已确认";
            case SHIPPED:
                return "已发货";
            case COMPLETED:
                return "已完成";
            case CANCELLED:
                return "已取消";
            default:
                return "未知";
        }
    }
    
    /**
     * 退出登录功能
     * @param event 触发事件的控件
     */
    protected void handleLogout(Node event) {
        // 显示确认对话框
        boolean confirmed = showConfirmDialog("退出登录", "确定要退出登录吗？");
        
        if (confirmed) {
            try {
                // 清除当前用户信息
                currentUser = null;
                
                // 加载登录界面
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                Scene loginScene = new Scene(loader.load(), 400, 300);
                
                // 获取当前窗口并切换到登录界面
                Stage currentStage = (Stage) event.getScene().getWindow();
                currentStage.setScene(loginScene);
                currentStage.setTitle("用户登录");
                currentStage.centerOnScreen();
                
                showInfoAlert("退出成功", "已成功退出登录");
                
            } catch (IOException e) {
                showErrorAlert("退出失败", "退出登录时发生错误：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}