package com.inventorymanager.order;

import java.util.List;

import com.inventorymanager.common.ApiResponse;
import com.inventorymanager.order.OrderStatus;
import com.inventorymanager.order.dto.OrderRequest;
import com.inventorymanager.order.dto.OrderResponse;
import com.inventorymanager.order.dto.OrderStatusRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(@RequestParam(required = false) OrderStatus status) {
		List<OrderResponse> orders = status == null ? orderService.getAllOrders() : orderService.getOrdersByStatus(status);
		return ResponseEntity.ok(new ApiResponse<>(true, "Orders retrieved successfully", orders));
	}

	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
		OrderResponse response = orderService.getOrderById(id);
		return ResponseEntity.ok(new ApiResponse<>(true, "Order retrieved successfully", response));
	}

	@PostMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody OrderRequest request) {
		OrderResponse response = orderService.createOrder(request);
		return ResponseEntity.ok(new ApiResponse<>(true, "Order created successfully", response));
	}

	@PatchMapping("/{id}/status")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(@PathVariable Long id, @RequestBody OrderStatusRequest request) {
		OrderResponse response = orderService.updateStatus(id, request.getStatus());
		return ResponseEntity.ok(new ApiResponse<>(true, "Order status updated successfully", response));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
		orderService.deleteOrder(id);
		return ResponseEntity.ok(new ApiResponse<>(true, "Order deleted successfully", null));
	}
}
