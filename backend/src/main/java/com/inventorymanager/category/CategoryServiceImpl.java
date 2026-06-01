package com.inventorymanager.category;

import java.util.List;

import com.inventorymanager.category.dto.CategoryRequest;
import com.inventorymanager.category.dto.CategoryResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CategoryResponse> getAllCategories() {
		return categoryRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public CategoryResponse getCategoryById(Long id) {
		return toResponse(findCategory(id));
	}

	@Override
	@Transactional
	public CategoryResponse createCategory(CategoryRequest request) {
		Category category = new Category();
		category.setName(request.getName());
		category.setDescription(request.getDescription());
		return toResponse(categoryRepository.save(category));
	}

	@Override
	@Transactional
	public CategoryResponse updateCategory(Long id, CategoryRequest request) {
		Category category = findCategory(id);
		category.setName(request.getName());
		category.setDescription(request.getDescription());
		return toResponse(categoryRepository.save(category));
	}

	@Override
	@Transactional
	public void deleteCategory(Long id) {
		Category category = findCategory(id);
		categoryRepository.delete(category);
	}

	private Category findCategory(Long id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));
	}

	private CategoryResponse toResponse(Category category) {
		return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
	}
}
