package com.inventorymanager.auth;

import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

	public String generateToken(String email, String role) {
		return null;
	}

	public boolean validateToken(String token) {
		return false;
	}

	public String extractEmail(String token) {
		return null;
	}
}
