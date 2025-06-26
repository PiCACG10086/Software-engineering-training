package com.university.bookstore.service;

import com.university.bookstore.model.Order;
import com.university.bookstore.model.OrderDetail;
import com.university.bookstore.model.CartItem;

import java.util.List;

/**
 * 订单业务逻辑服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     * @param studentId 学生ID
     * @param cartItems 购物车项目列表
     * @return 创建成功返回订单对象，否则返回null
     */
    Order createOrder(Integer studentId, List<CartItem> cartItems);
    
    /**
     * 根据ID获取订单信息
     * @param id 订单ID
     * @return 订单对象
     */
    Order getOrderById(Integer id);
    
    /**
     * 根据订单号获取订单信息
     * @param orderNumber 订单号
     * @return 订单对象
     */
    Order getOrderByOrderNumber(String orderNumber);
    
    /**
     * 根据学生ID获取订单列表
     * @param studentId 学生ID
     * @return 订单列表
     */
    List<Order> getOrdersByStudentId(Integer studentId);
    
    /**
     * 获取所有订单
     * @return 订单列表
     */
    List<Order> getAllOrders();
    
    /**
     * 根据订单状态获取订单列表
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> getOrdersByStatus(Order.OrderStatus status);
    
    /**
     * 分页获取订单列表
     * @param page 页码（从1开始）
     * @param pageSize 每页大小
     * @return 订单列表
     */
    List<Order> getOrdersWithPagination(int page, int pageSize);
    
    /**
     * 获取订单总数
     * @return 订单总数
     */
    int getTotalOrderCount();
    
    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param status 新状态
     * @return 更新成功返回true，否则返回false
     */
    boolean updateOrderStatus(Integer orderId, Order.OrderStatus status);
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @param userId 操作用户ID
     * @return 取消成功返回true，否则返回false
     */
    boolean cancelOrder(Integer orderId, Integer userId);
    
    /**
     * 确认订单（管理员操作）
     * @param orderId 订单ID
     * @return 确认成功返回true，否则返回false
     */
    boolean confirmOrder(Integer orderId);
    
    /**
     * 发货（管理员操作）
     * @param orderId 订单ID
     * @return 发货成功返回true，否则返回false
     */
    boolean shipOrder(Integer orderId);
    
    /**
     * 完成订单
     * @param orderId 订单ID
     * @return 完成成功返回true，否则返回false
     */
    boolean completeOrder(Integer orderId);
    
    /**
     * 删除订单
     * @param orderId 订单ID
     * @return 删除成功返回true，否则返回false
     */
    boolean deleteOrder(Integer orderId);
    
    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单详情列表
     */
    List<OrderDetail> getOrderDetails(Integer orderId);
    
    /**
     * 计算购物车总价
     * @param cartItems 购物车项目列表
     * @return 总价
     */
    java.math.BigDecimal calculateCartTotal(List<CartItem> cartItems);
    
    /**
     * 验证购物车库存
     * @param cartItems 购物车项目列表
     * @return 库存充足返回true，否则返回false
     */
    boolean validateCartStock(List<CartItem> cartItems);
    
    /**
     * 生成订单号
     * @return 唯一订单号
     */
    String generateOrderNumber();
    
    /**
     * 检查订单是否可以取消
     * @param orderId 订单ID
     * @return 可以取消返回true，否则返回false
     */
    boolean canCancelOrder(Integer orderId);
    
    /**
     * 检查订单是否可以发货
     * @param orderId 订单ID
     * @return 可以发货返回true，否则返回false
     */
    boolean canShipOrder(Integer orderId);
    
    /**
     * 获取待处理订单数量
     * @return 待处理订单数量
     */
    int getPendingOrderCount();
    
    /**
     * 获取今日订单数量
     * @return 今日订单数量
     */
    int getTodayOrderCount();
    
    /**
     * 获取学生的订单统计信息
     * @param studentId 学生ID
     * @return 订单统计信息（包含各状态订单数量）
     */
    java.util.Map<Order.OrderStatus, Integer> getStudentOrderStatistics(Integer studentId);
    
    /**
     * 模拟支付订单
     * @param orderId 订单ID
     * @param paymentMethod 支付方式（如：支付宝、微信、银行卡等）
     * @return 支付成功返回true，否则返回false
     */
    boolean payOrder(Integer orderId, String paymentMethod);
    
    /**
     * 检查订单是否可以支付
     * @param orderId 订单ID
     * @return 可以支付返回true，否则返回false
     */
    boolean canPayOrder(Integer orderId);
}