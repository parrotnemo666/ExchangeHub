package com.dao;

import java.sql.SQLException;

import com.model.transactionInfo.ExchangeTransaction;
import com.model.transactionInfo.PersonalInfo;
import com.model.transactionInfo.TransactionInfo;

//ExchangeTransactionDaoTest.java
public class ExchangeTransactionDaoTest {
	private static ExchangeTransactionDao dao;

	public static void main(String[] args) {
		dao = new ExchangeTransactionDao();

		// 執行所有測試
		testInsert();
//		testCheckDuplicate();
//		testFind();
//		testUpdate();
//		testDelete();
	}

	// 測試新增功能
	private static void testInsert() {
		System.out.println("\n=== 測試新增功能 ===");
		try {
			// 創建測試數據
			ExchangeTransaction transaction = createTestTransaction();

			// 執行新增
			boolean result = dao.insert(transaction);

			// 顯示結果
			System.out.println("新增結果: " + (result ? "成功" : "失敗"));

		} catch (SQLException e) {
			System.err.println("新增測試失敗: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// 測試重複檢查功能
	private static void testCheckDuplicate() {
		System.out.println("\n=== 測試重複檢查功能 ===");
		try {
			boolean isDuplicate = dao.checkDuplicate("TEST001", "A123456786", 1000.0);
			System.out.println("重複檢查結果: " + (isDuplicate ? "找到重複交易" : "無重複交易"));

		} catch (SQLException e) {
			System.err.println("重複檢查測試失敗: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// 測試查詢功能
	private static void testFind() {
		System.out.println("\n=== 測試查詢功能 ===");
		try {
			ExchangeTransaction transaction = dao.findByTransactionNumber("TEST001");

			if (transaction != null) {
				System.out.println("查詢成功，交易詳情：");
				System.out.println("交易號碼: " + transaction.getTransactionInfo().getTransactionNumber());
				System.out.println("交易金額: " + transaction.getTransactionInfo().getExchangeAmount());
				System.out.println("身分證號: " + transaction.getPersonalInfo().getIdNumber());
			} else {
				System.out.println("未找到交易");
			}

		} catch (SQLException e) {
			System.err.println("查詢測試失敗: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// 測試更新功能
	private static void testUpdate() {
		System.out.println("\n=== 測試更新功能 ===");
		try {
			boolean result = dao.update("TEST001", 2000.0, "2024-01-01 12:00:00");
			System.out.println("更新結果: " + (result ? "成功" : "失敗"));

			ExchangeTransaction updated = dao.findByTransactionNumber("TEST001");
			if (updated != null) {
				System.out.println("更新後金額: " + updated.getTransactionInfo().getExchangeAmount());
			}

		} catch (SQLException e) {
			System.err.println("更新測試失敗: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// 測試刪除功能
	private static void testDelete() {
		System.out.println("\n=== 測試刪除功能 ===");
		try {
			boolean result = dao.delete("TEST001");
			System.out.println("刪除結果: " + (result ? "成功" : "失敗"));

			ExchangeTransaction deleted = dao.findByTransactionNumber("TEST001");
			System.out.println("刪除後查詢結果: " + (deleted == null ? "已刪除" : "仍然存在"));

		} catch (SQLException e) {
			System.err.println("刪除測試失敗: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// 創建測試用的交易數據
	private static ExchangeTransaction createTestTransaction() {
		ExchangeTransaction transaction = new ExchangeTransaction();

		// 設置個人信息
		PersonalInfo personalInfo = new PersonalInfo();
		personalInfo.setName("測試用戶");
		personalInfo.setIdNumber("A123456789");
		personalInfo.setBirthDate("1990-01-01");
		personalInfo.setNationality("TWN");
		personalInfo.setPhoneNumber("0912345678");
		personalInfo.setCurrency("TWD");
		transaction.setPersonalInfo(personalInfo);

		// 設置交易信息
		TransactionInfo transactionInfo = new TransactionInfo();
		transactionInfo.setExchangeAmount(1000.0);
		transactionInfo.setTransactionNumber("TEST001");
		transactionInfo.setTransactionTime("2024-01-01 10:00:00");
		transactionInfo.setRemittanceCode("RC001");
		transactionInfo.setTransactionDescription("子玉川ライズショッピングセンタ");
		transaction.setTransactionInfo(transactionInfo);

		return transaction;
	}
}
