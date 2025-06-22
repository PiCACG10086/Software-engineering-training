package com.university.bookstore.model;

import java.math.BigDecimal;

/**
 * 订单详情实体类
 * 对应数据库表 t_order_detail
 */
public class OrderDetail {
    private Integer id;
    private Integer orderId;
    private Integer bookId;
    private Integer quantity;
    private BigDecimal unitPrice;
    
    // 关联对象
    private Book book;

    /**
     * 默认构造函数
     */
    public OrderDetail() {}

    /**
     * 带参数的构造函数
     * @param orderId 订单ID
     * @param bookId 教材ID
     * @param quantity 数量
     * @param unitPrice 单价
     */
    public OrderDetail(Integer orderId, Integer bookId, Integer quantity, BigDecimal unitPrice) {
        this.orderId = orderId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * 计算小计金额
     * @return 小计金额
     */
    public BigDecimal getSubtotal() {
        if (unitPrice != null && quantity != null) {
            return unitPrice.multiply(new BigDecimal(quantity));
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * 设置小计金额（通常用于从数据库读取时）
     * @param subtotal 小计金额
     */
    public void setSubtotal(BigDecimal subtotal) {
        // 这个方法主要用于数据库映射，实际小计通过getSubtotal()计算
        // 可以选择忽略设置的值，或者用于验证
    }

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", bookId=" + bookId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetail that = (OrderDetail) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}