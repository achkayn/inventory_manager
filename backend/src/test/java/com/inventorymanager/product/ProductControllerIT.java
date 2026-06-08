package com.inventorymanager.product;

import com.inventorymanager.AbstractControllerIT;
import com.inventorymanager.product.dto.ProductResponse;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class ProductControllerIT extends AbstractControllerIT {

    private ProductResponse sampleResponse() {
        return new ProductResponse(1L, "Laptop", "Gaming laptop",
                BigDecimal.valueOf(1200), 50, 10, 1L, "Electronics", false);
    }

    private static final String PRODUCT_BODY =
            "{\"name\":\"Laptop\",\"description\":\"Gaming laptop\",\"price\":1200," +
            "\"stockQty\":50,\"lowStockThreshold\":10,\"categoryId\":1}";

    // --- GET /api/products ---

    @Test
    void getAllProducts_withAuth_returns200WithList() {
        when(productService.getAllProducts()).thenReturn(List.of(sampleResponse()));

        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/products")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", hasSize(1))
                .body("data[0].name", equalTo("Laptop"))
                .body("data[0].categoryName", equalTo("Electronics"));
    }

    @Test
    void getAllProducts_withoutToken_returnsNon2xx() {
        given()
        .when()
                .get("/api/products")
        .then()
                .statusCode(not(in(List.of(200, 201, 204))));
    }

    // --- GET /api/products/low-stock ---

    @Test
    void getLowStockProducts_defaultThreshold_returns200() {
        ProductResponse lowStock = new ProductResponse(1L, "Laptop", "Gaming laptop",
                BigDecimal.valueOf(1200), 3, 10, 1L, "Electronics", true);
        when(productService.getLowStockProducts(25)).thenReturn(List.of(lowStock));

        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/products/low-stock")
        .then()
                .statusCode(200)
                .body("data[0].isLowStock", equalTo(true))
                .body("data[0].stockQty", equalTo(3));
    }

    @Test
    void getLowStockProducts_customThreshold_returns200() {
        when(productService.getLowStockProducts(5)).thenReturn(List.of());

        given()
                .header("Authorization", "Bearer " + userToken)
                .queryParam("threshold", 5)
        .when()
                .get("/api/products/low-stock")
        .then()
                .statusCode(200)
                .body("data", hasSize(0));
    }

    // --- GET /api/products/{id} ---

    @Test
    void getProductById_found_returns200() {
        when(productService.getProductById(1L)).thenReturn(sampleResponse());

        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/products/1")
        .then()
                .statusCode(200)
                .body("data.id", equalTo(1))
                .body("data.name", equalTo("Laptop"))
                .body("data.categoryId", equalTo(1))
                .body("data.isLowStock", equalTo(false));
    }

    @Test
    void getProductById_notFound_returns404() {
        when(productService.getProductById(99L))
                .thenThrow(new EntityNotFoundException("Product not found with id 99"));

        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/products/99")
        .then()
                .statusCode(404)
                .body("message", containsString("99"));
    }

    // --- POST /api/products (ADMIN only) ---

    @Test
    void createProduct_asAdmin_returns200() {
        when(productService.createProduct(any())).thenReturn(sampleResponse());

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(PRODUCT_BODY)
        .when()
                .post("/api/products")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.name", equalTo("Laptop"))
                .body("data.stockQty", equalTo(50));
    }

    @Test
    void createProduct_asUser_returns403() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(PRODUCT_BODY)
        .when()
                .post("/api/products")
        .then()
                .statusCode(403);
    }

    // --- PUT /api/products/{id} (ADMIN only) ---

    @Test
    void updateProduct_asAdmin_returns200() {
        ProductResponse updated = new ProductResponse(1L, "Updated Laptop", "Gaming",
                BigDecimal.valueOf(1300), 40, 10, 1L, "Electronics", false);
        when(productService.updateProduct(eq(1L), any())).thenReturn(updated);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"name\":\"Updated Laptop\",\"description\":\"Gaming\",\"price\":1300," +
                        "\"stockQty\":40,\"lowStockThreshold\":10,\"categoryId\":1}")
        .when()
                .put("/api/products/1")
        .then()
                .statusCode(200)
                .body("data.name", equalTo("Updated Laptop"))
                .body("data.price", equalTo(1300));
    }

    @Test
    void updateProduct_notFound_returns404() {
        when(productService.updateProduct(eq(99L), any()))
                .thenThrow(new EntityNotFoundException("Product not found with id 99"));

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(PRODUCT_BODY)
        .when()
                .put("/api/products/99")
        .then()
                .statusCode(404);
    }

    // --- DELETE /api/products/{id} (ADMIN only) ---

    @Test
    void deleteProduct_asAdmin_returns200() {
        doNothing().when(productService).deleteProduct(1L);

        given()
                .header("Authorization", "Bearer " + adminToken)
        .when()
                .delete("/api/products/1")
        .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    void deleteProduct_asUser_returns403() {
        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .delete("/api/products/1")
        .then()
                .statusCode(403);
    }
}
