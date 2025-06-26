package com.university.bookstore.dao.impl;

import com.university.bookstore.dao.OrderDAO;
import com.university.bookstore.model.Order;
import com.university.bookstore.model.OrderDetail;
import com.university.bookstore.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单数据访问对象实现类
 */
public class OrderDAOImpl implements OrderDAO {

    @Override
    public Order findById(Integer id) {
        String sql = "SELECT * FROM t_order WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Order findByOrderNumber(String orderNumber) {
        String sql = "SELECT * FROM t_order WHERE order_number = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToOrder(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Order> findByStudentId(Integer studentId) {
        String sql = "SELECT * FROM t_order WHERE user_id = ? ORDER BY create_time DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public List<Order> findByStatus(Order.OrderStatus status) {
        String sql = "SELECT * FROM t_order WHERE status = ? ORDER BY create_time DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT * FROM t_order ORDER BY create_time DESC";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public List<Order> findWithPagination(int offset, int limit) {
        String sql = "SELECT * FROM t_order ORDER BY create_time DESC LIMIT ? OFFSET ?";
        List<Order> orders = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM t_order";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean insert(Order order) {
        String sql = "INSERT INTO t_order (order_number, user_id, total_amount, status, create_time) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        System.out.println("[DEBUG] 开始插入订单到数据库");
        System.out.println("[DEBUG] 订单信息 - 订单号: " + order.getOrderNumber() + ", 学生ID: " + order.getStudentId() + ", 总价: " + order.getTotalPrice() + ", 状态: " + order.getStatus());
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, order.getOrderNumber());
            stmt.setInt(2, order.getStudentId());
            stmt.setBigDecimal(3, order.getTotalPrice());
            stmt.setString(4, order.getStatus().name());
            stmt.setTimestamp(5, new Timestamp(order.getCreateTime().getTime()));
            
            System.out.println("[DEBUG] 执行SQL: " + sql);
            int affectedRows = stmt.executeUpdate();
            System.out.println("[DEBUG] 影响行数: " + affectedRows);
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = (int) generatedKeys.getLong(1);
                    order.setId(generatedId);
                    System.out.println("[DEBUG] 订单插入成功，生成的订单ID: " + generatedId);
                }
                return true;
            } else {
                System.out.println("[ERROR] 订单插入失败：没有行被影响");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] 订单插入失败，SQL异常: " + e.getMessage());
            System.out.println("[ERROR] SQL状态码: " + e.getSQLState());
            System.out.println("[ERROR] 错误代码: " + e.getErrorCode());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Order order) {
        String sql = "UPDATE t_order SET user_id = ?, total_amount = ?, " +
                    "status = ?, update_time = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, order.getStudentId());
            stmt.setBigDecimal(2, order.getTotalPrice());
            stmt.setString(3, order.getStatus().name());
            stmt.setTimestamp(4, new Timestamp(order.getUpdateTime().getTime()));
            stmt.setInt(5, order.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateStatus(Integer orderId, Order.OrderStatus status) {
        String sql = "UPDATE t_order SET status = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, orderId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM t_order WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<OrderDetail> findOrderDetailsByOrderId(Integer orderId) {
        String sql = "SELECT * FROM t_order_item WHERE order_id = ?";
        List<OrderDetail> details = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                details.add(mapResultSetToOrderDetail(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    @Override
    public boolean insertOrderDetail(OrderDetail orderDetail) {
        String sql = "INSERT INTO t_order_item (order_id, book_id, quantity, price) " +
                    "VALUES (?, ?, ?, ?)";
        
        System.out.println("[DEBUG] 开始插入订单详情到数据库");
        System.out.println("[DEBUG] 订单详情信息 - 订单ID: " + orderDetail.getOrderId() + ", 图书ID: " + orderDetail.getBookId() + ", 数量: " + orderDetail.getQuantity() + ", 价格: " + orderDetail.getPrice());
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, orderDetail.getOrderId());
            stmt.setLong(2, orderDetail.getBookId());
            stmt.setInt(3, orderDetail.getQuantity());
            stmt.setBigDecimal(4, orderDetail.getPrice());
            
            System.out.println("[DEBUG] 执行SQL: " + sql);
            int affectedRows = stmt.executeUpdate();
            System.out.println("[DEBUG] 订单详情影响行数: " + affectedRows);
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = (int) generatedKeys.getLong(1);
                    orderDetail.setId(generatedId);
                    System.out.println("[DEBUG] 订单详情插入成功，生成的ID: " + generatedId);
                }
                return true;
            } else {
                System.out.println("[ERROR] 订单详情插入失败：没有行被影响");
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] 订单详情插入失败，SQL异常: " + e.getMessage());
            System.out.println("[ERROR] SQL状态码: " + e.getSQLState());
            System.out.println("[ERROR] 错误代码: " + e.getErrorCode());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteOrderDetails(Integer orderId) {
        String sql = "DELETE FROM order_details WHERE order_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, orderId);
            return stmt.executeUpdate() >= 0; // 允许删除0行
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean existsByOrderNumber(String orderNumber) {
        String sql = "SELECT COUNT(*) FROM t_order WHERE order_number = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, orderNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getPendingOrderCount() {
        String sql = "SELECT COUNT(*) FROM t_order WHERE status = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, Order.OrderStatus.PENDING.name());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTodayOrderCount() {
        String sql = "SELECT COUNT(*) FROM t_order WHERE DATE(create_time) = CURDATE()";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getStudentOrderCount(Long studentId) {
        String sql = "SELECT COUNT(*) FROM t_order WHERE user_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 将ResultSet映射为Order对象
     */
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setOrderNumber(rs.getString("order_number"));
        order.setStudentId(rs.getInt("user_id"));
        order.setTotalPrice(rs.getBigDecimal("total_amount"));
        order.setStatus(Order.OrderStatus.valueOf(rs.getString("status")));
        order.setCreateTime(rs.getTimestamp("create_time"));
        order.setUpdateTime(rs.getTimestamp("update_time"));
        return order;
    }

    /**
     * 批量插入订单详情
     * @param orderDetails 订单详情列表
     * @return 插入成功返回true，否则返回false
     */
    @Override
    public boolean insertOrderDetails(List<OrderDetail> orderDetails) {
        if (orderDetails == null || orderDetails.isEmpty()) {
            System.out.println("[DEBUG] 订单详情列表为空，跳过插入");
            return true;
        }
        
        System.out.println("[DEBUG] 开始批量插入订单详情，数量: " + orderDetails.size());
        
        String sql = "INSERT INTO t_order_item (order_id, book_id, quantity, price) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            for (int i = 0; i < orderDetails.size(); i++) {
                OrderDetail detail = orderDetails.get(i);
                System.out.println("[DEBUG] 准备插入第" + (i+1) + "个订单详情 - 订单ID: " + detail.getOrderId() + ", 图书ID: " + detail.getBookId() + ", 数量: " + detail.getQuantity() + ", 价格: " + detail.getPrice());
                
                stmt.setInt(1, detail.getOrderId());
                stmt.setInt(2, detail.getBookId());
                stmt.setInt(3, detail.getQuantity());
                stmt.setBigDecimal(4, detail.getPrice());
                stmt.addBatch();
            }
            
            System.out.println("[DEBUG] 执行批量插入SQL: " + sql);
            int[] results = stmt.executeBatch();
            System.out.println("[DEBUG] 批量插入结果数组长度: " + results.length);
            
            // 检查是否所有插入都成功
            for (int i = 0; i < results.length; i++) {
                System.out.println("[DEBUG] 第" + (i+1) + "个订单详情插入结果: " + results[i]);
                if (results[i] <= 0) {
                    System.out.println("[ERROR] 第" + (i+1) + "个订单详情插入失败，影响行数: " + results[i]);
                    return false;
                }
            }
            
            System.out.println("[DEBUG] 所有订单详情插入成功");
            return true;
        } catch (SQLException e) {
            System.out.println("[ERROR] 批量插入订单详情失败，SQL异常: " + e.getMessage());
            System.out.println("[ERROR] SQL状态码: " + e.getSQLState());
            System.out.println("[ERROR] 错误代码: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据学生ID和订单状态查找订单
     */
    @Override
    public List<Order> findByStudentIdAndStatus(Integer studentId, Order.OrderStatus status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM t_order WHERE user_id = ? AND status = ? ORDER BY create_time DESC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setString(2, status.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("根据学生ID和状态查找订单失败: " + e.getMessage());
            e.printStackTrace();
        }
        
        return orders;
    }

    /**
     * 将ResultSet映射为OrderDetail对象
     */
    private OrderDetail mapResultSetToOrderDetail(ResultSet rs) throws SQLException {
        OrderDetail detail = new OrderDetail();
        detail.setId((int) rs.getLong("id"));
        detail.setOrderId((int) rs.getLong("order_id"));
        detail.setBookId((int) rs.getLong("book_id"));
        detail.setQuantity(rs.getInt("quantity"));
        detail.setPrice(rs.getBigDecimal("price"));
        // subtotal通过getSubtotal()方法计算得出，不从数据库读取
        return detail;
    }
}