package com.inventorymanager.auth;

import com.inventorymanager.auth.dto.UpdateRoleRequest;
import com.inventorymanager.auth.dto.UserResponse;
import com.inventorymanager.common.ApiResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final AuthService authService;

	public UserController(AuthService authService) {
		this.authService = authService;
	}

	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
		List<UserResponse> users = authService.getAllUsers();
		return ResponseEntity.ok(new ApiResponse<>(true, "Users fetched successfully", users));
	}

	@PatchMapping("/{id}/role")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
			@PathVariable Long id,
			@RequestBody UpdateRoleRequest request) {
		UserResponse updated = authService.updateUserRole(id, request.getRole());
		return ResponseEntity.ok(new ApiResponse<>(true, "Role updated successfully", updated));
	}
}
