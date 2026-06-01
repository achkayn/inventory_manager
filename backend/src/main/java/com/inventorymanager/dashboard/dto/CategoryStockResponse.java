package com.inventorymanager.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStockResponse {

	private Long categoryId;
	private String categoryName;
	private Integer stockQty;
}
