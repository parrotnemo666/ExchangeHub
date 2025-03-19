package com.test;

import com.utils.keys.PemKeyManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;
import javax.crypto.Cipher;

/**
 * PemKeyManager 的多用戶密鑰測試類
 * 驗證用戶特定密鑰對的生成、存儲、載入和使用
 */
public class PemKeyManagerTest {
    private static final Logger logger = LogManager.getLogger(PemKeyManagerTest.class);
    private static final String KEY_DIR = "config/keys";
    
    // 測試用戶數據
    private static final Long TEST_USER_ID_1 = 1001L;
    private static final Long TEST_USER_ID_2 = 1002L;
    private static final String TEST_KEY_ID_1 = UUID.randomUUID().toString();
    private static final String TEST_KEY_ID_2 = UUID.randomUUID().toString();

    public void runFullKeyManagementTest() {
        try {
            logger.info("開始進行多用戶密鑰管理系統測試");
            
            // 1. 測試單例實現
            testSingletonImplementation();
            
            // 2. 測試多用戶密鑰生成
            testMultiUserKeyGeneration();
            
            // 3. 測試密鑰對的加密解密
            testKeyPairEncryptionDecryption();
            
            // 4. 測試不同用戶密鑰隔離
            testKeyIsolation();
            
            // 5. 測試密鑰驗證
            testKeyPairValidation();
            
            logger.info("多用戶密鑰管理系統測試完成 ✅");
        } catch (Exception e) {
            logger.error("密鑰管理系統測試過程中發生錯誤", e);
        } finally {
            // 清理測試數據
//            cleanupTestData();
        }
    }

    /**
     * 測試單例實現
     */
    private void testSingletonImplementation() {
        logger.info("測試單例實現");
        PemKeyManager instance1 = PemKeyManager.getInstance();
        PemKeyManager instance2 = PemKeyManager.getInstance();
        assert instance1 == instance2 : "單例實現測試失敗 ❌";
        logger.info("單例實現測試通過 ✅ ☑");
    }

    /**
     * 測試多用戶密鑰生成
     */
    private void testMultiUserKeyGeneration() throws Exception {
        logger.info("測試多用戶密鑰生成");
        
        PemKeyManager keyManager = PemKeyManager.getInstance();
        
        // 為兩個測試用戶生成密鑰對
        KeyPair keyPair1 = keyManager.generateUserKeyPair(TEST_USER_ID_1, TEST_KEY_ID_1);
        KeyPair keyPair2 = keyManager.generateUserKeyPair(TEST_USER_ID_2, TEST_KEY_ID_2);
        
        // 驗證密鑰目錄結構
        verifyKeyDirectoryStructure(TEST_USER_ID_1, TEST_KEY_ID_1);
        verifyKeyDirectoryStructure(TEST_USER_ID_2, TEST_KEY_ID_2);
        
        logger.info("多用戶密鑰生成測試通過 ✅");
    }

    /**
     * 驗證密鑰目錄結構
     */
    private void verifyKeyDirectoryStructure(Long userId, String keyId) throws Exception {
        String userDir = String.format("%s/%d", KEY_DIR, userId);
        String privateKeyPath = String.format("%s/private_%s.pem", userDir, keyId);
        String publicKeyPath = String.format("%s/public_%s.pem", userDir, keyId);
        
        assert Files.exists(Paths.get(userDir)) : "用戶目錄不存在 ❌";
        assert Files.exists(Paths.get(privateKeyPath)) : "私鑰文件不存在 ❌";
        assert Files.exists(Paths.get(publicKeyPath)) : "公鑰文件不存在 ❌";
        assert Files.size(Paths.get(privateKeyPath)) > 0 : "私鑰文件為空 ❌";
        assert Files.size(Paths.get(publicKeyPath)) > 0 : "公鑰文件為空 ❌";
    }

    /**
     * 測試密鑰對的加密解密
     */
    private void testKeyPairEncryptionDecryption() throws Exception {
        logger.info("測試密鑰對加密解密");
        
        PemKeyManager keyManager = PemKeyManager.getInstance();
        
        // 載入用戶1的密鑰對
        PublicKey publicKey = keyManager.loadUserPublicKey(TEST_USER_ID_1, TEST_KEY_ID_1);
        PrivateKey privateKey = keyManager.loadUserPrivateKey(TEST_USER_ID_1, TEST_KEY_ID_1);
        
        // 測試加密解密
        String originalMessage = "測試用戶專用密鑰加密解密";
        byte[] encrypted = encryptMessage(originalMessage, publicKey);
        String decrypted = decryptMessage(encrypted, privateKey);
        
        assert originalMessage.equals(decrypted) : "密鑰加密解密測試失敗 ❌";
        logger.info("密鑰對加密解密測試通過 ✅");
    }

    /**
     * 測試不同用戶密鑰隔離
     */
    private void testKeyIsolation() throws Exception {
        logger.info("測試密鑰隔離");
        
        PemKeyManager keyManager = PemKeyManager.getInstance();
        
        // 嘗試用用戶2的私鑰解密用戶1的加密數據
        PublicKey publicKey1 = keyManager.loadUserPublicKey(TEST_USER_ID_1, TEST_KEY_ID_1);
        PrivateKey privateKey2 = keyManager.loadUserPrivateKey(TEST_USER_ID_2, TEST_KEY_ID_2);
        
        String testMessage = "測試密鑰隔離";
        byte[] encrypted = encryptMessage(testMessage, publicKey1);
        
        try {
            decryptMessage(encrypted, privateKey2);
            throw new AssertionError("密鑰隔離測試失敗：不應該能用其他用戶的私鑰解密 ❌");
        } catch (Exception e) {
            // 預期會失敗
            logger.info("密鑰隔離測試通過 ✅");
        }
    }

    /**
     * 測試密鑰對驗證
     */
    private void testKeyPairValidation() {
        logger.info("測試密鑰對驗證");
        
        PemKeyManager keyManager = PemKeyManager.getInstance();
        boolean isValid = keyManager.validateUserKeyPair(TEST_USER_ID_1, TEST_KEY_ID_1);
        
        assert isValid : "密鑰對驗證失敗 ❌";
        logger.info("密鑰對驗證測試通過 ✅");
    }

    /**
     * 加密消息
     */
    private byte[] encryptMessage(String message, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(message.getBytes());
    }

    /**
     * 解密消息
     */
    private String decryptMessage(byte[] encrypted, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(encrypted));
    }

    /**
     * 清理測試數據
     */
    private void cleanupTestData() {
        try {
            PemKeyManager keyManager = PemKeyManager.getInstance();
            keyManager.deleteUserKeys(TEST_USER_ID_1, TEST_KEY_ID_1);
            keyManager.deleteUserKeys(TEST_USER_ID_2, TEST_KEY_ID_2);
            logger.info("測試數據清理完成");
        } catch (Exception e) {
            logger.error("清理測試數據時發生錯誤", e);
        }
    }

    /**
     * 主方法，用於單獨運行測試
     */
    public static void main(String[] args) {
        PemKeyManagerTest test = new PemKeyManagerTest();
        test.runFullKeyManagementTest();
    }
}