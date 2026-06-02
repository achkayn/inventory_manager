package com.inventorymanager.auth;

import com.inventorymanager.auth.dto.AuthResponse;
import com.inventorymanager.auth.dto.LoginRequest;
import com.inventorymanager.auth.dto.RegisterRequest;
import com.inventorymanager.auth.dto.UserResponse;
import java.util.List;

public interface AuthService {

	AuthResponse login(LoginRequest request);

	AuthResponse register(RegisterRequest request);

	List<UserResponse> getAllUsers();

	UserResponse updateUserRole(Long id, String role);
}
