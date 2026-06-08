package com.inventorymanager.dashboard;

import com.inventorymanager.category.Category;
import com.inventorymanager.dashboard.dto.CategoryStockResponse;
import com.inventorymanager.dashboard.dto.DashboardSummaryResponse;
import com.inventorymanager.order.OrderRepository;
import com.inventorymanager.order.OrderStatus;
import com.inventorymanager.product.Product;
import com.inventorymanager.product.ProductRepository;
import com.inventorymanager.supplier.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private Product makeProduct(Integer stockQty, Integer threshold) {
        Product p = new Product();
        p.setStockQty(stockQty);
        p.setLowStockThreshold(threshold);
        return p;
    }

    private Product makeProductWithCategory(Long catId, String catName, Integer stockQty) {
        Product p = new Product();
        p.setStockQty(stockQty);
        p.setLowStockThreshold(5);
        p.setCategory(new Category(catId, catName, "desc"));
        return p;
    }

    // --- getSummary ---

    @Test
    void getSummary_returnsCorrectAggregates() {
        when(productRepository.count()).thenReturn(10L);
        when(productRepository.findAll()).thenReturn(List.of(
                makeProduct(3, 10),   // low stock (3 < 10)
                makeProduct(20, 10)   // not low stock
        ));
        when(orderRepository.countByStatus(OrderStatus.PENDING)).thenReturn(4L);
        when(supplierRepository.count()).thenReturn(5L);

        DashboardSummaryResponse result = dashboardService.getSummary();

        assertThat(result.getTotalProducts()).isEqualTo(10L);
        assertThat(result.getLowStockItems()).isEqualTo(1L);
        assertThat(result.getPendingOrders()).isEqualTo(4L);
        assertThat(result.getTotalSuppliers()).isEqualTo(5L);
    }

    @Test
    void getSummary_allProductsLowStock_countedCorrectly() {
        when(productRepository.count()).thenReturn(3L);
        when(productRepository.findAll()).thenReturn(List.of(
                makeProduct(1, 10),
                makeProduct(2, 10),
                makeProduct(3, 10)
        ));
        when(orderRepository.countByStatus(OrderStatus.PENDING)).thenReturn(0L);
        when(supplierRepository.count()).thenReturn(0L);

        assertThat(dashboardService.getSummary().getLowStockItems()).isEqualTo(3L);
    }

    @Test
    void getSummary_noProductsLowStock_countIsZero() {
        when(productRepository.count()).thenReturn(2L);
        when(productRepository.findAll()).thenReturn(List.of(
                makeProduct(50, 10),
                makeProduct(100, 10)
        ));
        when(orderRepository.countByStatus(OrderStatus.PENDING)).thenReturn(0L);
        when(supplierRepository.count()).thenReturn(0L);

        assertThat(dashboardService.getSummary().getLowStockItems()).isEqualTo(0L);
    }

    @Test
    void getSummary_nullStockQtyAndThreshold_defaultToZero_notCountedAsLowStock() {
        when(productRepository.count()).thenReturn(1L);
        when(productRepository.findAll()).thenReturn(List.of(makeProduct(null, null)));
        when(orderRepository.countByStatus(OrderStatus.PENDING)).thenReturn(0L);
        when(supplierRepository.count()).thenReturn(0L);

        assertThat(dashboardService.getSummary().getLowStockItems()).isEqualTo(0L);
    }

    @Test
    void getSummary_stockAtThreshold_notCountedAsLowStock() {
        when(productRepository.count()).thenReturn(1L);
        when(productRepository.findAll()).thenReturn(List.of(makeProduct(10, 10)));
        when(orderRepository.countByStatus(OrderStatus.PENDING)).thenReturn(0L);
        when(supplierRepository.count()).thenReturn(0L);

        assertThat(dashboardService.getSummary().getLowStockItems()).isEqualTo(0L);
    }

    // --- getCategoryStock ---

    @Test
    void getCategoryStock_groupsProductsByCategory() {
        when(productRepository.findAll()).thenReturn(List.of(
                makeProductWithCategory(1L, "Electronics", 20),
                makeProductWithCategory(2L, "Clothing", 15)
        ));

        List<CategoryStockResponse> result = dashboardService.getCategoryStock();

        assertThat(result).hasSize(2);
        assertThat(result).anyMatch(r -> r.getCategoryId().equals(1L) && r.getStockQty() == 20);
        assertThat(result).anyMatch(r -> r.getCategoryId().equals(2L) && r.getStockQty() == 15);
    }

    @Test
    void getCategoryStock_excludesProductsWithoutCategory() {
        Product withoutCategory = makeProduct(10, 5);
        when(productRepository.findAll()).thenReturn(List.of(
                makeProductWithCategory(1L, "Electronics", 20),
                withoutCategory
        ));

        List<CategoryStockResponse> result = dashboardService.getCategoryStock();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryId()).isEqualTo(1L);
    }

    @Test
    void getCategoryStock_accumulatesStockForSameCategory() {
        when(productRepository.findAll()).thenReturn(List.of(
                makeProductWithCategory(1L, "Electronics", 30),
                makeProductWithCategory(1L, "Electronics", 20)
        ));

        List<CategoryStockResponse> result = dashboardService.getCategoryStock();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategoryId()).isEqualTo(1L);
        assertThat(result.get(0).getStockQty()).isEqualTo(50);
    }

    @Test
    void getCategoryStock_emptyRepository_returnsEmptyList() {
        when(productRepository.findAll()).thenReturn(List.of());

        assertThat(dashboardService.getCategoryStock()).isEmpty();
    }

    @Test
    void getCategoryStock_nullStockQty_defaultsToZeroForAccumulation() {
        when(productRepository.findAll()).thenReturn(List.of(
                makeProductWithCategory(1L, "Electronics", null),
                makeProductWithCategory(1L, "Electronics", 20)
        ));

        List<CategoryStockResponse> result = dashboardService.getCategoryStock();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStockQty()).isEqualTo(20);
    }
}
