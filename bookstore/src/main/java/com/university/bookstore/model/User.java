package com.university.bookstore.model;

import java.sql.Timestamp;

/**
 * 用户实体类
 * 对应数据库表 t_user
 */
public class User {
    private Integer id;
    private String username;
    private String password;
    private UserRole role;
    private String name;
    private String studentId;
    private Timestamp createTime;

    /**
     * 用户角色枚举
     */
    public enum UserRole {
        STUDENT("学生"),
        TEACHER("教师"),
        ADMIN("管理员");

        private String displayName;

        UserRole(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 默认构造函数
     */
    public User() {}

    /**
     * 带参数的构造函数
     * @param username 用户名
     * @param password 密码
     * @param role 角色
     * @param name 真实姓名
     */
    public User(String username, String password, UserRole role, String name) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
    }

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", name='" + name + '\'' +
                ", studentId='" + studentId + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}