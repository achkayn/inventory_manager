package com.inventorymanager.auth;

import com.inventorymanager.auth.dto.AuthResponse;
import com.inventorymanager.auth.dto.LoginRequest;
import com.inventorymanager.auth.dto.RegisterRequest;

public interface AuthService {

	AuthResponse login(LoginRequest request);

	AuthResponse register(RegisterRequest request);
}
