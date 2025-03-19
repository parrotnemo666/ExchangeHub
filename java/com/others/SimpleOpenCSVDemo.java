package com.others;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleOpenCSVDemo {
    public static void main(String[] args) {
        // 模擬結匯清單資料
        String csvContent = """
                結匯清單,20241127
                姓名,ID/居留證,生日,國籍,居留證核發日期,居留證有效期限,電話,幣別,結匯金額(外幣),匯款性質分類編號,交易商品名稱或服務類別,交易更新時間,交易序號
                邱品品,L123456787,79/06/26,中華民國,,,0988888328,JPY,609.00000,132,二子玉川ライズ,2024-11-26 08:11:41,f23181fe-3de1-487e-9c70
                """;

        System.out.println("=== OpenCSV 基本功能展示 ===\n");

        // 讀取 CSV
        System.out.println("1. 讀取 CSV 資料：");
        readCSV(new StringReader(csvContent));

        // 寫入 CSV
        System.out.println("\n2. 寫入 CSV 資料：");
        writeCSV("output.csv");

        // 讀取實際檔案
        System.out.println("\n3. 從檔案讀取並處理交易資料：");
        processExchangeFile("ExForeign.csv");
    }

    /**
     * 基本 CSV 讀取功能
     */
    private static void readCSV(Reader reader) {
        try (CSVReader csvReader = new CSVReader(reader)) {
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                System.out.println("讀取到的資料: " + Arrays.toString(nextLine));
                // 可以在這裡加入資料處理邏輯
            }
        } catch (Exception e) {
            System.err.println("讀取 CSV 時發生錯誤: " + e.getMessage());
        }
    }

    /**
     * 基本 CSV 寫入功能
     */
    private static void writeCSV(String fileName) {
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"交易序號", "身分證字號", "金額"});
        data.add(new String[]{"TX001", "A123456789", "1000.00"});
        data.add(new String[]{"TX002", "B987654321", "2000.00"});

        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeAll(data);
            System.out.println("成功寫入檔案: " + fileName);
        } catch (IOException e) {
            System.err.println("寫入 CSV 時發生錯誤: " + e.getMessage());
        }
    }

    /**
     * 處理結匯檔案
     */
    private static void processExchangeFile(String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            // 跳過標題列
            reader.readNext();
            String[] nextLine;
            
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length >= 13) {  // 確保資料欄位完整
                    processExchangeRecord(nextLine);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("找不到檔案: " + fileName);
        } catch (Exception e) {
            System.err.println("處理檔案時發生錯誤: " + e.getMessage());
        }
    }

    /**
     * 處理單筆結匯紀錄
     */
    private static void processExchangeRecord(String[] record) {
        String name = record[0];
        String idNumber = record[1];
        String amount = record[8];
        String transactionNumber = record[12];

        System.out.printf("處理交易 - 姓名: %s, 證號: %s, 金額: %s, 交易序號: %s%n",
            name, idNumber, amount, transactionNumber);
    }
}