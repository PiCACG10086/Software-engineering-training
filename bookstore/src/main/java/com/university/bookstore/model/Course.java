package com.university.bookstore.model;

/**
 * 课程实体类
 * 对应数据库表 t_course
 */
public class Course {
    private Integer id;
    private String courseCode;
    private String courseName;
    private Integer departmentId;
    private Integer teacherId;
    
    // 关联对象
    private Department department;
    private User teacher;

    /**
     * 默认构造函数
     */
    public Course() {}

    /**
     * 带参数的构造函数
     * @param courseCode 课程代码
     * @param courseName 课程名称
     * @param departmentId 院系ID
     * @param teacherId 教师ID
     */
    public Course(String courseCode, String courseName, Integer departmentId, Integer teacherId) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.departmentId = departmentId;
        this.teacherId = teacherId;
    }

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return courseCode + " - " + courseName; // 用于ComboBox显示
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id != null && id.equals(course.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}