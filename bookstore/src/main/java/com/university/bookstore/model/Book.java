package com.university.bookstore.model;

import javafx.beans.property.*;
import java.math.BigDecimal;

/**
 * 教材实体类
 * 对应数据库表 t_book
 */
public class Book {
    private Integer id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private BigDecimal price;
    private Integer stock;
    private String description;

    /**
     * 默认构造函数
     */
    public Book() {}

    /**
     * 带参数的构造函数
     * @param isbn ISBN号
     * @param title 书名
     * @param author 作者
     * @param publisher 出版社
     * @param price 价格
     * @param stock 库存
     */
    public Book(String isbn, String title, String author, String publisher, BigDecimal price, Integer stock) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.price = price;
        this.stock = stock;
    }

    /**
     * 检查库存是否充足
     * @param quantity 需要的数量
     * @return 是否有足够库存
     */
    public boolean hasEnoughStock(int quantity) {
        return stock != null && stock >= quantity;
    }

    /**
     * 减少库存
     * @param quantity 减少的数量
     * @return 操作是否成功
     */
    public boolean reduceStock(int quantity) {
        if (hasEnoughStock(quantity)) {
            this.stock -= quantity;
            return true;
        }
        return false;
    }

    /**
     * 增加库存
     * @param quantity 增加的数量
     */
    public void addStock(int quantity) {
        if (this.stock == null) {
            this.stock = 0;
        }
        this.stock += quantity;
    }
    
    /**
     * JavaFX属性：价格
     * @return 价格属性
     */
    public ObjectProperty<BigDecimal> priceProperty() {
        ObjectProperty<BigDecimal> priceProp = new SimpleObjectProperty<>();
        priceProp.set(this.price);
        return priceProp;
    }

    // Getter和Setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id != null && id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}