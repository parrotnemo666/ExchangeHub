package com.dao.member;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.model.member.UserToken;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.exception.DAOException;
import com.exception.ErrorCodes;


/**
 * Token資料存取層
 * 負責處理與 user_tokens 表的所有數據庫操作
 */
public class TokenDao {
    private static final Logger logger = LogManager.getLogger(TokenDao.class);
    
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
     * 根據TokenID查找Token
     */
    public UserToken findByTokenId(String tokenId) {
        if (tokenId == null || tokenId.trim().isEmpty()) {
            throw new DAOException(
                ErrorCodes.DB_INVALID_PARAM,
                "Token ID不能為空"
            );
        }

        String sql = "SELECT * FROM user_tokens WHERE token_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tokenId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapToToken(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            logger.error("查詢Token失敗, tokenId: {}", tokenId, e);
            throw new DAOException(
                ErrorCodes.DB_QUERY_ERROR,
                String.format("查詢Token失敗(ID: %s)", tokenId)
            );
        }
    }

    /**
     * 查找指定用戶的所有Token
     */
    public List<UserToken> findByUserId(Long userId) {
        if (userId == null) {
            throw new DAOException(
                ErrorCodes.DB_INVALID_PARAM,
                "用戶ID不能為空"
            );
        }

        String sql = "SELECT * FROM user_tokens WHERE user_id = ?";
        List<UserToken> tokens = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tokens.add(mapToToken(rs));
                }
            }
            return tokens;
        } catch (SQLException e) {
            logger.error("查詢用戶Token失敗, userId: {}", userId, e);
            throw new DAOException(
                ErrorCodes.DB_QUERY_ERROR,
                String.format("查詢用戶Token失敗(用戶ID: %d)", userId)
            );
        }
    }

    /**
     * 創建新的Token
     */
    public UserToken create(UserToken token) {
        validateTokenForCreate(token);

        String sql = "INSERT INTO user_tokens (user_id, token_id, token, server_name, issued_at, expiry_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setLong(1, token.getUserId());
            ps.setString(2, token.getTokenId());
            ps.setString(3, token.getToken());
            ps.setString(4, token.getServerName());
            ps.setTimestamp(5, Timestamp.valueOf(token.getIssuedAt()));
            ps.setTimestamp(6, Timestamp.valueOf(token.getExpiryDate()));
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DAOException(
                    ErrorCodes.DB_INSERT_ERROR,
                    "創建Token失敗，未能插入數據"
                );
            }
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    token.setId(rs.getLong(1));
                    return token;
                } else {
                    throw new DAOException(
                        ErrorCodes.DB_INSERT_ERROR,
                        "創建Token失敗，未能獲取生成的ID"
                    );
                }
            }
        } catch (SQLException e) {
            logger.error("創建Token失敗, userId: {}, tokenId: {}", token.getUserId(), token.getTokenId(), e);
            throw new DAOException(
                ErrorCodes.DB_INSERT_ERROR,
                String.format("創建Token失敗(用戶ID: %d)", token.getUserId())
            );
        }
    }

    /**
     * 根據TokenID刪除Token
     */
    public void delete(String tokenId) {
        if (tokenId == null || tokenId.trim().isEmpty()) {
            throw new DAOException(
                ErrorCodes.DB_INVALID_PARAM,
                "Token ID不能為空"
            );
        }

        String sql = "DELETE FROM user_tokens WHERE token_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tokenId);
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException(
                    ErrorCodes.DB_DELETE_ERROR,
                    String.format("刪除Token失敗，找不到指定Token(ID: %s)", tokenId)
                );
            }
        } catch (SQLException e) {
            logger.error("刪除Token失敗, tokenId: {}", tokenId, e);
            throw new DAOException(
                ErrorCodes.DB_DELETE_ERROR,
                String.format("刪除Token失敗(ID: %s)", tokenId)
            );
        }
    }

    /**
     * 刪除所有過期的Token
     */
    public void deleteExpiredTokens() {
        String sql = "DELETE FROM user_tokens WHERE expiry_date < GETDATE()";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            int affectedRows = ps.executeUpdate();
            logger.info("已刪除 {} 個過期Token", affectedRows);
            
        } catch (SQLException e) {
            logger.error("刪除過期Token失敗", e);
            throw new DAOException(
                ErrorCodes.DB_DELETE_ERROR,
                "刪除過期Token失敗"
            );
        }
    }

    /**
     * 數據映射
     */
    private UserToken mapToToken(ResultSet rs) throws SQLException {
        try {
            UserToken token = new UserToken();
            token.setId(rs.getLong("id"));
            token.setUserId(rs.getLong("user_id"));
            token.setTokenId(rs.getString("token_id"));
            token.setToken(rs.getString("token"));
            token.setServerName(rs.getString("server_name"));
            token.setIssuedAt(rs.getTimestamp("issued_at").toLocalDateTime());
            token.setExpiryDate(rs.getTimestamp("expiry_date").toLocalDateTime());
            return token;
        } catch (SQLException e) {
            logger.error("Token數據映射失敗", e);
            throw new DAOException(
                ErrorCodes.DB_MAPPING_ERROR,
                "Token數據映射失敗"
            );
        }
    }

    /**
     * 創建Token參數校驗
     */
    private void validateTokenForCreate(UserToken token) {
        if (token == null) {
            throw new DAOException(
                ErrorCodes.DB_INVALID_PARAM,
                "Token對象不能為空"
            );
        }
//        if (token.getUserId() == null) {
//            throw new DAOException(
//                ErrorCodes.DB_INVALID_PARAM,
//                "用戶ID不能為空"
//            );
//        }
        if (token.getTokenId() == null || token.getTokenId().trim().isEmpty()) {
            throw new DAOException(
                ErrorCodes.DB_INVALID_PARAM,
                "Token ID不能為空"
            );
        }
        if (token.getToken() == null || token.getToken().trim().isEmpty()) {
            throw new DAOException(
                ErrorCodes.DB_INVALID_PARAM,
                "Token內容不能為空"
            );
        }
        if (token.getServerName() == null || token.getServerName().trim().isEmpty()) {
            throw new DAOException(
                ErrorCodes.DB_INVALID_PARAM,
                "服務器名稱不能為空"
            );
        }
        if (token.getIssuedAt() == null) {
            throw new DAOException(
                ErrorCodes.DB_INVALID_PARAM,
                "簽發時間不能為空"
            );
        }
        if (token.getExpiryDate() == null) {
            throw new DAOException(
                ErrorCodes.DB_INVALID_PARAM,
                "過期時間不能為空"
            );
        }
    }
}
//
///**
// * Token資料存取層
// * 負責處理與 user_tokens 表的所有數據庫操作
// */
//public class TokenDao {
//    // 數據庫連接配置
//    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//    private static final String URL = "jdbc:sqlserver://172.16.46.181:1433;databaseName=ExchangeHub;" + 
//                                    "encrypt=true;trustServerCertificate=true;characterEncoding=UTF-8;";
//    private static final String USER = "sa";
//    private static final String PASSWORD = "Passw0rd";
//
//    /**
//     * 靜態初始化塊，加載 JDBC 驅動
//     * 在類首次加載時執行
//     */
//    static {
//        try {
//            Class.forName(DRIVER);
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException("Failed to load JDBC driver", e);
//        }
//    }
//
//    /**
//     * 獲取數據庫連接
//     * @return Connection 數據庫連接對象
//     * @throws SQLException 如果連接失敗拋出異常
//     */
//    private Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(URL, USER, PASSWORD);
//    }
//
//    /**
//     * 根據TokenID查找Token
//     * @param tokenId Token的唯一標識
//     * @return 查找到的Token對象，如果未找到返回null
//     * @throws RuntimeException 當數據庫操作失敗時拋出
//     */
//    public UserToken findByTokenId(String tokenId) {
//        String sql = "SELECT * FROM user_tokens WHERE token_id = ?";
//        try (Connection conn = getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, tokenId);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    return mapToToken(rs);
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Error finding token by id: " + tokenId, e);
//        }
//        return null;
//    }
//
//    /**
//     * 查找指定用戶的所有Token
//     * @param userId 用戶ID
//     * @return Token對象列表，如果未找到返回空列表
//     * @throws RuntimeException 當數據庫操作失敗時拋出
//     */
//    public List<UserToken> findByUserId(Long userId) {
//        String sql = "SELECT * FROM user_tokens WHERE user_id = ?";
//        List<UserToken> tokens = new ArrayList<>();
//        try (Connection conn = getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setLong(1, userId);
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    tokens.add(mapToToken(rs));
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Error finding tokens for user: " + userId, e);
//        }
//        return tokens;
//    }
//
//    /**
//     * 創建新的Token
//     * @param token 要創建的Token對象，不需要設置id
//     * @return 創建後的Token對象，包含生成的id
//     * @throws RuntimeException 當數據庫操作失敗時拋出
//     */
//    public UserToken create(UserToken token) {
//        String sql = "INSERT INTO user_tokens (user_id, token_id, token, server_name, issued_at, expiry_date) " +
//                    "VALUES (?, ?, ?, ?, ?, ?)";
//        try (Connection conn = getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            // 設置參數
//            ps.setLong(1, token.getUserId());
//            ps.setString(2, token.getTokenId());
//            ps.setString(3, token.getToken());
//            ps.setString(4, token.getServerName());
//            ps.setTimestamp(5, Timestamp.valueOf(token.getIssuedAt()));
//            ps.setTimestamp(6, Timestamp.valueOf(token.getExpiryDate()));
//            
//            // 執行插入
//            ps.executeUpdate();
//            
//            // 獲取生成的主鍵
//            try (ResultSet rs = ps.getGeneratedKeys()) {
//                if (rs.next()) {
//                    token.setId(rs.getLong(1));
//                }
//            }
//            return token;
//        } catch (SQLException e) {
//            throw new RuntimeException("Error creating token for user: " + token.getUserId(), e);
//        }
//    }
//
//    /**
//     * 根據TokenID刪除Token
//     * @param tokenId 要刪除的TokenID
//     * @throws RuntimeException 當數據庫操作失敗時拋出
//     */
//    public void delete(String tokenId) {
//        String sql = "DELETE FROM user_tokens WHERE token_id = ?";
//        try (Connection conn = getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, tokenId);
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException("Error deleting token: " + tokenId, e);
//        }
//    }
//
//    /**
//     * 刪除所有過期的Token
//     * @throws RuntimeException 當數據庫操作失敗時拋出
//     */
//    public void deleteExpiredTokens() {
//        String sql = "DELETE FROM user_tokens WHERE expiry_date < GETDATE()";
//        try (Connection conn = getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException("Error deleting expired tokens", e);
//        }
//    }
//
//    /**
//     * 將 ResultSet 映射為 UserToken 對象
//     * @param rs 結果集
//     * @return 映射後的 UserToken 對象
//     * @throws SQLException 當結果集讀取失敗時拋出
//     */
//    private UserToken mapToToken(ResultSet rs) throws SQLException {
//        UserToken token = new UserToken();
//        token.setId(rs.getLong("id"));
//        token.setUserId(rs.getLong("user_id"));
//        token.setTokenId(rs.getString("token_id"));
//        token.setToken(rs.getString("token"));
//        token.setServerName(rs.getString("server_name"));
//        token.setIssuedAt(rs.getTimestamp("issued_at").toLocalDateTime());
//        token.setExpiryDate(rs.getTimestamp("expiry_date").toLocalDateTime());
//        return token;
//    }
//}
