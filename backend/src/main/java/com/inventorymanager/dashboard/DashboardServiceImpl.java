package com.inventorymanager.dashboard;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.inventorymanager.dashboard.dto.CategoryStockResponse;
import com.inventorymanager.dashboard.dto.DashboardSummaryResponse;
import com.inventorymanager.order.OrderRepository;
import com.inventorymanager.order.OrderStatus;
import com.inventorymanager.product.ProductRepository;
import com.inventorymanager.supplier.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardServiceImpl implements DashboardService {

	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final SupplierRepository supplierRepository;

	public DashboardServiceImpl(ProductRepository productRepository, OrderRepository orderRepository,
			SupplierRepository supplierRepository) {
		this.productRepository = productRepository;
		this.orderRepository = orderRepository;
		this.supplierRepository = supplierRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public DashboardSummaryResponse getSummary() {
		long totalProducts = productRepository.count();
		long lowStockItems = productRepository.findAll().stream()
				.filter(product -> {
					int stockQty = product.getStockQty() == null ? 0 : product.getStockQty();
					int threshold = product.getLowStockThreshold() == null ? 0 : product.getLowStockThreshold();
					return stockQty < threshold;
				})
				.count();
		long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
		long totalSuppliers = supplierRepository.count();
		return new DashboardSummaryResponse(totalProducts, lowStockItems, pendingOrders, totalSuppliers);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CategoryStockResponse> getCategoryStock() {
		Map<Long, CategoryStockResponse> grouped = productRepository.findAll().stream()
				.filter(product -> product.getCategory() != null)
				.collect(Collectors.toMap(
						product -> product.getCategory().getId(),
						product -> new CategoryStockResponse(
								product.getCategory().getId(),
								product.getCategory().getName(),
								product.getStockQty() == null ? 0 : product.getStockQty()),
						(existing, incoming) -> {
							existing.setStockQty(existing.getStockQty() + incoming.getStockQty());
							return existing;
						}));
		return grouped.values().stream().toList();
	}
}
