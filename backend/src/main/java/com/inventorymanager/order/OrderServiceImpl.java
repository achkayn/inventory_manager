package com.inventorymanager.order;

import java.util.List;

import com.inventorymanager.order.dto.OrderRequest;
import com.inventorymanager.order.dto.OrderResponse;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

	@Override
	public List<OrderResponse> getAllOrders() {
		return null;
	}

	@Override
	public OrderResponse getOrderById(Long id) {
		return null;
	}

	@Override
	public OrderResponse createOrder(OrderRequest request) {
		return null;
	}

	@Override
	public OrderResponse updateStatus(Long id, OrderStatus status) {
		return null;
	}
}
