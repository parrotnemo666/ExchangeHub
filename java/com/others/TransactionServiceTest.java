package com.others;


import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.dto.TransactionResponse;
import com.model.transactionInfo.ExchangeTransaction;
import com.model.transactionInfo.PersonalInfo;
import com.model.transactionInfo.TransactionInfo;
import com.service.ExportDuplicateCsvService;

public class TransactionServiceTest {
    public static void main(String[] args) {
        // 建立 Service 實例
        ExportDuplicateCsvService service = new ExportDuplicateCsvService();

        try {
            // 準備測試資料
            TransactionResponse testData = new TransactionResponse();
            List<ExchangeTransaction> duplicates = new ArrayList<>();

            // 建立一筆測試交易
            ExchangeTransaction transaction = new ExchangeTransaction();
            
            PersonalInfo personalInfo = new PersonalInfo();
            personalInfo.setName("王小明");
            personalInfo.setIdNumber("A123456789");
            personalInfo.setBirthDate("1990-01-01");
            personalInfo.setNationality("TWN");
            personalInfo.setPhoneNumber("0912345678");
            personalInfo.setCurrency("USD");
            
            TransactionInfo transactionInfo = new TransactionInfo();
            transactionInfo.setExchangeAmount(1000.0);
            transactionInfo.setRemittanceCode("RC001");
            transactionInfo.setTransactionDescription("測試交易");
            transactionInfo.setTransactionNumber("TX001");
            transactionInfo.setTransactionTime("2024-01-01 10:00:00");
            
            transaction.setPersonalInfo(personalInfo);
            transaction.setTransactionInfo(transactionInfo);
            duplicates.add(transaction);
            
            testData.setDuplicates(duplicates);
            
            // 生成 CSV 並顯示結果
            byte[] csvContent = service.generateDuplicateTransactionsCsv(testData);
            System.out.println(new String(csvContent, StandardCharsets.UTF_8));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
