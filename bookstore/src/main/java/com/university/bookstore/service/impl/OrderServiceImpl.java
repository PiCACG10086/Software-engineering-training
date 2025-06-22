package com.university.bookstore.service.impl;

import com.university.bookstore.dao.OrderDAO;
import com.university.bookstore.dao.impl.OrderDAOImpl;
import com.university.bookstore.model.Order;
import com.university.bookstore.model.OrderDetail;
import com.university.bookstore.model.CartItem;
import com.university.bookstore.service.OrderService;
import com.university.bookstore.service.BookService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单业务逻辑服务实现类
 */
public class OrderServiceImpl implements OrderService {
    
    private final OrderDAO orderDAO;
    private final BookService bookService;
    
    public OrderServiceImpl() {
        this.orderDAO = new OrderDAOImpl();
        this.bookService = new BookServiceImpl();
    }
    
    @Override
    public Order createOrder(Integer studentId, List<CartItem> cartItems) {
        if (studentId == null || cartItems == null || cartItems.isEmpty()) {
            return null;
        }
        
        // 验证库存
        if (!validateCartStock(cartItems)) {
            return null;
        }
        
        // 计算总价
        BigDecimal totalPrice = calculateCartTotal(cartItems);
        
        // 创建订单
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setStudentId(studentId);
        order.setTotalPrice(totalPrice);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis()));
        
        // 插入订单
        if (!orderDAO.insert(order)) {
            return null;
        }
        
        // 创建订单详情
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(order.getId());
            detail.setBookId(item.getBook().getId());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getBook().getPrice());
            orderDetails.add(detail);
        }
        
        // 批量插入订单详情
        if (!orderDAO.insertOrderDetails(orderDetails)) {
            // 如果插入订单详情失败，删除已创建的订单
            orderDAO.delete(order.getId());
            return null;
        }
        
        // 减少库存
        for (CartItem item : cartItems) {
            if (!bookService.reduceBookStock(item.getBook().getId(), item.getQuantity())) {
                // 如果减少库存失败，需要回滚
                // 这里简化处理，实际应该使用事务
                return null;
            }
        }
        
        return order;
    }
    
    @Override
    public Order getOrderById(Integer id) {
        if (id == null) {
            return null;
        }
        return orderDAO.findById(id);
    }
    
    @Override
    public Order getOrderByOrderNumber(String orderNumber) {
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            return null;
        }
        return orderDAO.findByOrderNumber(orderNumber.trim());
    }
    
    @Override
    public List<Order> getOrdersByStudentId(Integer studentId) {
        if (studentId == null) {
            return new ArrayList<>();
        }
        return orderDAO.findByStudentId(studentId);
    }
    
    @Override
    public List<Order> getAllOrders() {
        return orderDAO.findAll();
    }
    
    @Override
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        if (status == null) {
            return new ArrayList<>();
        }
        return orderDAO.findByStatus(status);
    }
    
    @Override
    public List<Order> getOrdersWithPagination(int page, int pageSize) {
        if (page < 1 || pageSize < 1) {
            throw new IllegalArgumentException("页码和页面大小必须大于0");
        }
        
        int offset = (page - 1) * pageSize;
        return orderDAO.findWithPagination(offset, pageSize);
    }
    
    @Override
    public int getTotalOrderCount() {
        return orderDAO.getTotalCount();
    }
    
    @Override
    public boolean updateOrderStatus(Integer orderId, Order.OrderStatus status) {
        if (orderId == null || status == null) {
            return false;
        }
        return orderDAO.updateStatus(orderId, status);
    }
    
    @Override
    public boolean cancelOrder(Integer orderId, Integer userId) {
        if (orderId == null || userId == null) {
            return false;
        }
        
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            return false;
        }
        
        // 检查是否可以取消
        if (!canCancelOrder(orderId)) {
            return false;
        }
        
        // 恢复库存
        List<OrderDetail> details = orderDAO.findOrderDetailsByOrderId(orderId);
        for (OrderDetail detail : details) {
            bookService.addBookStock(detail.getBookId(), detail.getQuantity());
        }
        
        // 更新订单状态
        return orderDAO.updateStatus(orderId, Order.OrderStatus.CANCELLED);
    }
    
    @Override
    public boolean confirmOrder(Integer orderId) {
        if (orderId == null) {
            return false;
        }
        
        Order order = orderDAO.findById(orderId);
        if (order == null || order.getStatus() != Order.OrderStatus.PENDING) {
            return false;
        }
        
        return orderDAO.updateStatus(orderId, Order.OrderStatus.CONFIRMED);
    }
    
    @Override
    public boolean shipOrder(Integer orderId) {
        if (orderId == null) {
            return false;
        }
        
        if (!canShipOrder(orderId)) {
            return false;
        }
        
        return orderDAO.updateStatus(orderId, Order.OrderStatus.SHIPPED);
    }
    
    @Override
    public boolean completeOrder(Integer orderId) {
        if (orderId == null) {
            return false;
        }
        
        Order order = orderDAO.findById(orderId);
        if (order == null || order.getStatus() != Order.OrderStatus.SHIPPED) {
            return false;
        }
        
        return orderDAO.updateStatus(orderId, Order.OrderStatus.COMPLETED);
    }
    
    @Override
    public boolean deleteOrder(Integer orderId) {
        if (orderId == null) {
            return false;
        }
        
        // 先删除订单详情
        orderDAO.deleteOrderDetails(orderId);
        
        // 再删除订单
        return orderDAO.delete(orderId);
    }
    
    @Override
    public List<OrderDetail> getOrderDetails(Integer orderId) {
        if (orderId == null) {
            return new ArrayList<>();
        }
        return orderDAO.findOrderDetailsByOrderId(orderId);
    }
    
    @Override
    public BigDecimal calculateCartTotal(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            total = total.add(item.getSubtotal());
        }
        
        return total;
    }
    
    @Override
    public boolean validateCartStock(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return false;
        }
        
        for (CartItem item : cartItems) {
            if (!bookService.checkBookStock(item.getBook().getId(), item.getQuantity())) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String generateOrderNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        
        // 添加随机数确保唯一性
        Random random = new Random();
        int randomNum = random.nextInt(1000);
        
        String orderNumber = "ORD" + timestamp + String.format("%03d", randomNum);
        
        // 检查订单号是否已存在
        while (orderDAO.existsByOrderNumber(orderNumber)) {
            randomNum = random.nextInt(1000);
            orderNumber = "ORD" + timestamp + String.format("%03d", randomNum);
        }
        
        return orderNumber;
    }
    
    @Override
    public boolean canCancelOrder(Integer orderId) {
        if (orderId == null) {
            return false;
        }
        
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            return false;
        }
        
        // 只有待处理和已确认的订单可以取消
        return order.getStatus() == Order.OrderStatus.PENDING || 
               order.getStatus() == Order.OrderStatus.CONFIRMED;
    }
    
    @Override
    public boolean canShipOrder(Integer orderId) {
        if (orderId == null) {
            return false;
        }
        
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            return false;
        }
        
        // 只有已确认的订单可以发货
        return order.getStatus() == Order.OrderStatus.CONFIRMED;
    }
    
    @Override
    public int getPendingOrderCount() {
        return getOrdersByStatus(Order.OrderStatus.PENDING).size();
    }
    
    @Override
    public int getTodayOrderCount() {
        // 获取今天的开始时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();
        
        // 获取今天的结束时间
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date startOfNextDay = calendar.getTime();
        
        // 统计今天的订单
        List<Order> allOrders = getAllOrders();
        int count = 0;
        for (Order order : allOrders) {
            if (order.getCreateTime().compareTo(startOfDay) >= 0 && 
                order.getCreateTime().compareTo(startOfNextDay) < 0) {
                count++;
            }
        }
        
        return count;
    }
    
    @Override
    public Map<Order.OrderStatus, Integer> getStudentOrderStatistics(Integer studentId) {
        Map<Order.OrderStatus, Integer> statistics = new HashMap<>();
        
        // 初始化所有状态的计数为0
        for (Order.OrderStatus status : Order.OrderStatus.values()) {
            statistics.put(status, 0);
        }
        
        if (studentId == null) {
            return statistics;
        }
        
        List<Order> orders = getOrdersByStudentId(studentId);
        for (Order order : orders) {
            Order.OrderStatus status = order.getStatus();
            statistics.put(status, statistics.get(status) + 1);
        }
        
        return statistics;
    }
}