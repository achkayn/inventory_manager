package com.inventorymanager.order;

import java.time.LocalDateTime;
import java.util.List;

import com.inventorymanager.order.dto.OrderRequest;
import com.inventorymanager.order.dto.OrderResponse;
import com.inventorymanager.product.Product;
import com.inventorymanager.product.ProductRepository;
import com.inventorymanager.supplier.Supplier;
import com.inventorymanager.supplier.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final SupplierRepository supplierRepository;
	private final ProductRepository productRepository;

	public OrderServiceImpl(OrderRepository orderRepository, SupplierRepository supplierRepository,
			ProductRepository productRepository) {
		this.orderRepository = orderRepository;
		this.supplierRepository = supplierRepository;
		this.productRepository = productRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponse> getAllOrders() {
		return orderRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
		return orderRepository.findByStatus(status).stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public OrderResponse getOrderById(Long id) {
		return toResponse(findOrder(id));
	}

	@Override
	@Transactional
	public OrderResponse createOrder(OrderRequest request) {
		Supplier supplier = supplierRepository.findById(request.getSupplierId())
				.orElseThrow(() -> new EntityNotFoundException("Supplier not found with id " + request.getSupplierId()));
		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new EntityNotFoundException("Product not found with id " + request.getProductId()));

		Order order = new Order();
		order.setSupplier(supplier);
		order.setProduct(product);
		order.setQuantity(request.getQuantity());
		order.setStatus(OrderStatus.PENDING);
		order.setCreatedAt(LocalDateTime.now());
		return toResponse(orderRepository.save(order));
	}

	@Override
	@Transactional
	public OrderResponse updateStatus(Long id, OrderStatus status) {
		Order order = findOrder(id);
		order.setStatus(status);
		return toResponse(orderRepository.save(order));
	}

	@Override
	@Transactional
	public void deleteOrder(Long id) {
		Order order = findOrder(id);
		orderRepository.delete(order);
	}

	private Order findOrder(Long id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id " + id));
	}

	private OrderResponse toResponse(Order order) {
		return new OrderResponse(
				order.getId(),
				order.getSupplier() != null ? order.getSupplier().getId() : null,
				order.getSupplier() != null ? order.getSupplier().getName() : null,
				order.getProduct() != null ? order.getProduct().getId() : null,
				order.getProduct() != null ? order.getProduct().getName() : null,
				order.getQuantity(),
				order.getStatus(),
				order.getCreatedAt());
	}
}
