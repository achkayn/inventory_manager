package com.inventorymanager.product;

import java.util.List;

import com.inventorymanager.common.ApiResponse;
import com.inventorymanager.product.dto.ProductRequest;
import com.inventorymanager.product.dto.ProductResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
		List<ProductResponse> products = productService.getAllProducts();
		return ResponseEntity.ok(new ApiResponse<>(true, "Products retrieved successfully", products));
	}

	@GetMapping("/low-stock")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<List<ProductResponse>>> getLowStockProducts(
			@RequestParam(defaultValue = "25") int threshold) {
		List<ProductResponse> products = productService.getLowStockProducts(threshold);
		return ResponseEntity.ok(new ApiResponse<>(true, "Low stock products retrieved successfully", products));
	}

	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
		ProductResponse response = productService.getProductById(id);
		return ResponseEntity.ok(new ApiResponse<>(true, "Product retrieved successfully", response));
	}

	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody ProductRequest request) {
		ProductResponse response = productService.createProduct(request);
		return ResponseEntity.ok(new ApiResponse<>(true, "Product created successfully", response));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
		ProductResponse response = productService.updateProduct(id, request);
		return ResponseEntity.ok(new ApiResponse<>(true, "Product updated successfully", response));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
		return ResponseEntity.ok(new ApiResponse<>(true, "Product deleted successfully", null));
	}
}
