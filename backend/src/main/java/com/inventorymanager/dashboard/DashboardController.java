package com.inventorymanager.dashboard;

import java.util.List;

import com.inventorymanager.common.ApiResponse;
import com.inventorymanager.dashboard.dto.CategoryStockResponse;
import com.inventorymanager.dashboard.dto.DashboardSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

	private final DashboardService dashboardService;

	public DashboardController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	@GetMapping("/summary")
	public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary() {
		DashboardSummaryResponse response = dashboardService.getSummary();
		return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard summary retrieved successfully", response));
	}

	@GetMapping("/category-stock")
	public ResponseEntity<ApiResponse<List<CategoryStockResponse>>> getCategoryStock() {
		List<CategoryStockResponse> response = dashboardService.getCategoryStock();
		return ResponseEntity.ok(new ApiResponse<>(true, "Category stock retrieved successfully", response));
	}
}
