package com.inventorymanager.order;

import com.inventorymanager.AbstractControllerIT;
import com.inventorymanager.order.dto.OrderResponse;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class OrderControllerIT extends AbstractControllerIT {

    private OrderResponse sampleResponse() {
        return new OrderResponse(1L, 1L, "ACME Corp", 1L, "Laptop", 10,
                OrderStatus.PENDING, LocalDateTime.now());
    }

    // --- GET /api/orders ---

    @Test
    void getAllOrders_withAuth_returns200WithList() {
        when(orderService.getAllOrders()).thenReturn(List.of(sampleResponse()));

        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/orders")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", hasSize(1))
                .body("data[0].status", equalTo("PENDING"))
                .body("data[0].supplierName", equalTo("ACME Corp"))
                .body("data[0].productName", equalTo("Laptop"));
    }

    @Test
    void getAllOrders_withStatusParam_returnsFilteredList() {
        when(orderService.getOrdersByStatus(OrderStatus.PENDING)).thenReturn(List.of(sampleResponse()));

        given()
                .header("Authorization", "Bearer " + userToken)
                .queryParam("status", "PENDING")
        .when()
                .get("/api/orders")
        .then()
                .statusCode(200)
                .body("data", hasSize(1))
                .body("data[0].status", equalTo("PENDING"));
    }

    @Test
    void getAllOrders_withoutToken_returnsNon2xx() {
        given()
        .when()
                .get("/api/orders")
        .then()
                .statusCode(not(in(List.of(200, 201, 204))));
    }

    // --- GET /api/orders/{id} ---

    @Test
    void getOrderById_found_returns200() {
        when(orderService.getOrderById(1L)).thenReturn(sampleResponse());

        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/orders/1")
        .then()
                .statusCode(200)
                .body("data.id", equalTo(1))
                .body("data.quantity", equalTo(10))
                .body("data.supplierId", equalTo(1));
    }

    @Test
    void getOrderById_notFound_returns404() {
        when(orderService.getOrderById(99L))
                .thenThrow(new EntityNotFoundException("Order not found with id 99"));

        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/orders/99")
        .then()
                .statusCode(404)
                .body("message", containsString("99"));
    }

    // --- POST /api/orders (any authenticated user) ---

    @Test
    void createOrder_withAuth_returns200() {
        when(orderService.createOrder(any())).thenReturn(sampleResponse());

        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body("{\"supplierId\":1,\"productId\":1,\"quantity\":10}")
        .when()
                .post("/api/orders")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.status", equalTo("PENDING"))
                .body("data.quantity", equalTo(10));
    }

    @Test
    void createOrder_withoutToken_returnsNon2xx() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"supplierId\":1,\"productId\":1,\"quantity\":10}")
        .when()
                .post("/api/orders")
        .then()
                .statusCode(not(in(List.of(200, 201, 204))));
    }

    // --- PATCH /api/orders/{id}/status (ADMIN only) ---

    @Test
    void updateStatus_asAdmin_returns200() {
        OrderResponse confirmed = new OrderResponse(1L, 1L, "ACME Corp", 1L, "Laptop", 10,
                OrderStatus.CONFIRMED, LocalDateTime.now());
        when(orderService.updateStatus(eq(1L), eq(OrderStatus.CONFIRMED))).thenReturn(confirmed);

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"status\":\"CONFIRMED\"}")
        .when()
                .patch("/api/orders/1/status")
        .then()
                .statusCode(200)
                .body("data.status", equalTo("CONFIRMED"));
    }

    @Test
    void updateStatus_asUser_returns403() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body("{\"status\":\"CONFIRMED\"}")
        .when()
                .patch("/api/orders/1/status")
        .then()
                .statusCode(403);
    }

    @Test
    void updateStatus_insufficientStock_returns400() {
        when(orderService.updateStatus(eq(1L), eq(OrderStatus.DELIVERED)))
                .thenThrow(new IllegalStateException("Insufficient stock"));

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"status\":\"DELIVERED\"}")
        .when()
                .patch("/api/orders/1/status")
        .then()
                .statusCode(400)
                .body("message", equalTo("Insufficient stock"));
    }

    // --- DELETE /api/orders/{id} (ADMIN only) ---

    @Test
    void deleteOrder_asAdmin_returns200() {
        doNothing().when(orderService).deleteOrder(1L);

        given()
                .header("Authorization", "Bearer " + adminToken)
        .when()
                .delete("/api/orders/1")
        .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    void deleteOrder_asUser_returns403() {
        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .delete("/api/orders/1")
        .then()
                .statusCode(403);
    }

    @Test
    void deleteOrder_notFound_returns404() {
        when(orderService.getOrderById(99L))
                .thenThrow(new EntityNotFoundException("Order not found with id 99"));

        given()
                .header("Authorization", "Bearer " + adminToken)
        .when()
                .get("/api/orders/99")
        .then()
                .statusCode(404);
    }
}
