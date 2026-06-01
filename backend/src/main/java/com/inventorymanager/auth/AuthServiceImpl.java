package com.inventorymanager.auth;

import com.inventorymanager.auth.dto.AuthResponse;
import com.inventorymanager.auth.dto.LoginRequest;
import com.inventorymanager.auth.dto.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

	@Override
	public AuthResponse login(LoginRequest request) {
		return null;
	}

	@Override
	public AuthResponse register(RegisterRequest request) {
		return null;
	}
}
