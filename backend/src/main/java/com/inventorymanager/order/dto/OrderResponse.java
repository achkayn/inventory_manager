package com.inventorymanager.order.dto;

import java.time.LocalDateTime;

import com.inventorymanager.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

	private Long id;
	private Long supplierId;
	private String supplierName;
	private Long productId;
	private String productName;
	private Integer quantity;
	private OrderStatus status;
	private LocalDateTime createdAt;
}
