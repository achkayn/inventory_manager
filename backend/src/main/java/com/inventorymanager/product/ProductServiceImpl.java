package com.inventorymanager.product;

import java.util.List;

import com.inventorymanager.category.Category;
import com.inventorymanager.category.CategoryRepository;
import com.inventorymanager.product.dto.ProductRequest;
import com.inventorymanager.product.dto.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> getAllProducts() {
		return productRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ProductResponse getProductById(Long id) {
		return toResponse(findProduct(id));
	}

	@Override
	@Transactional
	public ProductResponse createProduct(ProductRequest request) {
		Product product = new Product();
		applyRequest(product, request);
		return toResponse(productRepository.save(product));
	}

	@Override
	@Transactional
	public ProductResponse updateProduct(Long id, ProductRequest request) {
		Product product = findProduct(id);
		applyRequest(product, request);
		return toResponse(productRepository.save(product));
	}

	@Override
	@Transactional
	public void deleteProduct(Long id) {
		Product product = findProduct(id);
		productRepository.delete(product);
	}

	public List<ProductResponse> getLowStockProducts(int threshold) {
		return productRepository.findByStockQtyLessThan(threshold).stream().map(this::toResponse).toList();
	}

	private Product findProduct(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Product not found with id " + id));
	}

	private void applyRequest(Product product, ProductRequest request) {
		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());
		product.setStockQty(request.getStockQty());
		product.setLowStockThreshold(request.getLowStockThreshold());

		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new EntityNotFoundException("Category not found with id " + request.getCategoryId()));
		product.setCategory(category);
	}

	private ProductResponse toResponse(Product product) {
		Long categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
		String categoryName = product.getCategory() != null ? product.getCategory().getName() : null;
		int threshold = product.getLowStockThreshold() == null ? 0 : product.getLowStockThreshold();
		int stockQty = product.getStockQty() == null ? 0 : product.getStockQty();
		boolean lowStock = stockQty < threshold;

		return new ProductResponse(
				product.getId(),
				product.getName(),
				product.getDescription(),
				product.getPrice(),
				product.getStockQty(),
				product.getLowStockThreshold(),
				categoryId,
				categoryName,
				lowStock);
	}
}
