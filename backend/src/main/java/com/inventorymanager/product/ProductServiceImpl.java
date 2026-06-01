package com.inventorymanager.product;

import java.util.List;

import com.inventorymanager.product.dto.ProductRequest;
import com.inventorymanager.product.dto.ProductResponse;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

	@Override
	public List<ProductResponse> getAllProducts() {
		return null;
	}

	@Override
	public ProductResponse getProductById(Long id) {
		return null;
	}

	@Override
	public ProductResponse createProduct(ProductRequest request) {
		return null;
	}

	@Override
	public ProductResponse updateProduct(Long id, ProductRequest request) {
		return null;
	}

	@Override
	public void deleteProduct(Long id) {
	}
}
