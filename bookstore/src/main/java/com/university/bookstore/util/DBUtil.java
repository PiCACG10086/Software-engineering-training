package com.university.bookstore.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库连接工具类
 * 使用HikariCP连接池管理数据库连接
 */
public class DBUtil {
    private static HikariDataSource dataSource;
    
    // 数据库连接配置
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bookstore?useUnicode=true&characterEncoding=utf8&useSSL=false";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "123456";
    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";

    static {
        initDataSource();
    }

    /**
     * 初始化数据源
     */
    private static void initDataSource() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USERNAME);
            config.setPassword(DB_PASSWORD);
            config.setDriverClassName(DB_DRIVER);
            
            // 连接池配置优化
            config.setMaximumPoolSize(50); // 增加最大连接数
            config.setMinimumIdle(10);     // 增加最小空闲连接数
            config.setConnectionTimeout(20000); // 减少连接超时时间
            config.setIdleTimeout(300000);      // 减少空闲超时时间(5分钟)
            config.setMaxLifetime(1200000);     // 减少连接最大生存时间(20分钟)
            config.setLeakDetectionThreshold(60000); // 连接泄漏检测(1分钟)
            
            // 性能优化配置
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "false");
            config.setAutoCommit(true); // 确保自动提交
            // 设置事务隔离级别为READ_COMMITTED，确保能读取到已提交的数据
            config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
            // 禁用缓存以确保数据一致性
            config.addDataSourceProperty("useLocalSessionState", "false");
            config.addDataSourceProperty("useLocalTransactionState", "false");
            config.addDataSourceProperty("maintainTimeStats", "false");
            
            // 连接测试
            config.setConnectionTestQuery("SELECT 1");
            
            dataSource = new HikariDataSource(config);
            
            System.out.println("数据库连接池初始化成功");
        } catch (Exception e) {
            System.err.println("数据库连接池初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     * @return 数据库连接
     * @throws SQLException SQL异常
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("数据源未初始化");
        }
        return dataSource.getConnection();
    }

    /**
     * 关闭连接
     * @param connection 数据库连接
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("关闭数据库连接失败: " + e.getMessage());
            }
        }
    }

    /**
     * 测试数据库连接
     * @return 连接是否成功
     */
    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("数据库连接测试失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 关闭数据源
     */
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("数据库连接池已关闭");
        }
    }

    /**
     * 获取数据源信息
     * @return 数据源信息字符串
     */
    public static String getDataSourceInfo() {
        if (dataSource != null) {
            return String.format("连接池状态 - 活跃连接: %d, 空闲连接: %d, 总连接: %d",
                    dataSource.getHikariPoolMXBean().getActiveConnections(),
                    dataSource.getHikariPoolMXBean().getIdleConnections(),
                    dataSource.getHikariPoolMXBean().getTotalConnections());
        }
        return "数据源未初始化";
    }
}