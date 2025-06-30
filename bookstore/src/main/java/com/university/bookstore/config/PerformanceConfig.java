package com.university.bookstore.config;

import com.university.bookstore.util.CacheManager;
import com.university.bookstore.util.DBUtil;
import com.university.bookstore.util.PerformanceMonitor;

/**
 * 性能配置类
 * 在应用启动时进行性能相关的初始化配置
 */
public class PerformanceConfig {
    
    private static boolean initialized = false;
    
    /**
     * 初始化性能配置
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        
        System.out.println("正在初始化性能配置...");
        
        // 1. 预热数据库连接池
        warmupConnectionPool();
        
        // 2. 初始化缓存
        initializeCache();
        
        // 3. 设置JVM性能参数
        configureJVMPerformance();
        
        // 4. 启动性能监控
        startPerformanceMonitoring();
        
        initialized = true;
        System.out.println("性能配置初始化完成");
    }
    
    /**
     * 预热数据库连接池
     */
    private static void warmupConnectionPool() {
        System.out.println("预热数据库连接池...");
        
        // 创建一些初始连接来预热连接池
        for (int i = 0; i < 5; i++) {
            try {
                if (DBUtil.testConnection()) {
                    System.out.println("连接池预热成功 - 连接 " + (i + 1));
                }
            } catch (Exception e) {
                System.err.println("连接池预热失败: " + e.getMessage());
            }
        }
        
        // 打印连接池状态
        System.out.println(DBUtil.getDataSourceInfo());
    }
    
    /**
     * 初始化缓存
     */
    private static void initializeCache() {
        System.out.println("初始化缓存系统...");
        
        // 缓存系统已经在CacheManager中自动初始化
        // 这里可以预加载一些常用数据
        
        System.out.println("缓存系统初始化完成");
    }
    
    /**
     * 配置JVM性能参数
     */
    private static void configureJVMPerformance() {
        System.out.println("配置JVM性能参数...");
        
        // 设置系统属性来优化性能
        // 移除可能导致JavaFX显示问题的设置
        // System.setProperty("java.awt.headless", "false"); // JavaFX需要
        // System.setProperty("prism.order", "sw"); // 软件渲染（如果硬件加速有问题）
        // System.setProperty("prism.text", "t2k"); // 文本渲染优化
        
        // 打印JVM信息
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        
        System.out.println("JVM内存信息:");
        System.out.println("  最大内存: " + (maxMemory / 1024 / 1024) + " MB");
        System.out.println("  总内存: " + (totalMemory / 1024 / 1024) + " MB");
        System.out.println("  空闲内存: " + (freeMemory / 1024 / 1024) + " MB");
        System.out.println("  已用内存: " + ((totalMemory - freeMemory) / 1024 / 1024) + " MB");
    }
    
    /**
     * 启动性能监控
     */
    private static void startPerformanceMonitoring() {
        System.out.println("启动性能监控...");
        
        // 启动一个定时任务来定期打印性能统计
        java.util.Timer timer = new java.util.Timer("PerformanceMonitor", true);
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                // 每5分钟打印一次性能统计
                if (PerformanceMonitor.getExecutionCount("loadBooksWithPagination") > 0) {
                    System.out.println("\n=== 性能监控报告 ===");
                    PerformanceMonitor.printStatistics();
                    
                    // 检查内存使用情况
                    Runtime runtime = Runtime.getRuntime();
                    long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                    long maxMemory = runtime.maxMemory();
                    double memoryUsage = (double) usedMemory / maxMemory * 100;
                    
                    System.out.printf("内存使用率: %.2f%%\n", memoryUsage);
                    
                    if (memoryUsage > 80) {
                        System.out.println("警告: 内存使用率过高，建议进行垃圾回收");
                        System.gc();
                    }
                }
            }
        }, 300000, 300000); // 5分钟间隔
        
        System.out.println("性能监控已启动");
    }
    
    /**
     * 关闭性能配置
     */
    public static void shutdown() {
        if (!initialized) {
            return;
        }
        
        System.out.println("关闭性能配置...");
        
        // 打印最终的性能统计
        PerformanceMonitor.printStatistics();
        
        // 关闭缓存管理器
        CacheManager.shutdown();
        
        // 关闭数据库连接池
        DBUtil.closeDataSource();
        
        initialized = false;
        System.out.println("性能配置已关闭");
    }
    
    /**
     * 获取性能建议
     */
    public static void printPerformanceRecommendations() {
        System.out.println("\n=== 性能优化建议 ===");
        
        // 检查数据库查询性能
        double avgBookLoadTime = PerformanceMonitor.getAverageExecutionTime("loadBooksWithPagination");
        if (avgBookLoadTime > 500) {
            System.out.println("建议: 图书加载时间较长(" + avgBookLoadTime + "ms)，考虑优化数据库查询或增加缓存");
        }
        
        // 检查缓存命中率
        int cacheSize = CacheManager.size();
        if (cacheSize == 0) {
            System.out.println("建议: 缓存未被使用，检查缓存配置");
        } else if (cacheSize > 1000) {
            System.out.println("建议: 缓存项过多(" + cacheSize + ")，考虑调整缓存策略");
        }
        
        // 检查内存使用
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        double memoryUsage = (double) usedMemory / maxMemory * 100;
        
        if (memoryUsage > 70) {
            System.out.println("建议: 内存使用率较高(" + String.format("%.2f", memoryUsage) + "%)，考虑增加JVM堆内存");
        }
        
        System.out.println("=== 建议结束 ===");
    }
}