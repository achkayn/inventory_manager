package com.inventorymanager.dashboard;

import java.util.List;

import com.inventorymanager.dashboard.dto.CategoryStockResponse;
import com.inventorymanager.dashboard.dto.DashboardSummaryResponse;

public interface DashboardService {

	DashboardSummaryResponse getSummary();

	List<CategoryStockResponse> getCategoryStock();
}
