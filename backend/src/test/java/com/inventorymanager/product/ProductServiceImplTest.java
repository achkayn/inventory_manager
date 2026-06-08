package com.inventorymanager.product;

import com.inventorymanager.category.Category;
import com.inventorymanager.category.CategoryRepository;
import com.inventorymanager.product.dto.ProductRequest;
import com.inventorymanager.product.dto.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category sampleCategory() {
        return new Category(1L, "Electronics", "Gadgets");
    }

    private Product sampleProduct() {
        Product p = new Product();
        p.setId(1L);
        p.setName("Laptop");
        p.setDescription("Gaming laptop");
        p.setPrice(BigDecimal.valueOf(1200));
        p.setStockQty(50);
        p.setLowStockThreshold(10);
        p.setCategory(sampleCategory());
        return p;
    }

    private ProductRequest sampleRequest() {
        return new ProductRequest("Laptop", "Gaming laptop", BigDecimal.valueOf(1200), 50, 10, 1L);
    }

    // --- getAllProducts ---

    @Test
    void getAllProducts_returnsMappedList() {
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct()));

        List<ProductResponse> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
        assertThat(result.get(0).getCategoryName()).isEqualTo("Electronics");
    }

    @Test
    void getAllProducts_emptyRepository_returnsEmptyList() {
        when(productRepository.findAll()).thenReturn(List.of());

        assertThat(productService.getAllProducts()).isEmpty();
    }

    // --- getProductById ---

    @Test
    void getProductById_found_returnsResponse() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct()));

        ProductResponse result = productService.getProductById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getCategoryId()).isEqualTo(1L);
        assertThat(result.getCategoryName()).isEqualTo("Electronics");
    }

    @Test
    void getProductById_notFound_throwsEntityNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- createProduct ---

    @Test
    void createProduct_success_savesAndReturnsResponse() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory()));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct());

        ProductResponse result = productService.createProduct(sampleRequest());

        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getCategoryId()).isEqualTo(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_categoryNotFound_throwsEntityNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(
                new ProductRequest("X", "desc", BigDecimal.ONE, 10, 5, 99L)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).save(any());
    }

    // --- updateProduct ---

    @Test
    void updateProduct_found_updatesAndReturnsResponse() {
        Product existing = sampleProduct();
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory()));
        when(productRepository.save(existing)).thenReturn(existing);

        ProductResponse result = productService.updateProduct(1L, sampleRequest());

        assertThat(result.getName()).isEqualTo("Laptop");
        verify(productRepository).save(existing);
    }

    @Test
    void updateProduct_notFound_throwsEntityNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(99L, sampleRequest()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- deleteProduct ---

    @Test
    void deleteProduct_found_deletesSuccessfully() {
        Product product = sampleProduct();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_notFound_throwsEntityNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- getLowStockProducts ---

    @Test
    void getLowStockProducts_returnsFilteredList() {
        Product lowStock = sampleProduct();
        lowStock.setStockQty(3);
        when(productRepository.findByStockQtyLessThan(5)).thenReturn(List.of(lowStock));

        List<ProductResponse> result = productService.getLowStockProducts(5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStockQty()).isEqualTo(3);
    }

    // --- toResponse: lowStock flag logic ---

    @Test
    void toResponse_lowStockTrue_whenStockQtyBelowThreshold() {
        Product product = sampleProduct();
        product.setStockQty(5);
        product.setLowStockThreshold(10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThat(productService.getProductById(1L).isLowStock()).isTrue();
    }

    @Test
    void toResponse_lowStockFalse_whenStockQtyAboveThreshold() {
        Product product = sampleProduct();
        product.setStockQty(50);
        product.setLowStockThreshold(10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThat(productService.getProductById(1L).isLowStock()).isFalse();
    }

    @Test
    void toResponse_lowStockFalse_whenStockEqualsThreshold() {
        Product product = sampleProduct();
        product.setStockQty(10);
        product.setLowStockThreshold(10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThat(productService.getProductById(1L).isLowStock()).isFalse();
    }

    @Test
    void toResponse_nullStockQtyAndThreshold_defaultToZero_notLowStock() {
        Product product = sampleProduct();
        product.setStockQty(null);
        product.setLowStockThreshold(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse result = productService.getProductById(1L);

        assertThat(result.isLowStock()).isFalse();
    }

    @Test
    void toResponse_nullCategory_returnsNullCategoryFields() {
        Product product = sampleProduct();
        product.setCategory(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse result = productService.getProductById(1L);

        assertThat(result.getCategoryId()).isNull();
        assertThat(result.getCategoryName()).isNull();
    }
}
