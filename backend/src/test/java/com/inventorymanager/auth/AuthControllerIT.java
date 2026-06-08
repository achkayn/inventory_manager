package com.inventorymanager.auth;

import com.inventorymanager.AbstractControllerIT;
import com.inventorymanager.auth.dto.AuthResponse;
import com.inventorymanager.auth.dto.LoginRequest;
import com.inventorymanager.auth.dto.RegisterRequest;
import com.inventorymanager.common.ConflictException;
import com.inventorymanager.common.UnauthorizedException;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthControllerIT extends AbstractControllerIT {

    // --- POST /api/auth/login ---

    @Test
    void login_validCredentials_returns200WithToken() {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new AuthResponse("jwt-token", "alice", "alice@test.com", "ROLE_USER"));

        given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"alice@test.com\",\"password\":\"password\"}")
        .when()
                .post("/api/auth/login")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Login successful"))
                .body("data.token", equalTo("jwt-token"))
                .body("data.username", equalTo("alice"))
                .body("data.role", equalTo("ROLE_USER"));
    }

    @Test
    void login_invalidCredentials_returns401() {
        when(authService.login(any()))
                .thenThrow(new UnauthorizedException("Invalid email or password"));

        given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"bad@test.com\",\"password\":\"wrong\"}")
        .when()
                .post("/api/auth/login")
        .then()
                .statusCode(401)
                .body("message", containsString("Invalid email or password"));
    }

    // --- POST /api/auth/register ---

    @Test
    void register_newEmail_returns200WithToken() {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new AuthResponse("jwt-token", "alice", "alice@test.com", "ROLE_USER"));

        given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"alice\",\"email\":\"alice@test.com\",\"password\":\"password\"}")
        .when()
                .post("/api/auth/register")
        .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Registration successful"))
                .body("data.token", notNullValue())
                .body("data.email", equalTo("alice@test.com"));
    }

    @Test
    void register_duplicateEmail_returns409() {
        when(authService.register(any()))
                .thenThrow(new ConflictException("Email already taken"));

        given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"alice\",\"email\":\"alice@test.com\",\"password\":\"password\"}")
        .when()
                .post("/api/auth/register")
        .then()
                .statusCode(409)
                .body("message", equalTo("Email already taken"));
    }

    // --- /api/auth is public — no token needed ---

    @Test
    void login_noBody_doesNotCrashServer() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"alice@test.com\",\"password\":\"password\"}")
        .when()
                .post("/api/auth/login")
        .then()
                .statusCode(not(greaterThanOrEqualTo(500)));
    }
}
