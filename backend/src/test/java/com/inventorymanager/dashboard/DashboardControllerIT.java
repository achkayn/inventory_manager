package com.inventorymanager.dashboard;

import com.inventorymanager.AbstractControllerIT;
import com.inventorymanager.dashboard.dto.CategoryStockResponse;
import com.inventorymanager.dashboard.dto.DashboardSummaryResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

class DashboardControllerIT extends AbstractControllerIT {

    // --- GET /api/dashboard/summary ---

    @Test
    void getSummary_withAuth_returns200WithAggregates() {
        when(dashboardService.getSummary())
                .thenReturn(new DashboardSummaryResponse(10L, 2L, 5L, 3L));

        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/dashboard/summary")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.totalProducts", equalTo(10))
                .body("data.lowStockItems", equalTo(2))
                .body("data.pendingOrders", equalTo(5))
                .body("data.totalSuppliers", equalTo(3));
    }

    @Test
    void getSummary_withAdminToken_returns200() {
        when(dashboardService.getSummary())
                .thenReturn(new DashboardSummaryResponse(0L, 0L, 0L, 0L));

        given()
                .header("Authorization", "Bearer " + adminToken)
        .when()
                .get("/api/dashboard/summary")
        .then()
                .statusCode(200)
                .body("data.totalProducts", equalTo(0));
    }

    @Test
    void getSummary_withoutToken_returnsNon2xx() {
        given()
        .when()
                .get("/api/dashboard/summary")
        .then()
                .statusCode(not(in(List.of(200, 201, 204))));
    }

    // --- GET /api/dashboard/category-stock ---

    @Test
    void getCategoryStock_withAuth_returns200WithList() {
        when(dashboardService.getCategoryStock()).thenReturn(List.of(
                new CategoryStockResponse(1L, "Electronics", 100),
                new CategoryStockResponse(2L, "Clothing", 50)
        ));

        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/dashboard/category-stock")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", hasSize(2))
                .body("data[0].categoryName", equalTo("Electronics"))
                .body("data[0].stockQty", equalTo(100))
                .body("data[1].categoryName", equalTo("Clothing"));
    }

    @Test
    void getCategoryStock_emptyResult_returns200WithEmptyList() {
        when(dashboardService.getCategoryStock()).thenReturn(List.of());

        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/dashboard/category-stock")
        .then()
                .statusCode(200)
                .body("data", hasSize(0));
    }

    @Test
    void getCategoryStock_withoutToken_returnsNon2xx() {
        given()
        .when()
                .get("/api/dashboard/category-stock")
        .then()
                .statusCode(not(in(List.of(200, 201, 204))));
    }
}
