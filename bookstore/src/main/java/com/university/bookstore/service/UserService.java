package com.university.bookstore.service;

import com.university.bookstore.model.User;

import java.util.List;

/**
 * 用户业务逻辑服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @param role 角色
     * @return 登录成功返回用户对象，否则返回null
     */
    User login(String username, String password, User.UserRole role);
    
    /**
     * 用户登录（自动识别角色）
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回用户对象，否则返回null
     */
    User login(String username, String password);
    
    /**
     * 用户注册
     * @param user 用户对象
     * @return 注册成功返回true，否则返回false
     */
    boolean register(User user);
    
    /**
     * 添加用户（管理员功能）
     * @param user 用户对象
     * @return 添加成功返回true，否则返回false
     */
    boolean addUser(User user);
    
    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户对象
     */
    User getUserById(Integer id);
    
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户对象
     */
    User getUserByUsername(String username);
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<User> getAllUsers();
    
    /**
     * 根据角色获取用户列表
     * @param role 用户角色
     * @return 用户列表
     */
    List<User> getUsersByRole(User.UserRole role);
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 更新成功返回true，否则返回false
     */
    boolean updateUser(User user);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除成功返回true，否则返回false
     */
    boolean deleteUser(Integer id);
    
    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改成功返回true，否则返回false
     */
    boolean changePassword(Integer userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码（管理员功能）
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 重置成功返回true，否则返回false
     */
    boolean resetPassword(Integer userId, String newPassword);
    
    /**
     * 检查用户名是否可用
     * @param username 用户名
     * @return 可用返回true，否则返回false
     */
    boolean isUsernameAvailable(String username);
    
    /**
     * 验证用户密码
     * @param userId 用户ID
     * @param password 密码
     * @return 验证成功返回true，否则返回false
     */
    boolean validatePassword(Integer userId, String password);
    
    /**
     * 获取所有教师用户
     * @return 教师用户列表
     */
    List<User> getAllTeachers();
    
    /**
     * 获取所有学生用户
     * @return 学生用户列表
     */
    List<User> getAllStudents();
}