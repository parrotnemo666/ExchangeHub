//package com.test;
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//import org.junit.Before;
//import org.junit.Test;
//
//import com.model.ProcessResult;
//import com.service.ExchangeService111;
//
//import static org.junit.Assert.*;
//
//public class ExchangeServiceUnitTest {
//    private ExchangeService111 exchangeService111;
//    
//    @Before
//    public void setUp() {
//        exchangeService111 = new ExchangeService111();
//    }
//    
////    @Test
////    public void testProcessExchangeList_NormalCase() {
////        // 準備測試數據
////        String csvContent = 
////            "姓名,身分證字號,生日,國籍,電話,幣別,結匯金額,匯款代碼,交易描述,交易時間,交易序號\n" +
////            "王小明,A123456789,1990-01-01,TWN,0912345678,USD,1000.00,RC001,一般匯款,2024-01-01 10:00:00,TX001";
////        
////        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
////        
////        // 執行測試
////        ProcessResult result = exchangeService.processExchangeList(inputStream);
////        
////        // 驗證結果
////        assertTrue(result.isSuccess());
////        assertEquals(1, result.getProcessedCount());
////        assertEquals(0, result.getDuplicateCount());
////    }
//    
////    @Test    //ok
////    public void testProcessExchangeList_DuplicateCase() {
////        // 準備包含重複交易的測試數據
////        String csvContent = 
////            "姓名,身分證字號,生日,國籍,電話,幣別,結匯金額,匯款代碼,交易描述,交易時間,交易序號\n" +
////            "王小明,A123456789,1990-01-01,TWN,0912345678,USD,1000.00,RC001,一般匯款,2024-01-01 10:00:00,TX001\n" +
////            "李大華,A123456786,1995-05-05,TWN,0987654321,USD,1000.00,RC002,一般匯款,2024-01-01 11:00:00,TX003";
////            
////        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
////        
////        // 執行測試
////        ProcessResult result = exchangeService.processExchangeList(inputStream);
////        System.out.println(result.getProcessedCount());
////        System.out.println(result.getDuplicateCount());
////        
////        // 驗證結果
////        assertTrue(result.isSuccess());  //交易流程是否完成
////        assertEquals(2, result.getProcessedCount());  //總共多少筆交易成功
////        assertEquals(1, result.getDuplicateCount());  //總共多少筆交易重複
////    }
////    
////    @Test  //ok
////    public void testProcessExchangeList_InvalidFormat() {
////        // 準備格式錯誤的測試數據
////        String csvContent = 
////            "姓名,身分證字號\n" +
////            "王小明,A123456789";
////            
////        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
////        
////        // 執行測試
////        ProcessResult result = exchangeService.processExchangeList(inputStream);
////        System.out.println(result.isSuccess());
////        System.out.println(result.getMessage());
////        // 驗證結果
////        assertFalse(result.isSuccess());
////        assertTrue(result.getMessage().contains("處理失敗"));
////    }
////    
////    @Test   //ok
////    public void testProcessExchangeList_EmptyFile() {
////        // 準備空檔案測試數據
////        String csvContent = "姓名,身分證字號,生日,國籍,電話,幣別,結匯金額,匯款代碼,交易描述,交易時間,交易序號\n";
////            
////        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
////        
////        // 執行測試
////        ProcessResult result = exchangeService.processExchangeList(inputStream);
////        
////        System.out.println(result.isSuccess());
////        System.out.println(result.getProcessedCount());
////        System.out.println(result.getDuplicateCount());
////        
////        // 驗證結果
////        assertFalse(result.isSuccess());
////        assertEquals(0, result.getProcessedCount());
////        assertEquals(0, result.getDuplicateCount());
////    }
////    
////    @Test(expected = IllegalArgumentException.class)
////    public void testProcessExchangeList_NullInputStream() {
////        // 測試空輸入流
////        exchangeService.processExchangeList(null);
////    }
////    
////    @Test   //OK
////    public void testProcessExchangeList_MultipleTransactions() {
////        // 準備多筆交易測試數據
////        String csvContent = 
////            "姓名,身分證字號,生日,國籍,電話,幣別,結匯金額,匯款代碼,交易描述,交易時間,交易序號\n" +
////            "王小明,A123456787,1990-01-01,TWN,0912345678,USD,1000.00,RC001,一般匯款,2024-01-01 10:00:00,TX001\n" +
////            "李大華,B987654327,1995-05-05,TWN,0987654321,USD,2000.00,RC002,一般匯款,2024-01-01 11:00:00,TX002\n" +
////            "張三,C456789017,1985-12-31,TWN,0923456789,USD,3000.00,RC003,一般匯款,2024-01-01 12:00:00,TX003";
////            
////        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
////        
////        // 執行測試
////        ProcessResult result = exchangeService.processExchangeList(inputStream);
////        
////        // 驗證結果
////        assertTrue(result.isSuccess());
////        assertEquals(3, result.getProcessedCount());
////        assertEquals(0, result.getDuplicateCount());
////    }
//}