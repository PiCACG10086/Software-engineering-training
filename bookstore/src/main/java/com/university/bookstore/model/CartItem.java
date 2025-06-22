package com.university.bookstore.model;

import javafx.beans.property.*;
import java.math.BigDecimal;

/**
 * 购物车项实体类
 * 用于购物车功能，不对应数据库表
 */
public class CartItem {
    private Book book;
    private Integer quantity;

    /**
     * 默认构造函数
     */
    public CartItem() {}

    /**
     * 带参数的构造函数
     * @param book 教材
     * @param quantity 数量
     */
    public CartItem(Book book, Integer quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    /**
     * 计算小计金额
     * @return 小计金额
     */
    public BigDecimal getSubtotal() {
        if (book != null && book.getPrice() != null && quantity != null) {
            return book.getPrice().multiply(new BigDecimal(quantity));
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * JavaFX属性：小计金额
     * @return 小计金额属性
     */
    public ObjectProperty<BigDecimal> subtotalProperty() {
        ObjectProperty<BigDecimal> subtotalProp = new SimpleObjectProperty<>();
        subtotalProp.set(getSubtotal());
        return subtotalProp;
    }

    /**
     * 增加数量
     * @param amount 增加的数量
     */
    public void addQuantity(int amount) {
        if (this.quantity == null) {
            this.quantity = 0;
        }
        this.quantity += amount;
    }

    /**
     * 检查是否有足够库存
     * @return 是否有足够库存
     */
    public boolean hasEnoughStock() {
        return book != null && book.hasEnoughStock(quantity);
    }

    // Getter和Setter方法
    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "book=" + (book != null ? book.getTitle() : "null") +
                ", quantity=" + quantity +
                ", subtotal=" + getSubtotal() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return book != null && book.equals(cartItem.book);
    }

    @Override
    public int hashCode() {
        return book != null ? book.hashCode() : 0;
    }
}