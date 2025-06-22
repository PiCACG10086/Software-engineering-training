package com.university.bookstore.dao.impl;

import com.university.bookstore.dao.UserDAO;
import com.university.bookstore.model.User;
import com.university.bookstore.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户数据访问对象实现类
 */
public class UserDAOImpl implements UserDAO {

    /**
     * 根据用户名和密码查找用户（用于登录验证）
     */
    @Override
    public User findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM t_user WHERE username = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("查询用户失败: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据用户名查找用户
     */
    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM t_user WHERE username = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("根据用户名查询用户失败: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据ID查找用户
     */
    @Override
    public User findById(Integer id) {
        String sql = "SELECT * FROM t_user WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("根据ID查询用户失败: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查找所有用户
     */
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM t_user ORDER BY create_time DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有用户失败: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    /**
     * 根据角色查找用户
     */
    @Override
    public List<User> findByRole(User.UserRole role) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM t_user WHERE role = ? ORDER BY create_time DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, role.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("根据角色查询用户失败: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    /**
     * 插入新用户
     */
    @Override
    public boolean insert(User user) {
        String sql = "INSERT INTO t_user (username, password, role, name, student_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().name());
            stmt.setString(4, user.getName());
            stmt.setString(5, user.getStudentId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                // 获取生成的主键
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("插入用户失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新用户信息
     */
    @Override
    public boolean update(User user) {
        String sql = "UPDATE t_user SET username = ?, role = ?, name = ?, student_id = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getRole().name());
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getStudentId());
            stmt.setInt(5, user.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("更新用户失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除用户
     */
    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM t_user WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("删除用户失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查用户名是否已存在
     */
    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM t_user WHERE username = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("检查用户名是否存在失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新用户密码
     */
    @Override
    public boolean updatePassword(Integer id, String newPassword) {
        String sql = "UPDATE t_user SET password = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newPassword);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("更新用户密码失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将ResultSet映射为User对象
     * @param rs ResultSet对象
     * @return User对象
     * @throws SQLException SQL异常
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(User.UserRole.valueOf(rs.getString("role")));
        user.setName(rs.getString("name"));
        user.setStudentId(rs.getString("student_id"));
        user.setCreateTime(rs.getTimestamp("create_time"));
        return user;
    }
}