package com.dao.member;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.exception.DAOException;
import com.model.member.UserToken;

public class TokenDaoTest {
    public static void main(String[] args) {
        TokenDao tokenDao = new TokenDao();
        
        // 測試創建和查詢 Token
        try {
            System.out.println("===== 測試創建 Token =====");
            UserToken newToken = createSampleToken();
            
            UserToken createdToken = tokenDao.create(newToken);
            System.out.println("創建 Token 成功: " + createdToken.getId());
            
            // 測試通過 TokenId 查詢
            System.out.println("\n===== 測試查詢 Token =====");
            UserToken foundToken = tokenDao.findByTokenId(createdToken.getTokenId());
            System.out.println("查詢 Token 成功: " + foundToken.getTokenId());
            
            // 測試查詢用戶的所有Token
            System.out.println("\n===== 測試查詢用戶的所有 Token =====");
            List<UserToken> userTokens = tokenDao.findByUserId(foundToken.getUserId());
            System.out.println("用戶Token數量: " + userTokens.size());
            
            // 測試刪除 Token
            System.out.println("\n===== 測試刪除 Token =====");
            tokenDao.delete(foundToken.getTokenId());
            System.out.println("刪除 Token 成功");
            
            // 測試刪除過期Token
            System.out.println("\n===== 測試刪除過期 Token =====");
            tokenDao.deleteExpiredTokens();
            System.out.println("過期Token清理完成");
            
        } catch (DAOException e) {
            System.err.println("錯誤代碼: " + e.getCode());
            System.err.println("錯誤信息: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 測試錯誤情況
        System.out.println("\n===== 測試錯誤情況 =====");
        
        // 測試空Token對象
        try {
            tokenDao.create(null);
        } catch (DAOException e) {
            System.out.println("預期的錯誤被捕獲 (空Token對象):");
            System.out.println("錯誤代碼: " + e.getCode());
            System.out.println("錯誤信息: " + e.getMessage());
        }
        
        // 測試無效的TokenId
        try {
            tokenDao.findByTokenId("");
        } catch (DAOException e) {
            System.out.println("\n預期的錯誤被捕獲 (無效TokenId):");
            System.out.println("錯誤代碼: " + e.getCode());
            System.out.println("錯誤信息: " + e.getMessage());
        }
        
        // 測試無效的UserId
        try {
            tokenDao.findByUserId(null);
        } catch (DAOException e) {
            System.out.println("\n預期的錯誤被捕獲 (無效UserId):");
            System.out.println("錯誤代碼: " + e.getCode());
            System.out.println("錯誤信息: " + e.getMessage());
        }
    }
    
    /**
     * 創建測試用的Token對象
     */
    private static UserToken createSampleToken() {
        UserToken token = new UserToken();
//        token.setUserId(88L);  // 假設用戶ID為1
        token.setTokenId(UUID.randomUUID().toString());
        token.setToken("sample.jwt.token");  // 實際應該是JWT格式的token
        token.setServerName("TestServer");
        token.setIssuedAt(LocalDateTime.now());
        token.setExpiryDate(LocalDateTime.now().plusHours(24));
        return token;
    }
}
