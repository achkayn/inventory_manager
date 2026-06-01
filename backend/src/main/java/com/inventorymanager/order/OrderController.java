package com.inventorymanager.order;

import java.util.List;

import com.inventorymanager.common.ApiResponse;
import com.inventorymanager.order.dto.OrderRequest;
import com.inventorymanager.order.dto.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
		return null;
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
		return null;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody OrderRequest request) {
		return null;
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(@PathVariable Long id, @RequestBody OrderStatus status) {
		return null;
	}
}
