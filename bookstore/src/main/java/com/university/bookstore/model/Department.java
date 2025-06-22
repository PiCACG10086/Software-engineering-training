package com.university.bookstore.model;

/**
 * 院系实体类
 * 对应数据库表 t_department
 */
public class Department {
    private Integer id;
    private String name;

    /**
     * 默认构造函数
     */
    public Department() {}

    /**
     * 带参数的构造函数
     * @param name 院系名称
     */
    public Department(String name) {
        this.name = name;
    }

    /**
     * 带参数的构造函数
     * @param id 院系ID
     * @param name 院系名称
     */
    public Department(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name; // 用于ComboBox显示
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}