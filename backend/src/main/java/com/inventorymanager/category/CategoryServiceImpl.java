package com.inventorymanager.category;

import java.util.List;

import com.inventorymanager.category.dto.CategoryRequest;
import com.inventorymanager.category.dto.CategoryResponse;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Override
	public List<CategoryResponse> getAllCategories() {
		return null;
	}

	@Override
	public CategoryResponse getCategoryById(Long id) {
		return null;
	}

	@Override
	public CategoryResponse createCategory(CategoryRequest request) {
		return null;
	}

	@Override
	public CategoryResponse updateCategory(Long id, CategoryRequest request) {
		return null;
	}

	@Override
	public void deleteCategory(Long id) {
	}
}
