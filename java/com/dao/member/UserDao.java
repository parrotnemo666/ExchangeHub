package com.dao.member;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.exception.DAOException;
import com.exception.ErrorCodes;
import com.model.member.User;
import com.model.member.UserToken;


/**
 * 用戶資料存取層
 * 負責處理與 users 表的所有數據庫操作
 */
public class UserDao {
    private static final Logger logger = LogManager.getLogger(UserDao.class);
    
    // 數據庫連接配置
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String URL = "jdbc:sqlserver://172.16.46.181:1433;databaseName=ExchangeHub;" + 
                                    "encrypt=true;trustServerCertificate=true;characterEncoding=UTF-8;";
    private static final String USER = "sa";
    private static final String PASSWORD = "Passw0rd";

    /**
     * 靜態初始化塊，加載 JDBC 驅動
     */
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            logger.error("JDBC驅動加載失敗", e);
            throw new DAOException(
                ErrorCodes.DB_DRIVER_ERROR,
                "數據庫驅動加載失敗"
            );
        }
    }

    /**
     * 獲取數據庫連接
     */
    private Connection getConnection() throws DAOException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            logger.error("數據庫連接失敗", e);
            throw new DAOException(
                ErrorCodes.DB_CONNECTION_ERROR,
                "無法建立數據庫連接"
            );
        }
    }

    /**
     * 根據ID查找用戶
     */
    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapToUser(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            logger.error("查詢用戶失敗, userId: {}", id, e);
            throw new DAOException(
                ErrorCodes.DB_QUERY_ERROR,
                String.format("查詢用戶數據失敗(ID: %d)", id)
            );
        }
    }

    /**
     * 根據用戶名查找用戶
     */
    public User findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new DAOException(
                ErrorCodes.DB_INVALID_PARAM,
                "用戶名不能為空"
            );
        }

        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapToUser(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            logger.error("查詢用戶失敗, username: {}", username, e);
            throw new DAOException(
                ErrorCodes.DB_QUERY_ERROR,
                String.format("查詢用戶數據失敗(用戶名: %s)", username)
            );
        }
    }

    /**
     * 創建新用戶
     */
    public User create(User user) {
        // 參數校驗
        validateUserForCreate(user);

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException(
                    ErrorCodes.DB_INSERT_ERROR,
                    "創建用戶失敗，未能插入數據"
                );
            }
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                    return user;
                } else {
                    throw new DAOException(
                        ErrorCodes.DB_INSERT_ERROR,
                        "創建用戶失敗，未能獲取生成的ID"
                    );
                }
            }
        } catch (SQLException e) {
            logger.error("創建用戶失敗, username: {}", user.getUsername(), e);
            throw new DAOException(
                ErrorCodes.DB_INSERT_ERROR,
                String.format("創建用戶失敗(用戶名: %s)", user.getUsername())
            );
        }
    }

    /**
     * 更新用戶資訊
     */
    public void update(User user) {
        // 參數校驗
        validateUserForUpdate(user);

        String sql = "UPDATE users SET username = ?, password = ?, updated_at = GETDATE() WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setLong(3, user.getId());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException(
                    ErrorCodes.DB_UPDATE_ERROR,
                    String.format("更新用戶失敗，找不到指定用戶(ID: %d)", user.getId())
                );
            }
        } catch (SQLException e) {
            logger.error("更新用戶失敗, userId: {}", user.getId(), e);
            throw new DAOException(
                ErrorCodes.DB_UPDATE_ERROR,
                String.format("更新用戶數據失敗(ID: %d)", user.getId())
            );
        }
    }

    /**
     * 刪除用戶
     */
    public void delete(Long id) {
        if (id == null) {
            throw new DAOException(
                ErrorCodes.DB_INVALID_PARAM,
                "用戶ID不能為空"
            );
        }

        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException(
                    ErrorCodes.DB_DELETE_ERROR,
                    String.format("刪除用戶失敗，找不到指定用戶(ID: %d)", id)
                );
            }
        } catch (SQLException e) {
            logger.error("刪除用戶失敗, userId: {}", id, e);
            throw new DAOException(
                ErrorCodes.DB_DELETE_ERROR,
                String.format("刪除用戶數據失敗(ID: %d)", id)
            );
        }
    }

    /**
     * 數據映射
     */
    private User mapToUser(ResultSet rs) throws SQLException {
        try {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return user;
        } catch (SQLException e) {
            logger.error("數據映射失敗", e);
            throw new DAOException(
                ErrorCodes.DB_MAPPING_ERROR,
                "用戶數據映射失敗"
            );
        }
    }

    /**
     * 創建用戶參數校驗
     */
    private void validateUserForCreate(User user) {
        if (user == null) {
            throw new DAOException(
                ErrorCodes.DB_INVALID_PARAM,
                "用戶對象不能為空"
            );
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new DAOException(
                ErrorCodes.DB_INVALID_PARAM,
                "用戶名不能為空"
            );
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new DAOException(
                ErrorCodes.DB_INVALID_PARAM,
                "密碼不能為空"
            );
        }
    }

    /**
     * 更新用戶參數校驗
     */
    private void validateUserForUpdate(User user) {
        if (user == null) {
            throw new DAOException(
                ErrorCodes.DB_INVALID_PARAM,
                "用戶對象不能為空"
            );
        }
        if (user.getId() == null) {
            throw new DAOException(
                ErrorCodes.DB_INVALID_PARAM,
                "用戶ID不能為空"
            );
        }
        validateUserForCreate(user);
    }
}
