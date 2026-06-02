package com.inventorymanager.auth;

import com.inventorymanager.auth.dto.AuthResponse;
import com.inventorymanager.auth.dto.LoginRequest;
import com.inventorymanager.auth.dto.RegisterRequest;
import com.inventorymanager.auth.dto.UserResponse;
import com.inventorymanager.common.ConflictException;
import com.inventorymanager.common.UnauthorizedException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
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

	@Override
	public List<UserResponse> getAllUsers() {
		return userRepository.findAll().stream()
				.map(u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole()))
				.collect(Collectors.toList());
	}

	@Override
	public UserResponse updateUserRole(Long id, String role) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
		user.setRole(role.toUpperCase(Locale.ROOT));
		User saved = userRepository.save(user);
		return new UserResponse(saved.getId(), saved.getUsername(), saved.getEmail(), saved.getRole());
	}
}
