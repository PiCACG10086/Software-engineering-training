package com.university.bookstore.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 订单实体类
 * 对应数据库表 t_order
 */
public class Order {
    private Integer id;
    private String orderNumber;
    private Integer studentId;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private Timestamp createTime;
    private Timestamp updateTime;
    
    // 关联对象
    private User student;
    private List<OrderDetail> orderDetails;

    /**
     * 订单状态枚举
     */
    public enum OrderStatus {
        PENDING("待支付"),
        PAID("已支付"),
        CONFIRMED("已确认"),
        SHIPPED("已发货"),
        COMPLETED("已完成"),
        CANCELLED("已取消");

        private String displayName;

        OrderStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 默认构造函数
     */
    public Order() {}

    /**
     * 带参数的构造函数
     * @param orderNumber 订单号
     * @param studentId 学生ID
     * @param totalPrice 总价
     */
    public Order(String orderNumber, Integer studentId, BigDecimal totalPrice) {
        this.orderNumber = orderNumber;
        this.studentId = studentId;
        this.totalPrice = totalPrice;
        this.status = OrderStatus.PENDING;
    }

    /**
     * 生成订单号
     * @return 订单号
     */
    public static String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis();
    }

    /**
     * 检查订单是否可以取消
     * @return 是否可以取消
     */
    public boolean canCancel() {
        return status == OrderStatus.PENDING || status == OrderStatus.PAID;
    }

    /**
     * 检查订单是否可以发货
     * @return 是否可以发货
     */
    public boolean canShip() {
        return status == OrderStatus.PAID;
    }

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", studentId=" + studentId +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id != null && id.equals(order.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}