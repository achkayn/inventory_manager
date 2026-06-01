package com.inventorymanager.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

	List<Order> findBySupplierId(Long supplierId);

	List<Order> findByStatus(OrderStatus status);

	long countByStatus(OrderStatus status);
}
