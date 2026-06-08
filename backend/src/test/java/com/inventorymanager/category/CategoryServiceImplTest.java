package com.inventorymanager.category;

import com.inventorymanager.category.dto.CategoryRequest;
import com.inventorymanager.category.dto.CategoryResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    // --- getAllCategories ---

    @Test
    void getAllCategories_returnsMappedList() {
        when(categoryRepository.findAll()).thenReturn(List.of(
                new Category(1L, "Electronics", "Gadgets"),
                new Category(2L, "Clothing", "Apparel")
        ));

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Electronics");
        assertThat(result.get(1).getName()).isEqualTo("Clothing");
    }

    @Test
    void getAllCategories_emptyRepository_returnsEmptyList() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        assertThat(categoryService.getAllCategories()).isEmpty();
    }

    // --- getCategoryById ---

    @Test
    void getCategoryById_found_returnsResponse() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category(1L, "Electronics", "Gadgets")));

        CategoryResponse result = categoryService.getCategoryById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Electronics");
        assertThat(result.getDescription()).isEqualTo("Gadgets");
    }

    @Test
    void getCategoryById_notFound_throwsEntityNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- createCategory ---

    @Test
    void createCategory_savesAndReturnsResponse() {
        Category saved = new Category(1L, "Electronics", "Gadgets");
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryResponse result = categoryService.createCategory(new CategoryRequest("Electronics", "Gadgets"));

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Electronics");
        verify(categoryRepository).save(any(Category.class));
    }

    // --- updateCategory ---

    @Test
    void updateCategory_found_updatesAndReturnsResponse() {
        Category existing = new Category(1L, "OldName", "OldDesc");
        Category updated = new Category(1L, "NewName", "NewDesc");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(updated);

        CategoryResponse result = categoryService.updateCategory(1L, new CategoryRequest("NewName", "NewDesc"));

        assertThat(result.getName()).isEqualTo("NewName");
        assertThat(result.getDescription()).isEqualTo("NewDesc");
        verify(categoryRepository).save(existing);
    }

    @Test
    void updateCategory_notFound_throwsEntityNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateCategory(99L, new CategoryRequest("X", "Y")))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- deleteCategory ---

    @Test
    void deleteCategory_found_deletesSuccessfully() {
        Category category = new Category(1L, "Electronics", "Gadgets");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_notFound_throwsEntityNotFoundException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }
}
