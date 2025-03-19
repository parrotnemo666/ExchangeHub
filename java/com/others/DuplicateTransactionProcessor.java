//package com.others;
//import com.dto.TransactionResponse;
//import com.google.gson.Gson;
//import com.model.transactionInfo.ExchangeTransaction;
//import com.model.transactionInfo.PersonalInfo;
//import com.model.transactionInfo.TransactionInfo;
//
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//public class DuplicateTransactionProcessor {
//    public static void main(String[] args) {
//        // 步驟1：準備JSON字串
//        String jsonString = "{ \"duplicateCount\": 2, \"duplicates\": [ { \"personalInfo\": { \"birthDate\": \"1980-01-01\", \"currency\": \"USD\", \"idNumber\": \"A123456789\", \"name\": \"John Doe\", \"nationality\": \"USA\", \"phoneNumber\": \"123456789\", \"residencePermitExpiryDate\": \"2025-01-01\", \"residencePermitIssueDate\": \"2020-01-01\" }, \"transactionInfo\": { \"exchangeAmount\": 1000.0, \"remittanceCode\": \"RC001\", \"transactionDescription\": \"Transaction 1\", \"transactionNumber\": \"TX001\", \"transactionTime\": \"2023-04-01 10:00:00\" } }, { \"personalInfo\": { \"birthDate\": \"1985-05-15\", \"currency\": \"CAD\", \"idNumber\": \"B987654321\", \"name\": \"Jane Smith\", \"nationality\": \"Canada\", \"phoneNumber\": \"987654321\", \"residencePermitExpiryDate\": \"2023-06-01\", \"residencePermitIssueDate\": \"2018-06-01\" }, \"transactionInfo\": { \"exchangeAmount\": 2000.0, \"remittanceCode\": \"RC002\", \"transactionDescription\": \"Transaction 2\", \"transactionNumber\": \"TX002\", \"transactionTime\": \"2023-04-02 11:00:00\" } } ], \"nonDuplicateCount\": 1, \"nonDuplicates\": [ { \"personalInfo\": { \"birthDate\": \"1980-01-01\", \"currency\": \"USD\", \"idNumber\": \"A123456789\", \"name\": \"John Doe\", \"nationality\": \"USA\", \"phoneNumber\": \"123456789\", \"residencePermitExpiryDate\": \"2025-01-01\", \"residencePermitIssueDate\": \"2020-01-01\" }, \"transactionInfo\": { \"exchangeAmount\": 1000.0, \"remittanceCode\": \"RC003\", \"transactionDescription\": \"Transaction 3\", \"transactionNumber\": \"TX003\", \"transactionTime\": \"2023-04-03 12:00:00\" } } ]}";
//
//        try {
//            // 步驟2：使用GSON解析JSON字串
//            System.out.println("開始解析JSON資料...");
//            Gson gson = new Gson();
//            TransactionResponse response = gson.fromJson(jsonString, TransactionResponse.class);
//            
//            // 步驟3：提取重複交易資料
//            List<ExchangeTransaction> duplicates = response.getDuplicates();
//            System.out.println("找到 " + duplicates.size() + " 筆重複交易");
//
//            // 步驟4：生成CSV內容
//            String csvContent = convertDuplicatesToCsv(duplicates);
//
//            // 步驟5：將CSV內容寫入檔案
//            String fileName = generateFileName();
//            writeToFile(fileName, csvContent);
//
//            // 步驟6：顯示處理結果
//            System.out.println("\n處理完成！");
//            System.out.println("CSV檔案已生成：" + fileName);
//            System.out.println("\n檔案內容預覽：");
//            System.out.println("----------------------------------------");
//            System.out.println(csvContent);
//            System.out.println("----------------------------------------");
//
//        } catch (Exception e) {
//            System.err.println("處理過程發生錯誤：");
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 將重複交易列表轉換為CSV格式
//     */
//    private static String convertDuplicatesToCsv(List<ExchangeTransaction> duplicates) {
//        StringBuilder csv = new StringBuilder();
//        
//        // 加入CSV標題行
//        csv.append("交易序號,姓名,身分證字號,生日,國籍,")
//           .append("居留證核發日期,居留證有效期限,電話,")
//           .append("幣別,結匯金額,匯款性質分類編號,")
//           .append("交易商品描述,交易時間\n");
//
//        // 處理每一筆重複交易
//        for (ExchangeTransaction transaction : duplicates) {
//            PersonalInfo pi = transaction.getPersonalInfo();
//            TransactionInfo ti = transaction.getTransactionInfo();
//
//            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%.2f,%s,%s,%s\n",
//                escapeField(ti.getTransactionNumber()),
//                escapeField(pi.getName()),
//                escapeField(pi.getIdNumber()),
//                escapeField(pi.getBirthDate()),
//                escapeField(pi.getNationality()),
//                escapeField(pi.getResidencePermitIssueDate()),
//                escapeField(pi.getResidencePermitExpiryDate()),
//                escapeField(pi.getPhoneNumber()),
//                escapeField(pi.getCurrency()),
//                ti.getExchangeAmount(),
//                escapeField(ti.getRemittanceCode()),
//                escapeField(ti.getTransactionDescription()),
//                escapeField(ti.getTransactionTime())
//            ));
//        }
//
//        return csv.toString();
//    }
//
//    /**
//     * 處理CSV欄位中的特殊字元
//     */
//    private static String escapeField(String field) {
//        if (field == null) {
//            return "";
//        }
//        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
//            return "\"" + field.replace("\"", "\"\"") + "\"";
//        }
//        return field;
//    }
//
//    /**
//     * 生成包含時間戳記的檔案名稱
//     */
//    private static String generateFileName() {
//        String timestamp = LocalDateTime.now().format(
//            DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//        return "D:/duplicate_transactions_" + timestamp + ".csv";
//    } 
//
//    /**
//     * 將內容寫入檔案
//     */
//    private static void writeToFile(String fileName, String content) throws IOException {
//        try (FileWriter writer = new FileWriter(fileName, StandardCharsets.UTF_8)) {
//            writer.write(content);
//        }
//    }
//}