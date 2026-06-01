package com.inventorymanager.product;

import java.util.List;

import com.inventorymanager.product.dto.ProductRequest;
import com.inventorymanager.product.dto.ProductResponse;

public interface ProductService {

	List<ProductResponse> getAllProducts();

	ProductResponse getProductById(Long id);

	ProductResponse createProduct(ProductRequest request);

	ProductResponse updateProduct(Long id, ProductRequest request);

	void deleteProduct(Long id);
}
