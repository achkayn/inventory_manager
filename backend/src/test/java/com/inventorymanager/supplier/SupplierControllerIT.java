package com.inventorymanager.supplier;

import com.inventorymanager.AbstractControllerIT;
import com.inventorymanager.common.ConflictException;
import com.inventorymanager.supplier.dto.SupplierResponse;
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

class SupplierControllerIT extends AbstractControllerIT {

    private SupplierResponse sampleResponse() {
        return new SupplierResponse(1L, "ACME Corp", "acme@test.com", "555-1234", "123 Main St");
    }

    private static final String SUPPLIER_BODY =
            "{\"name\":\"ACME Corp\",\"email\":\"acme@test.com\",\"phone\":\"555-1234\",\"address\":\"123 Main St\"}";

    // --- GET /api/suppliers ---

    @Test
    void getAllSuppliers_withAuth_returns200WithList() {
        when(supplierService.getAllSuppliers()).thenReturn(List.of(
                sampleResponse(),
                new SupplierResponse(2L, "Beta Ltd", "beta@test.com", "555-5678", "456 Ave")
        ));

        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/suppliers")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", hasSize(2))
                .body("data[0].name", equalTo("ACME Corp"))
                .body("data[1].email", equalTo("beta@test.com"));
    }

    @Test
    void getAllSuppliers_withoutToken_returnsNon2xx() {
        given()
        .when()
                .get("/api/suppliers")
        .then()
                .statusCode(not(in(List.of(200, 201, 204))));
    }

    // --- GET /api/suppliers/{id} ---

    @Test
    void getSupplierById_found_returns200() {
        when(supplierService.getSupplierById(1L)).thenReturn(sampleResponse());

        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/suppliers/1")
        .then()
                .statusCode(200)
                .body("data.id", equalTo(1))
                .body("data.name", equalTo("ACME Corp"))
                .body("data.email", equalTo("acme@test.com"));
    }

    @Test
    void getSupplierById_notFound_returns404() {
        when(supplierService.getSupplierById(99L))
                .thenThrow(new EntityNotFoundException("Supplier not found with id 99"));

        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/suppliers/99")
        .then()
                .statusCode(404)
                .body("message", containsString("99"));
    }

    // --- POST /api/suppliers (ADMIN only) ---

    @Test
    void createSupplier_asAdmin_returns200() {
        when(supplierService.createSupplier(any())).thenReturn(sampleResponse());

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(SUPPLIER_BODY)
        .when()
                .post("/api/suppliers")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.name", equalTo("ACME Corp"))
                .body("data.phone", equalTo("555-1234"));
    }

    @Test
    void createSupplier_duplicateEmail_returns409() {
        when(supplierService.createSupplier(any()))
                .thenThrow(new ConflictException("Supplier email already exists"));

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(SUPPLIER_BODY)
        .when()
                .post("/api/suppliers")
        .then()
                .statusCode(409)
                .body("message", equalTo("Supplier email already exists"));
    }

    @Test
    void createSupplier_asUser_returns403() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body(SUPPLIER_BODY)
        .when()
                .post("/api/suppliers")
        .then()
                .statusCode(403);
    }

    // --- PUT /api/suppliers/{id} (ADMIN only) ---

    @Test
    void updateSupplier_asAdmin_returns200() {
        SupplierResponse updated = new SupplierResponse(1L, "ACME Updated", "acme@test.com", "555-9999", "999 New St");
        when(supplierService.updateSupplier(eq(1L), any())).thenReturn(updated);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"name\":\"ACME Updated\",\"email\":\"acme@test.com\",\"phone\":\"555-9999\",\"address\":\"999 New St\"}")
        .when()
                .put("/api/suppliers/1")
        .then()
                .statusCode(200)
                .body("data.name", equalTo("ACME Updated"))
                .body("data.phone", equalTo("555-9999"));
    }

    @Test
    void updateSupplier_notFound_returns404() {
        when(supplierService.updateSupplier(eq(99L), any()))
                .thenThrow(new EntityNotFoundException("Supplier not found with id 99"));

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body(SUPPLIER_BODY)
        .when()
                .put("/api/suppliers/99")
        .then()
                .statusCode(404);
    }

    // --- DELETE /api/suppliers/{id} (ADMIN only) ---

    @Test
    void deleteSupplier_asAdmin_returns200() {
        doNothing().when(supplierService).deleteSupplier(1L);

        given()
                .header("Authorization", "Bearer " + adminToken)
        .when()
                .delete("/api/suppliers/1")
        .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    void deleteSupplier_asUser_returns403() {
        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .delete("/api/suppliers/1")
        .then()
                .statusCode(403);
    }
}
