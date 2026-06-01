package com.inventorymanager.product.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

	private Long id;
	private String name;
	private String description;
	private BigDecimal price;
	private Integer stockQty;
	private Integer lowStockThreshold;
	private Long categoryId;
	private String categoryName;
	private boolean lowStock;
}
