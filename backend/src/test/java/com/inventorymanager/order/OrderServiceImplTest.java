package com.inventorymanager.order;

import com.inventorymanager.order.dto.OrderRequest;
import com.inventorymanager.order.dto.OrderResponse;
import com.inventorymanager.product.Product;
import com.inventorymanager.product.ProductRepository;
import com.inventorymanager.supplier.Supplier;
import com.inventorymanager.supplier.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Supplier sampleSupplier() {
        return new Supplier(1L, "ACME Corp", "acme@example.com", "555-1234", "123 Main St");
    }

    private Product sampleProduct() {
        Product p = new Product();
        p.setId(1L);
        p.setName("Laptop");
        p.setPrice(BigDecimal.valueOf(1200));
        p.setStockQty(100);
        p.setLowStockThreshold(10);
        return p;
    }

    private Order sampleOrder() {
        Order o = new Order();
        o.setId(1L);
        o.setSupplier(sampleSupplier());
        o.setProduct(sampleProduct());
        o.setQuantity(10);
        o.setStatus(OrderStatus.PENDING);
        o.setCreatedAt(LocalDateTime.now());
        return o;
    }

    // --- getAllOrders ---

    @Test
    void getAllOrders_returnsMappedList() {
        when(orderRepository.findAll()).thenReturn(List.of(sampleOrder()));

        List<OrderResponse> result = orderService.getAllOrders();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.get(0).getSupplierName()).isEqualTo("ACME Corp");
        assertThat(result.get(0).getProductName()).isEqualTo("Laptop");
    }

    @Test
    void getAllOrders_emptyRepository_returnsEmptyList() {
        when(orderRepository.findAll()).thenReturn(List.of());

        assertThat(orderService.getAllOrders()).isEmpty();
    }

    // --- getOrdersByStatus ---

    @Test
    void getOrdersByStatus_returnsMappedList() {
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(List.of(sampleOrder()));

        List<OrderResponse> result = orderService.getOrdersByStatus(OrderStatus.PENDING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void getOrdersByStatus_noMatch_returnsEmptyList() {
        when(orderRepository.findByStatus(OrderStatus.DELIVERED)).thenReturn(List.of());

        assertThat(orderService.getOrdersByStatus(OrderStatus.DELIVERED)).isEmpty();
    }

    // --- getOrderById ---

    @Test
    void getOrderById_found_returnsResponse() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder()));

        OrderResponse result = orderService.getOrderById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getProductName()).isEqualTo("Laptop");
        assertThat(result.getSupplierId()).isEqualTo(1L);
    }

    @Test
    void getOrderById_notFound_throwsEntityNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- createOrder ---

    @Test
    void createOrder_success_savesOrderWithPendingStatus() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(sampleSupplier()));
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct()));
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder());

        OrderResponse result = orderService.createOrder(new OrderRequest(1L, 1L, 10));

        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        verify(orderRepository).save(argThat(o -> o.getStatus() == OrderStatus.PENDING && o.getQuantity() == 10));
    }

    @Test
    void createOrder_supplierNotFound_throwsEntityNotFoundException() {
        when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(new OrderRequest(99L, 1L, 10)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_productNotFound_throwsEntityNotFoundException() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(sampleSupplier()));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(new OrderRequest(1L, 99L, 10)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(orderRepository, never()).save(any());
    }

    // --- updateStatus ---

    @Test
    void updateStatus_toPending_doesNotChangeStock() {
        Order order = sampleOrder();
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderService.updateStatus(1L, OrderStatus.PENDING);

        verify(productRepository, never()).save(any());
    }

    @Test
    void updateStatus_toConfirmed_doesNotChangeStock() {
        Order order = sampleOrder();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderService.updateStatus(1L, OrderStatus.CONFIRMED);

        verify(productRepository, never()).save(any());
    }

    @Test
    void updateStatus_toDelivered_decrementsProductStockQty() {
        Product product = sampleProduct();
        product.setStockQty(100);
        Order order = sampleOrder();
        order.setProduct(product);
        order.setQuantity(30);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.save(product)).thenReturn(product);
        when(orderRepository.save(order)).thenReturn(order);

        orderService.updateStatus(1L, OrderStatus.DELIVERED);

        assertThat(product.getStockQty()).isEqualTo(70);
        verify(productRepository).save(product);
    }

    @Test
    void updateStatus_toDelivered_exactStock_decrementsToZero() {
        Product product = sampleProduct();
        product.setStockQty(10);
        Order order = sampleOrder();
        order.setProduct(product);
        order.setQuantity(10);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.save(product)).thenReturn(product);
        when(orderRepository.save(order)).thenReturn(order);

        orderService.updateStatus(1L, OrderStatus.DELIVERED);

        assertThat(product.getStockQty()).isEqualTo(0);
    }

    @Test
    void updateStatus_toDelivered_insufficientStock_throwsIllegalStateException() {
        Product product = sampleProduct();
        product.setStockQty(5);
        Order order = sampleOrder();
        order.setProduct(product);
        order.setQuantity(10);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateStatus(1L, OrderStatus.DELIVERED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");

        verify(productRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    // --- deleteOrder ---

    @Test
    void deleteOrder_found_deletesSuccessfully() {
        Order order = sampleOrder();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(1L);

        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrder_notFound_throwsEntityNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.deleteOrder(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- toResponse: null-safe supplier/product mapping ---

    @Test
    void toResponse_nullSupplierAndProduct_returnsNullNameFields() {
        Order order = sampleOrder();
        order.setSupplier(null);
        order.setProduct(null);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse result = orderService.getOrderById(1L);

        assertThat(result.getSupplierId()).isNull();
        assertThat(result.getSupplierName()).isNull();
        assertThat(result.getProductId()).isNull();
        assertThat(result.getProductName()).isNull();
    }
}
