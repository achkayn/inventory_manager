package com.inventorymanager.auth;

import com.inventorymanager.AbstractControllerIT;
import com.inventorymanager.auth.dto.UserResponse;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class UserControllerIT extends AbstractControllerIT {

    // --- GET /api/users (ADMIN only) ---

    @Test
    void getAllUsers_asAdmin_returns200WithList() {
        when(authService.getAllUsers()).thenReturn(List.of(
                new UserResponse(1L, "alice", "alice@test.com", "ROLE_USER"),
                new UserResponse(2L, "bob", "bob@test.com", "ROLE_ADMIN")
        ));

        given()
                .header("Authorization", "Bearer " + adminToken)
        .when()
                .get("/api/users")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", hasSize(2))
                .body("data[0].username", equalTo("alice"))
                .body("data[1].role", equalTo("ROLE_ADMIN"));
    }

    @Test
    void getAllUsers_asUser_returns403() {
        given()
                .header("Authorization", "Bearer " + userToken)
        .when()
                .get("/api/users")
        .then()
                .statusCode(403);
    }

    @Test
    void getAllUsers_withoutToken_returnsNon2xx() {
        given()
        .when()
                .get("/api/users")
        .then()
                .statusCode(not(in(List.of(200, 201, 204))));
    }

    // --- PATCH /api/users/{id}/role (ADMIN only) ---

    @Test
    void updateUserRole_asAdmin_returns200WithUpdatedRole() {
        when(authService.updateUserRole(eq(1L), any()))
                .thenReturn(new UserResponse(1L, "alice", "alice@test.com", "ROLE_ADMIN"));

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"role\":\"ROLE_ADMIN\"}")
        .when()
                .patch("/api/users/1/role")
        .then()
                .statusCode(200)
                .body("data.role", equalTo("ROLE_ADMIN"))
                .body("data.username", equalTo("alice"));
    }

    @Test
    void updateUserRole_asUser_returns403() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .contentType(ContentType.JSON)
                .body("{\"role\":\"ROLE_ADMIN\"}")
        .when()
                .patch("/api/users/1/role")
        .then()
                .statusCode(403);
    }

    @Test
    void updateUserRole_userNotFound_returns404() {
        when(authService.updateUserRole(eq(99L), any()))
                .thenThrow(new EntityNotFoundException("User not found with id 99"));

        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType(ContentType.JSON)
                .body("{\"role\":\"ROLE_ADMIN\"}")
        .when()
                .patch("/api/users/99/role")
        .then()
                .statusCode(404)
                .body("message", containsString("99"));
    }
}
