package com.inventorymanager.order;

import java.util.List;

import com.inventorymanager.order.dto.OrderRequest;
import com.inventorymanager.order.dto.OrderResponse;

public interface OrderService {

	List<OrderResponse> getAllOrders();

	List<OrderResponse> getOrdersByStatus(OrderStatus status);

	OrderResponse getOrderById(Long id);

	OrderResponse createOrder(OrderRequest request);

	OrderResponse updateStatus(Long id, OrderStatus status);

	void deleteOrder(Long id);
}
