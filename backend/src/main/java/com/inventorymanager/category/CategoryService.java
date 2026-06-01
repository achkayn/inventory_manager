package com.inventorymanager.category;

import java.util.List;

import com.inventorymanager.category.dto.CategoryRequest;
import com.inventorymanager.category.dto.CategoryResponse;

public interface CategoryService {

	List<CategoryResponse> getAllCategories();

	CategoryResponse getCategoryById(Long id);

	CategoryResponse createCategory(CategoryRequest request);

	CategoryResponse updateCategory(Long id, CategoryRequest request);

	void deleteCategory(Long id);
}
