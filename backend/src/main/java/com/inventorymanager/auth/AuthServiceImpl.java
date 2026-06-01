package com.inventorymanager.auth;

import com.inventorymanager.auth.dto.AuthResponse;
import com.inventorymanager.auth.dto.LoginRequest;
import com.inventorymanager.auth.dto.RegisterRequest;
import com.inventorymanager.common.ConflictException;
import com.inventorymanager.common.UnauthorizedException;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new UnauthorizedException("Invalid email or password");
		}

		String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
		return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole());
	}

	@Override
	public AuthResponse register(RegisterRequest request) {
		userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
			throw new ConflictException("Email already taken");
		});

		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(request.getRole() == null || request.getRole().isBlank()
				? "ROLE_USER"
				: request.getRole().toUpperCase(Locale.ROOT));

		User savedUser = userRepository.save(user);
		String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole());
		return new AuthResponse(token, savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole());
	}
}
