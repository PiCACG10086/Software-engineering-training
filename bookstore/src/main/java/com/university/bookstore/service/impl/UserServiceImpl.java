package com.university.bookstore.service.impl;

import com.university.bookstore.dao.UserDAO;
import com.university.bookstore.dao.impl.UserDAOImpl;
import com.university.bookstore.model.User;
import com.university.bookstore.service.UserService;


import java.util.List;

/**
 * 用户业务逻辑服务实现类
 */
public class UserServiceImpl implements UserService {
    
    private final UserDAO userDAO;
    
    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }
    
    @Override
    public User login(String username, String password, User.UserRole role) {
        if (username == null || password == null || role == null) {
            return null;
        }
        
        // 直接使用明文密码验证用户
        User user = userDAO.findByUsernameAndPassword(username, password);
        
        // 检查角色是否匹配
        if (user != null && user.getRole() == role) {
            return user;
        }
        
        return null;
    }
    
    @Override
    public boolean register(User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return false;
        }
        
        // 检查用户名是否已存在
        if (userDAO.existsByUsername(user.getUsername())) {
            return false;
        }
        
        // 直接使用明文密码
        
        // 设置创建时间
        user.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));
        
        return userDAO.insert(user);
    }
    
    @Override
    public User getUserById(Integer id) {
        if (id == null) {
            return null;
        }
        return userDAO.findById(id);
    }
    
    @Override
    public User getUserByUsername(String username) {
        if (username == null) {
            return null;
        }
        return userDAO.findByUsername(username);
    }
    
    @Override
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }
    
    @Override
    public List<User> getUsersByRole(User.UserRole role) {
        if (role == null) {
            return null;
        }
        return userDAO.findByRole(role);
    }
    
    @Override
    public boolean updateUser(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }
        
        // 如果密码被修改，直接使用明文密码
        // 密码已经是明文，无需加密
        
        return userDAO.update(user);
    }
    
    @Override
    public boolean deleteUser(Integer id) {
        if (id == null) {
            return false;
        }
        return userDAO.delete(id);
    }
    
    @Override
    public boolean changePassword(Integer userId, String oldPassword, String newPassword) {
        if (userId == null || oldPassword == null || newPassword == null) {
            return false;
        }
        
        // 验证旧密码
        if (!validatePassword(userId, oldPassword)) {
            return false;
        }
        
        // 更新密码
        return userDAO.updatePassword(userId, newPassword);
    }
    
    @Override
    public boolean resetPassword(Integer userId, String newPassword) {
        if (userId == null || newPassword == null) {
            return false;
        }
        
        return userDAO.updatePassword(userId, newPassword);
    }
    
    @Override
    public boolean isUsernameAvailable(String username) {
        if (username == null) {
            return false;
        }
        return !userDAO.existsByUsername(username);
    }
    
    @Override
    public boolean validatePassword(Integer userId, String password) {
        if (userId == null || password == null) {
            return false;
        }
        
        User user = userDAO.findById(userId);
        if (user == null) {
            return false;
        }
        
        return password.equals(user.getPassword());
    }
    
    @Override
    public List<User> getAllTeachers() {
        return userDAO.findByRole(User.UserRole.TEACHER);
    }
    
    @Override
    public List<User> getAllStudents() {
        return userDAO.findByRole(User.UserRole.STUDENT);
    }
    

}