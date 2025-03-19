package com.dao.member;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 管理應用程式的資料庫連接。
 * 使用原生 JDBC 驅動進行連接，保留簡單性。
 */
public class DatabaseConnectionToken {

    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String URL = "jdbc:sqlserver://172.16.46.181:1433;databaseName=ExchangeHub;" +
            "encrypt=true;trustServerCertificate=true;characterEncoding=UTF-8;";
    private static final String USER = "sa";
    private static final String PASSWORD = "Passw0rd";

    static {
        try {
            // 載入驅動程式
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("載入資料庫驅動失敗：" + e.getMessage());
            throw new RuntimeException("資料庫驅動初始化失敗", e);
        }
    }

    /**
     * 建立並傳回資料庫連接。
     *
     * @return 代表資料庫連接的 Connection 物件
     * @throws SQLException 如果發生資料庫存取錯誤
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        // 測試資料庫連接
        try (Connection connection = getConnection()) {
            System.out.println("資料庫連接成功！");
        } catch (SQLException e) {
            System.err.println("資料庫連接失敗：" + e.getMessage());
        }
    }
}
