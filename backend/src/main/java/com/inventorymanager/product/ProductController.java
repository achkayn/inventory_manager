package com.inventorymanager.product;

import java.util.List;

import com.inventorymanager.common.ApiResponse;
import com.inventorymanager.product.dto.ProductRequest;
import com.inventorymanager.product.dto.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
		return null;
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
		return null;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody ProductRequest request) {
		return null;
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
		return null;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
		return null;
	}
}
