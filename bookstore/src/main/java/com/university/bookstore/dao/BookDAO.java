package com.university.bookstore.dao;

import com.university.bookstore.model.Book;

import java.util.List;

/**
 * 教材数据访问对象接口
 */
public interface BookDAO {
    
    /**
     * 根据ID查找教材
     * @param id 教材ID
     * @return 教材对象，如果不存在返回null
     */
    Book findById(Integer id);
    
    /**
     * 根据ISBN查找教材
     * @param isbn ISBN号
     * @return 教材对象，如果不存在返回null
     */
    Book findByIsbn(String isbn);
    
    /**
     * 查找所有教材
     * @return 教材列表
     */
    List<Book> findAll();
    
    /**
     * 根据书名模糊查询教材
     * @param title 书名关键字
     * @return 教材列表
     */
    List<Book> findByTitleLike(String title);
    
    /**
     * 根据作者模糊查询教材
     * @param author 作者关键字
     * @return 教材列表
     */
    List<Book> findByAuthorLike(String author);
    
    /**
     * 根据出版社查询教材
     * @param publisher 出版社
     * @return 教材列表
     */
    List<Book> findByPublisher(String publisher);
    
    /**
     * 根据课程ID查找教材
     * @param courseId 课程ID
     * @return 教材列表
     */
    List<Book> findByCourseId(Integer courseId);
    
    /**
     * 根据院系ID查找教材
     * @param departmentId 院系ID
     * @return 教材列表
     */
    List<Book> findByDepartmentId(Integer departmentId);
    
    /**
     * 综合搜索教材（按书名、作者、ISBN）
     * @param keyword 搜索关键字
     * @return 教材列表
     */
    List<Book> searchBooks(String keyword);
    
    /**
     * 分页查询教材
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 教材列表
     */
    List<Book> findWithPagination(int offset, int limit);
    
    /**
     * 根据搜索条件分页查询教材
     * @param title 书名关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 教材列表
     */
    List<Book> findByTitleWithPagination(String title, int offset, int limit);
    
    /**
     * 获取教材总数
     * @return 教材总数
     */
    int getTotalCount();
    
    /**
     * 根据搜索条件获取教材总数
     * @param title 书名关键词
     * @return 教材总数
     */
    int getTotalCountByTitle(String title);
    
    /**
     * 插入新教材
     * @param book 教材对象
     * @return 插入成功返回true，否则返回false
     */
    boolean insert(Book book);
    
    /**
     * 更新教材信息
     * @param book 教材对象
     * @return 更新成功返回true，否则返回false
     */
    boolean update(Book book);
    
    /**
     * 删除教材
     * @param id 教材ID
     * @return 删除成功返回true，否则返回false
     */
    boolean delete(Integer id);
    
    /**
     * 更新教材库存
     * @param id 教材ID
     * @param stock 新库存数量
     * @return 更新成功返回true，否则返回false
     */
    boolean updateStock(Integer id, Integer stock);
    
    /**
     * 减少教材库存
     * @param id 教材ID
     * @param quantity 减少的数量
     * @return 更新成功返回true，否则返回false
     */
    boolean reduceStock(Integer id, Integer quantity);
    
    /**
     * 检查ISBN是否已存在
     * @param isbn ISBN号
     * @return 存在返回true，否则返回false
     */
    boolean existsByIsbn(String isbn);
    
    /**
     * 获取库存不足的教材列表
     * @param threshold 库存阈值
     * @return 教材列表
     */
    List<Book> findLowStockBooks(int threshold);
}