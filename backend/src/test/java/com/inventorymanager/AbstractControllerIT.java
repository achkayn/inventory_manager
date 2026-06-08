package com.inventorymanager;

import com.inventorymanager.auth.AuthService;
import com.inventorymanager.auth.JwtUtil;
import com.inventorymanager.category.CategoryService;
import com.inventorymanager.dashboard.DashboardService;
import com.inventorymanager.order.OrderService;
import com.inventorymanager.product.ProductService;
import com.inventorymanager.supplier.SupplierService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    protected JwtUtil jwtUtil;

    @MockBean
    protected AuthService authService;

    @MockBean
    protected CategoryService categoryService;

    @MockBean
    protected ProductService productService;

    @MockBean
    protected SupplierService supplierService;

    @MockBean
    protected OrderService orderService;

    @MockBean
    protected DashboardService dashboardService;

    protected String userToken;
    protected String adminToken;

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        userToken = jwtUtil.generateToken("user@test.com", "ROLE_USER");
        adminToken = jwtUtil.generateToken("admin@test.com", "ROLE_ADMIN");
    }
}
