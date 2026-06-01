package com.inventorymanager.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {

	private long totalProducts;
	private long lowStockItems;
	private long pendingOrders;
	private long totalSuppliers;
}
