package com.university.bookstore.dao;

import com.university.bookstore.model.User;

import java.util.List;

/**
 * 用户数据访问对象接口
 */
public interface UserDAO {
    
    /**
     * 根据用户名和密码查找用户（用于登录验证）
     * @param username 用户名
     * @param password 密码
     * @return 用户对象，如果不存在返回null
     */
    User findByUsernameAndPassword(String username, String password);
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象，如果不存在返回null
     */
    User findByUsername(String username);
    
    /**
     * 根据ID查找用户
     * @param id 用户ID
     * @return 用户对象，如果不存在返回null
     */
    User findById(Integer id);
    
    /**
     * 查找所有用户
     * @return 用户列表
     */
    List<User> findAll();
    
    /**
     * 根据角色查找用户
     * @param role 用户角色
     * @return 用户列表
     */
    List<User> findByRole(User.UserRole role);
    
    /**
     * 插入新用户
     * @param user 用户对象
     * @return 插入成功返回true，否则返回false
     */
    boolean insert(User user);
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 更新成功返回true，否则返回false
     */
    boolean update(User user);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除成功返回true，否则返回false
     */
    boolean delete(Integer id);
    
    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 存在返回true，否则返回false
     */
    boolean existsByUsername(String username);
    
    /**
     * 更新用户密码
     * @param id 用户ID
     * @param newPassword 新密码
     * @return 更新成功返回true，否则返回false
     */
    boolean updatePassword(Integer id, String newPassword);
}