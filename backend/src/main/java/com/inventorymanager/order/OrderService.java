package com.inventorymanager.order;

import java.util.List;

import com.inventorymanager.order.dto.OrderRequest;
import com.inventorymanager.order.dto.OrderResponse;

public interface OrderService {

	List<OrderResponse> getAllOrders();

	OrderResponse getOrderById(Long id);

	OrderResponse createOrder(OrderRequest request);

	OrderResponse updateStatus(Long id, OrderStatus status);
}
