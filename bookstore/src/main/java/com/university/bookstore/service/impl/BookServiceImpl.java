package com.university.bookstore.service.impl;

import com.university.bookstore.dao.BookDAO;
import com.university.bookstore.dao.impl.BookDAOImpl;
import com.university.bookstore.model.Book;
import com.university.bookstore.service.BookService;
import com.university.bookstore.util.CacheManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图书业务逻辑服务实现类
 */
public class BookServiceImpl implements BookService {
    
    private final BookDAO bookDAO;
    
    public BookServiceImpl() {
        this.bookDAO = new BookDAOImpl();
    }
    
    @Override
    public Book getBookById(Integer id) {
        if (id == null) {
            return null;
        }
        return bookDAO.findById(id);
    }
    
    @Override
    public Book getBookByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return null;
        }
        return bookDAO.findByIsbn(isbn.trim());
    }
    
    @Override
    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }
    
    /**
     * 强制从数据库获取所有图书（绕过缓存）
     */
    public List<Book> getAllBooksForceRefresh() {
        // 先清除相关缓存
        CacheManager.clearByPattern("books_");
        return bookDAO.findAll();
    }
    
    /**
     * 强制搜索图书（绕过缓存）
     */
    public List<Book> searchBooksForceRefresh(String keyword) {
        // 先清除相关缓存
        CacheManager.clearByPattern("books_");
        return bookDAO.findByTitleLike(keyword);
    }
    
    @Override
    public List<Book> getBooksWithPagination(int page, int pageSize) {
        if (page <= 0 || pageSize <= 0) {
            return new ArrayList<>();
        }
        
        // 使用缓存提高性能
        String cacheKey = CacheManager.generateKey("books_page", page, pageSize);
        List<Book> cachedBooks = CacheManager.get(cacheKey);
        if (cachedBooks != null) {
            return cachedBooks;
        }
        
        int offset = (page - 1) * pageSize;
        List<Book> books = bookDAO.findWithPagination(offset, pageSize);
        
        // 缓存结果
        CacheManager.put(cacheKey, books, 60000); // 缓存1分钟
        
        return books;
    }
    
    /**
     * 根据搜索条件分页获取教材
     */
    public List<Book> searchBooksWithPagination(String keyword, int page, int pageSize) {
        if (page <= 0 || pageSize <= 0) {
            return new ArrayList<>();
        }
        
        // 使用缓存提高性能
        String cacheKey = CacheManager.generateKey("books_search", keyword, page, pageSize);
        List<Book> cachedBooks = CacheManager.get(cacheKey);
        if (cachedBooks != null) {
            return cachedBooks;
        }
        
        int offset = (page - 1) * pageSize;
        List<Book> books = bookDAO.findByTitleWithPagination(keyword, offset, pageSize);
        
        // 缓存结果
        CacheManager.put(cacheKey, books, 60000); // 缓存1分钟
        
        return books;
    }
    
    /**
     * 获取教材总数（带缓存）
     */
    public int getTotalBooksCount() {
        String cacheKey = "books_total_count";
        Integer cachedCount = CacheManager.get(cacheKey);
        if (cachedCount != null) {
            return cachedCount;
        }
        
        int count = bookDAO.getTotalCount();
        CacheManager.put(cacheKey, count, 120000); // 缓存2分钟
        
        return count;
    }
    
    /**
     * 根据搜索条件获取教材总数（带缓存）
     */
    public int getTotalBooksCountByTitle(String keyword) {
        String cacheKey = CacheManager.generateKey("books_search_count", keyword);
        Integer cachedCount = CacheManager.get(cacheKey);
        if (cachedCount != null) {
            return cachedCount;
        }
        
        int count = bookDAO.getTotalCountByTitle(keyword);
        CacheManager.put(cacheKey, count, 120000); // 缓存2分钟
        
        return count;
    }
    
    @Override
    public int getTotalBookCount() {
        return bookDAO.getTotalCount();
    }
    
    @Override
    public List<Book> searchBooksByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookDAO.findByTitleLike(title.trim());
    }
    
    @Override
    public List<Book> searchBooksByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookDAO.findByAuthorLike(author.trim());
    }
    
    @Override
    public List<Book> searchBooksByPublisher(String publisher) {
        if (publisher == null || publisher.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookDAO.findByPublisher(publisher.trim());
    }
    
    @Override
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookDAO.searchBooks(keyword.trim());
    }
    
    @Override
    public List<Book> getBooksByCourseId(Integer courseId) {
        if (courseId == null) {
            return null;
        }
        return bookDAO.findByCourseId(courseId);
    }
    
    @Override
    public List<Book> getBooksByDepartmentId(Integer departmentId) {
        if (departmentId == null) {
            return null;
        }
        return bookDAO.findByDepartmentId(departmentId);
    }
    
    @Override
    public boolean addBook(Book book) {
        if (book == null) {
            return false;
        }
        
        // 验证必填字段
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty() ||
            book.getTitle() == null || book.getTitle().trim().isEmpty() ||
            book.getAuthor() == null || book.getAuthor().trim().isEmpty() ||
            book.getPublisher() == null || book.getPublisher().trim().isEmpty() ||
            book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) <= 0 ||
            book.getStock() == null || book.getStock() < 0) {
            return false;
        }
        
        // 检查ISBN是否已存在
        if (bookDAO.existsByIsbn(book.getIsbn().trim())) {
            return false;
        }
        
        boolean result = bookDAO.insert(book);
        if (result) {
            // 清除相关缓存
            CacheManager.clearByPattern("books_");
        }
        return result;
    }
    
    @Override
    public boolean updateBook(Book book) {
        if (book == null || book.getId() == null) {
            return false;
        }
        
        // 验证必填字段
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty() ||
            book.getTitle() == null || book.getTitle().trim().isEmpty() ||
            book.getAuthor() == null || book.getAuthor().trim().isEmpty() ||
            book.getPublisher() == null || book.getPublisher().trim().isEmpty() ||
            book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) <= 0 ||
            book.getStock() == null || book.getStock() < 0) {
            return false;
        }
        
        // 检查ISBN是否与其他图书冲突
        Book existingBook = bookDAO.findByIsbn(book.getIsbn().trim());
        if (existingBook != null && !existingBook.getId().equals(book.getId())) {
            return false;
        }
        
        boolean result = bookDAO.update(book);
        if (result) {
            // 清除相关缓存
            CacheManager.clearByPattern("books_");
        }
        return result;
    }
    
    @Override
    public boolean deleteBook(Integer id) {
        if (id == null) {
            return false;
        }
        boolean result = bookDAO.delete(id);
        if (result) {
            // 清除相关缓存
            CacheManager.clearByPattern("books_");
        }
        return result;
    }
    
    @Override
    public boolean updateBookStock(Integer id, Integer stock) {
        if (id == null || stock == null || stock < 0) {
            return false;
        }
        boolean result = bookDAO.updateStock(id, stock);
        if (result) {
            // 清除相关缓存
            CacheManager.clearByPattern("books_");
        }
        return result;
    }
    
    @Override
    public boolean reduceBookStock(Integer id, Integer quantity) {
        if (id == null || quantity == null || quantity <= 0) {
            return false;
        }
        
        // 检查库存是否充足
        if (!checkBookStock(id, quantity)) {
            return false;
        }
        
        boolean result = bookDAO.reduceStock(id, quantity);
        if (result) {
            // 清除相关缓存
            CacheManager.clearByPattern("books_");
        }
        return result;
    }
    
    @Override
    public boolean addBookStock(Integer id, Integer quantity) {
        if (id == null || quantity == null || quantity <= 0) {
            return false;
        }
        
        Book book = bookDAO.findById(id);
        if (book == null) {
            return false;
        }
        
        int newStock = book.getStock() + quantity;
        boolean result = bookDAO.updateStock(id, newStock);
        if (result) {
            // 清除相关缓存
            CacheManager.clearByPattern("books_");
        }
        return result;
    }
    
    @Override
    public boolean checkBookStock(Integer id, Integer quantity) {
        if (id == null || quantity == null || quantity <= 0) {
            return false;
        }
        
        Book book = bookDAO.findById(id);
        if (book == null) {
            return false;
        }
        
        return book.getStock() >= quantity;
    }
    
    @Override
    public List<Book> getLowStockBooks(Integer threshold) {
        if (threshold == null || threshold < 0) {
            threshold = 10; // 默认阈值
        }
        return bookDAO.findLowStockBooks(threshold);
    }
    
    @Override
    public boolean isIsbnExists(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        return bookDAO.existsByIsbn(isbn.trim());
    }
    
    @Override
    public List<Book> searchBooksByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null) {
            minPrice = BigDecimal.ZERO;
        }
        if (maxPrice == null) {
            maxPrice = new BigDecimal("999999.99");
        }
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("最低价格不能大于最高价格");
        }
        
        // 创建final变量用于lambda表达式
        final BigDecimal finalMinPrice = minPrice;
        final BigDecimal finalMaxPrice = maxPrice;
        
        // 这里需要在BookDAO中添加相应的方法
        // 暂时返回所有图书并在内存中过滤
        return getAllBooks().stream()
                .filter(book -> book.getPrice().compareTo(finalMinPrice) >= 0 && 
                               book.getPrice().compareTo(finalMaxPrice) <= 0)
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<Book> getPopularBooks(int limit) {
        if (limit <= 0) {
            limit = 10; // 默认返回10本
        }
        
        // 这里需要根据销量排序，暂时返回前N本图书
        List<Book> allBooks = getAllBooks();
        return allBooks.stream()
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }
}