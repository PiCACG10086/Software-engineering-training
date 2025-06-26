package com.university.bookstore.dao;

import com.university.bookstore.model.Order;
import com.university.bookstore.model.OrderDetail;

import java.util.List;

/**
 * 订单数据访问对象接口
 */
public interface OrderDAO {
    
    /**
     * 根据ID查找订单
     * @param id 订单ID
     * @return 订单对象，如果不存在返回null
     */
    Order findById(Integer id);
    
    /**
     * 根据订单号查找订单
     * @param orderNumber 订单号
     * @return 订单对象，如果不存在返回null
     */
    Order findByOrderNumber(String orderNumber);
    
    /**
     * 根据学生ID查找订单
     * @param studentId 学生ID
     * @return 订单列表
     */
    List<Order> findByStudentId(Integer studentId);
    
    /**
     * 根据学生ID和订单状态查找订单
     * @param studentId 学生ID
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> findByStudentIdAndStatus(Integer studentId, Order.OrderStatus status);
    
    /**
     * 查找所有订单
     * @return 订单列表
     */
    List<Order> findAll();
    
    /**
     * 根据订单状态查找订单
     * @param status 订单状态
     * @return 订单列表
     */
    List<Order> findByStatus(Order.OrderStatus status);
    
    /**
     * 分页查询订单
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 订单列表
     */
    List<Order> findWithPagination(int offset, int limit);
    
    /**
     * 获取订单总数
     * @return 订单总数
     */
    int getTotalCount();
    
    /**
     * 插入新订单
     * @param order 订单对象
     * @return 插入成功返回true，否则返回false
     */
    boolean insert(Order order);
    
    /**
     * 更新订单信息
     * @param order 订单对象
     * @return 更新成功返回true，否则返回false
     */
    boolean update(Order order);
    
    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 新状态
     * @return 更新成功返回true，否则返回false
     */
    boolean updateStatus(Integer id, Order.OrderStatus status);
    
    /**
     * 删除订单
     * @param id 订单ID
     * @return 删除成功返回true，否则返回false
     */
    boolean delete(Integer id);
    
    /**
     * 根据订单ID查找订单详情
     * @param orderId 订单ID
     * @return 订单详情列表
     */
    List<OrderDetail> findOrderDetailsByOrderId(Integer orderId);
    
    /**
     * 插入订单详情
     * @param orderDetail 订单详情对象
     * @return 插入成功返回true，否则返回false
     */
    boolean insertOrderDetail(OrderDetail orderDetail);
    
    /**
     * 批量插入订单详情
     * @param orderDetails 订单详情列表
     * @return 插入成功返回true，否则返回false
     */
    boolean insertOrderDetails(List<OrderDetail> orderDetails);
    
    /**
     * 删除订单详情
     * @param orderId 订单ID
     * @return 删除成功返回true，否则返回false
     */
    boolean deleteOrderDetails(Integer orderId);
    
    /**
     * 检查订单号是否已存在
     * @param orderNumber 订单号
     * @return 存在返回true，否则返回false
     */
    boolean existsByOrderNumber(String orderNumber);
}