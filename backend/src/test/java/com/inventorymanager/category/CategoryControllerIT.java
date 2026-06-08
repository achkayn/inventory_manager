package com.inventorymanager.category;

import com.inventorymanager.AbstractControllerIT;
import com.inventorymanager.category.dto.CategoryResponse;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class CategoryControllerIT extends AbstractControllerIT {

    // --- GET /api/categories ---

    @Test
    void getAllCategories_withAuth_returns200WithList() {
        when(categoryService.getAllCategories()).thenReturn(List.of(
                new CategoryResponse(1L, "Electronics", "Gadgets"),
                new CategoryResponse(2L, "Clothing", "Apparel")
        ));

        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/categories")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", hasSize(2))
                .body("data[0].name", equalTo("Electronics"))
                .body("data[1].name", equalTo("Clothing"));
    }

    @Test
    void getAllCategories_withoutToken_returnsNon2xx() {
        given()
        .when()
                .get("/api/categories")
        .then()
                .statusCode(not(in(List.of(200, 201, 204))));
    }

    // --- GET /api/categories/{id} ---

    @Test
    void getCategoryById_found_returns200() {
        when(categoryService.getCategoryById(1L))
                .thenReturn(new CategoryResponse(1L, "Electronics", "Gadgets"));

        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/categories/1")
        .then()
                .statusCode(200)
                .body("data.id", equalTo(1))
                .body("data.name", equalTo("Electronics"))
                .body("data.description", equalTo("Gadgets"));
    }

    @Test
    void getCategoryById_notFound_returns404() {
        when(categoryService.getCategoryById(99L))
                .thenThrow(new EntityNotFoundException("Category not found with id 99"));

        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/categories/99")
        .then()
                .statusCode(404)
                .body("message", containsString("99"));
    }

    // --- POST /api/categories (ADMIN only) ---

    @Test
    void createCategory_asAdmin_returns200() {
        when(categoryService.createCategory(any()))
                .thenReturn(new CategoryResponse(1L, "Electronics", "Gadgets"));

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"name\":\"Electronics\",\"description\":\"Gadgets\"}")
        .when()
                .post("/api/categories")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.id", equalTo(1))
                .body("data.name", equalTo("Electronics"));
    }

    @Test
    void createCategory_asUser_returns403() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body("{\"name\":\"Electronics\",\"description\":\"Gadgets\"}")
        .when()
                .post("/api/categories")
        .then()
                .statusCode(403);
    }

    // --- PUT /api/categories/{id} (ADMIN only) ---

    @Test
    void updateCategory_asAdmin_returns200() {
        when(categoryService.updateCategory(eq(1L), any()))
                .thenReturn(new CategoryResponse(1L, "Updated Name", "Updated Desc"));

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"name\":\"Updated Name\",\"description\":\"Updated Desc\"}")
        .when()
                .put("/api/categories/1")
        .then()
                .statusCode(200)
                .body("data.name", equalTo("Updated Name"))
                .body("data.description", equalTo("Updated Desc"));
    }

    @Test
    void updateCategory_asUser_returns403() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body("{\"name\":\"Electronics\",\"description\":\"Gadgets\"}")
        .when()
                .put("/api/categories/1")
        .then()
                .statusCode(403);
    }

    @Test
    void updateCategory_notFound_returns404() {
        when(categoryService.updateCategory(eq(99L), any()))
                .thenThrow(new EntityNotFoundException("Category not found with id 99"));

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"name\":\"X\",\"description\":\"Y\"}")
        .when()
                .put("/api/categories/99")
        .then()
                .statusCode(404);
    }

    // --- DELETE /api/categories/{id} (ADMIN only) ---

    @Test
    void deleteCategory_asAdmin_returns200() {
        doNothing().when(categoryService).deleteCategory(1L);

        given()
                .header("Authorization", "Bearer " + adminToken)
        .when()
                .delete("/api/categories/1")
        .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    void deleteCategory_asUser_returns403() {
        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .delete("/api/categories/1")
        .then()
                .statusCode(403);
    }
}
