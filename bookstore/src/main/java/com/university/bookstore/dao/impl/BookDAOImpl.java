package com.university.bookstore.dao.impl;

import com.university.bookstore.dao.BookDAO;
import com.university.bookstore.model.Book;
import com.university.bookstore.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 教材数据访问对象实现类
 */
public class BookDAOImpl implements BookDAO {

    /**
     * 根据ID查找教材
     */
    @Override
    public Book findById(Integer id) {
        String sql = "SELECT * FROM t_book WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBook(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("根据ID查询教材失败: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据ISBN查找教材
     */
    @Override
    public Book findByIsbn(String isbn) {
        String sql = "SELECT * FROM t_book WHERE isbn = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, isbn);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBook(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("根据ISBN查询教材失败: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查找所有教材
     */
    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM t_book ORDER BY title";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            System.err.println("查询所有教材失败: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    /**
     * 根据书名模糊查询教材
     */
    @Override
    public List<Book> findByTitleLike(String title) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM t_book WHERE title LIKE ? ORDER BY title";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + title + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("根据书名模糊查询教材失败: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    /**
     * 根据作者模糊查询教材
     */
    @Override
    public List<Book> findByAuthorLike(String author) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM t_book WHERE author LIKE ? ORDER BY title";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + author + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("根据作者模糊查询教材失败: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    /**
     * 根据出版社查询教材
     */
    @Override
    public List<Book> findByPublisher(String publisher) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM t_book WHERE publisher = ? ORDER BY title";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, publisher);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("根据出版社查询教材失败: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    /**
     * 根据课程ID查找教材
     */
    @Override
    public List<Book> findByCourseId(Integer courseId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.* FROM t_book b " +
                    "INNER JOIN t_course_book cb ON b.id = cb.book_id " +
                    "WHERE cb.course_id = ? ORDER BY b.title";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("根据课程ID查询教材失败: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    /**
     * 根据院系ID查找教材
     */
    @Override
    public List<Book> findByDepartmentId(Integer departmentId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT DISTINCT b.* FROM t_book b " +
                    "INNER JOIN t_course_book cb ON b.id = cb.book_id " +
                    "INNER JOIN t_course c ON cb.course_id = c.id " +
                    "WHERE c.department_id = ? ORDER BY b.title";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, departmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("根据院系ID查询教材失败: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    /**
     * 综合搜索教材（按书名、作者、ISBN）
     */
    @Override
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM t_book WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ? ORDER BY title";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("综合搜索教材失败: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    /**
     * 分页查询教材（优化版本）
     */
    @Override
    public List<Book> findWithPagination(int offset, int limit) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM t_book ORDER BY id LIMIT ? OFFSET ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("分页查询教材失败: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }
    
    /**
     * 根据搜索条件分页查询教材
     */
    public List<Book> findByTitleWithPagination(String title, int offset, int limit) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM t_book WHERE title LIKE ? ORDER BY id LIMIT ? OFFSET ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + title + "%");
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("根据标题分页查询教材失败: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }
    
    /**
     * 根据搜索条件获取教材总数
     */
    public int getTotalCountByTitle(String title) {
        String sql = "SELECT COUNT(*) FROM t_book WHERE title LIKE ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + title + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("根据标题获取教材总数失败: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取教材总数
     */
    @Override
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM t_book";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("获取教材总数失败: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 插入新教材
     */
    @Override
    public boolean insert(Book book) {
        String sql = "INSERT INTO t_book (isbn, title, author, publisher, price, stock, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setBigDecimal(5, book.getPrice());
            stmt.setInt(6, book.getStock());
            stmt.setString(7, book.getDescription());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                // 获取生成的主键
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("插入教材失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新教材信息
     */
    @Override
    public boolean update(Book book) {
        String sql = "UPDATE t_book SET isbn = ?, title = ?, author = ?, publisher = ?, price = ?, stock = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setBigDecimal(5, book.getPrice());
            stmt.setInt(6, book.getStock());
            stmt.setString(7, book.getDescription());
            stmt.setInt(8, book.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("更新教材失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除教材
     */
    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM t_book WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("删除教材失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新教材库存
     */
    @Override
    public boolean updateStock(Integer id, Integer stock) {
        String sql = "UPDATE t_book SET stock = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, stock);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("更新教材库存失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 减少教材库存
     */
    @Override
    public boolean reduceStock(Integer id, Integer quantity) {
        String sql = "UPDATE t_book SET stock = stock - ? WHERE id = ? AND stock >= ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantity);
            stmt.setInt(2, id);
            stmt.setInt(3, quantity);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("减少教材库存失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查ISBN是否已存在
     */
    @Override
    public boolean existsByIsbn(String isbn) {
        String sql = "SELECT COUNT(*) FROM t_book WHERE isbn = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, isbn);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("检查ISBN是否存在失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取库存不足的教材列表
     */
    @Override
    public List<Book> findLowStockBooks(int threshold) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM t_book WHERE stock <= ? ORDER BY stock ASC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, threshold);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("获取库存不足教材失败: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    /**
     * 将ResultSet映射为Book对象
     * @param rs ResultSet对象
     * @return Book对象
     * @throws SQLException SQL异常
     */
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPublisher(rs.getString("publisher"));
        book.setPrice(rs.getBigDecimal("price"));
        book.setStock(rs.getInt("stock"));
        book.setDescription(rs.getString("description"));
        return book;
    }
}