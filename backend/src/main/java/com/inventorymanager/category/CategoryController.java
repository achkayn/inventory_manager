package com.inventorymanager.category;

import java.util.List;

import com.inventorymanager.category.dto.CategoryRequest;
import com.inventorymanager.category.dto.CategoryResponse;
import com.inventorymanager.common.ApiResponse;
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
@RequestMapping("/api/categories")
public class CategoryController {

	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
		List<CategoryResponse> categories = categoryService.getAllCategories();
		return ResponseEntity.ok(new ApiResponse<>(true, "Categories retrieved successfully", categories));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
		CategoryResponse response = categoryService.getCategoryById(id);
		return ResponseEntity.ok(new ApiResponse<>(true, "Category retrieved successfully", response));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@RequestBody CategoryRequest request) {
		CategoryResponse response = categoryService.createCategory(request);
		return ResponseEntity.ok(new ApiResponse<>(true, "Category created successfully", response));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest request) {
		CategoryResponse response = categoryService.updateCategory(id, request);
		return ResponseEntity.ok(new ApiResponse<>(true, "Category updated successfully", response));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
		categoryService.deleteCategory(id);
		return ResponseEntity.ok(new ApiResponse<>(true, "Category deleted successfully", null));
	}
}
