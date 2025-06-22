package com.university.bookstore.service;

import com.university.bookstore.model.Book;

import java.math.BigDecimal;
import java.util.List;

/**
 * 图书业务逻辑服务接口
 */
public interface BookService {
    
    /**
     * 根据ID获取图书信息
     * @param id 图书ID
     * @return 图书对象
     */
    Book getBookById(Integer id);
    
    /**
     * 根据ISBN获取图书信息
     * @param isbn ISBN号
     * @return 图书对象
     */
    Book getBookByIsbn(String isbn);
    
    /**
     * 获取所有图书
     * @return 图书列表
     */
    List<Book> getAllBooks();
    
    /**
     * 分页获取图书列表
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @return 图书列表
     */
    List<Book> getBooksWithPagination(int page, int pageSize);
    
    /**
     * 获取图书总数
     * @return 图书总数
     */
    int getTotalBookCount();
    
    /**
     * 根据标题搜索图书
     * @param title 标题关键词
     * @return 图书列表
     */
    List<Book> searchBooksByTitle(String title);
    
    /**
     * 根据作者搜索图书
     * @param author 作者关键词
     * @return 图书列表
     */
    List<Book> searchBooksByAuthor(String author);
    
    /**
     * 根据出版社搜索图书
     * @param publisher 出版社关键词
     * @return 图书列表
     */
    List<Book> searchBooksByPublisher(String publisher);
    
    /**
     * 综合搜索图书
     * @param keyword 关键词
     * @return 图书列表
     */
    List<Book> searchBooks(String keyword);
    
    /**
     * 根据课程ID获取图书列表
     * @param courseId 课程ID
     * @return 图书列表
     */
    List<Book> getBooksByCourseId(Integer courseId);
    
    /**
     * 根据院系ID获取图书列表
     * @param departmentId 院系ID
     * @return 图书列表
     */
    List<Book> getBooksByDepartmentId(Integer departmentId);
    
    /**
     * 添加新图书
     * @param book 图书对象
     * @return 添加成功返回true，否则返回false
     */
    boolean addBook(Book book);
    
    /**
     * 更新图书信息
     * @param book 图书对象
     * @return 更新成功返回true，否则返回false
     */
    boolean updateBook(Book book);
    
    /**
     * 删除图书
     * @param id 图书ID
     * @return 删除成功返回true，否则返回false
     */
    boolean deleteBook(Integer id);
    
    /**
     * 更新图书库存
     * @param id 图书ID
     * @param stock 新库存数量
     * @return 更新成功返回true，否则返回false
     */
    boolean updateBookStock(Integer id, Integer stock);
    
    /**
     * 减少图书库存
     * @param id 图书ID
     * @param quantity 减少数量
     * @return 减少成功返回true，否则返回false
     */
    boolean reduceBookStock(Integer id, Integer quantity);
    
    /**
     * 增加图书库存
     * @param id 图书ID
     * @param quantity 增加数量
     * @return 增加成功返回true，否则返回false
     */
    boolean addBookStock(Integer id, Integer quantity);
    
    /**
     * 检查图书库存是否充足
     * @param id 图书ID
     * @param quantity 需要数量
     * @return 库存充足返回true，否则返回false
     */
    boolean checkBookStock(Integer id, Integer quantity);
    
    /**
     * 获取库存不足的图书列表
     * @param threshold 库存阈值
     * @return 库存不足的图书列表
     */
    List<Book> getLowStockBooks(Integer threshold);
    
    /**
     * 检查ISBN是否已存在
     * @param isbn ISBN号
     * @return 存在返回true，否则返回false
     */
    boolean isIsbnExists(String isbn);
    
    /**
     * 根据价格范围搜索图书
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 图书列表
     */
    List<Book> searchBooksByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * 获取热门图书（根据销量）
     * @param limit 限制数量
     * @return 热门图书列表
     */
    List<Book> getPopularBooks(int limit);
}