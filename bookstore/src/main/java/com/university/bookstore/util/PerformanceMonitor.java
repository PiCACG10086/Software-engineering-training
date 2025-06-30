package com.university.bookstore.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能监控工具类
 * 用于监控方法执行时间和数据库查询性能
 */
public class PerformanceMonitor {
    private static final ConcurrentHashMap<String, AtomicLong> executionCounts = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, AtomicLong> totalExecutionTimes = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> maxExecutionTimes = new ConcurrentHashMap<>();
    
    /**
     * 记录方法执行时间
     * @param methodName 方法名
     * @param executionTime 执行时间（毫秒）
     */
    public static void recordExecution(String methodName, long executionTime) {
        executionCounts.computeIfAbsent(methodName, k -> new AtomicLong(0)).incrementAndGet();
        totalExecutionTimes.computeIfAbsent(methodName, k -> new AtomicLong(0)).addAndGet(executionTime);
        maxExecutionTimes.merge(methodName, executionTime, Math::max);
    }
    
    /**
     * 获取方法平均执行时间
     * @param methodName 方法名
     * @return 平均执行时间（毫秒）
     */
    public static double getAverageExecutionTime(String methodName) {
        AtomicLong count = executionCounts.get(methodName);
        AtomicLong total = totalExecutionTimes.get(methodName);
        
        if (count == null || total == null || count.get() == 0) {
            return 0.0;
        }
        
        return (double) total.get() / count.get();
    }
    
    /**
     * 获取方法最大执行时间
     * @param methodName 方法名
     * @return 最大执行时间（毫秒）
     */
    public static long getMaxExecutionTime(String methodName) {
        return maxExecutionTimes.getOrDefault(methodName, 0L);
    }
    
    /**
     * 获取方法执行次数
     * @param methodName 方法名
     * @return 执行次数
     */
    public static long getExecutionCount(String methodName) {
        AtomicLong count = executionCounts.get(methodName);
        return count != null ? count.get() : 0;
    }
    
    /**
     * 打印性能统计信息
     */
    public static void printStatistics() {
        System.out.println("\n=== 性能统计信息 ===");
        System.out.printf("%-30s %-10s %-15s %-15s%n", "方法名", "执行次数", "平均时间(ms)", "最大时间(ms)");
        System.out.println("-".repeat(70));
        
        for (String methodName : executionCounts.keySet()) {
            System.out.printf("%-30s %-10d %-15.2f %-15d%n",
                methodName,
                getExecutionCount(methodName),
                getAverageExecutionTime(methodName),
                getMaxExecutionTime(methodName)
            );
        }
        
        System.out.println("=== 缓存统计信息 ===");
        System.out.println("缓存大小: " + CacheManager.size());
        System.out.println("数据源信息: " + DBUtil.getDataSourceInfo());
    }
    
    /**
     * 清空统计信息
     */
    public static void clearStatistics() {
        executionCounts.clear();
        totalExecutionTimes.clear();
        maxExecutionTimes.clear();
    }
    
    /**
     * 性能监控装饰器
     * 用于包装需要监控的方法
     */
    public static <T> T monitor(String methodName, MonitoredOperation<T> operation) {
        long startTime = System.currentTimeMillis();
        try {
            T result = operation.execute();
            long executionTime = System.currentTimeMillis() - startTime;
            recordExecution(methodName, executionTime);
            
            // 如果执行时间超过阈值，记录警告
            if (executionTime > 1000) { // 1秒
                System.out.println("警告: 方法 " + methodName + " 执行时间过长: " + executionTime + "ms");
            }
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            recordExecution(methodName + "_ERROR", executionTime);
            throw new RuntimeException("方法执行失败: " + methodName, e);
        }
    }
    
    /**
     * 监控操作接口
     */
    @FunctionalInterface
    public interface MonitoredOperation<T> {
        T execute() throws Exception;
    }
}