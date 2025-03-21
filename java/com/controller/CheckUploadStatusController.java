package com.controller;

import java.sql.SQLException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.service.PersistenceService;
import com.dao.ExchangeTransactionDao;
import com.dto.UploadStatus;
import com.model.controllerResult.ErrorResponse;

@Path("/exchange/check")
public class CheckUploadStatusController {
	private static final Logger log = LogManager.getLogger(CheckUploadStatusController.class);
	private final PersistenceService persistenceService;

	public CheckUploadStatusController() {
		// 初始化相依的服務
		ExchangeTransactionDao exchangeDao = new ExchangeTransactionDao();
		this.persistenceService = new PersistenceService(exchangeDao);
	}

//測試說今天有沒有上傳過檔案了
	@GET
	@Path("/upload-status")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkTodayStatus() {
		try {
			// 使用 PersistenceService 檢查狀態
			UploadStatus status = persistenceService.checkTodayUploadStatus();

			// 記錄日誌
			log.info("成功檢查上傳狀態: hasUploaded={}, lastUploadTime={}", status.isHasUploaded(), status.getLastUploadTime());

			return Response.ok(status).build();

		} catch (SQLException e) {
			log.error("檢查上傳狀態失敗", e);

			ErrorResponse error = new ErrorResponse("檢查上傳狀態失敗: " + e.getMessage());

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
		} catch (Exception e) {
			log.error("發生未預期的錯誤", e);

			ErrorResponse error = new ErrorResponse("系統發生未預期的錯誤");

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
		}
	}

	@DELETE
	@Path("/delete-today")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTodayData() {
		try {
			ExchangeTransactionDao dao = new ExchangeTransactionDao();
			int deletedCount = dao.deleteTodayTransactions();

			return Response.ok(new DeleteResult(true, "成功刪除今日資料", deletedCount)).build();
		} catch (Exception e) {

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new DeleteResult(false, "刪除失敗: " + e.getMessage(), 0)).build();
		}
	}

	// 刪除結果類
	public static class DeleteResult {
		private boolean success;
		private String message;
		private int deletedCount;

		public DeleteResult(boolean success, String message, int deletedCount) {
			this.success = success;
			this.message = message;
			this.deletedCount = deletedCount;
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public int getDeletedCount() {
			return deletedCount;
		}

		public void setDeletedCount(int deletedCount) {
			this.deletedCount = deletedCount;
		}

		// getters and setters
		
	}
}