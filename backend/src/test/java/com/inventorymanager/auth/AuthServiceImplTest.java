package com.inventorymanager.auth;

import com.inventorymanager.auth.dto.AuthResponse;
import com.inventorymanager.auth.dto.LoginRequest;
import com.inventorymanager.auth.dto.RegisterRequest;
import com.inventorymanager.auth.dto.UserResponse;
import com.inventorymanager.common.ConflictException;
import com.inventorymanager.common.UnauthorizedException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    // --- login ---

    @Test
    void login_success_returnsAuthResponse() {
        User user = new User(1L, "alice", "alice@example.com", "encodedPwd", "ROLE_USER");
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPwd", "encodedPwd")).thenReturn(true);
        when(jwtUtil.generateToken("alice@example.com", "ROLE_USER")).thenReturn("jwt-token");

        AuthResponse response = authService.login(new LoginRequest("alice@example.com", "rawPwd"));

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUsername()).isEqualTo("alice");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
        assertThat(response.getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    void login_emailNotFound_throwsUnauthorizedException() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("unknown@example.com", "pwd")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void login_wrongPassword_throwsUnauthorizedException() {
        User user = new User(1L, "alice", "alice@example.com", "encodedPwd", "ROLE_USER");
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPwd", "encodedPwd")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("alice@example.com", "wrongPwd")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid email or password");
    }

    // --- register ---

    @Test
    void register_nullRole_defaultsToRoleUser() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pwd")).thenReturn("encodedPwd");
        User savedUser = new User(1L, "alice", "alice@example.com", "encodedPwd", "ROLE_USER");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken("alice@example.com", "ROLE_USER")).thenReturn("token");

        AuthResponse response = authService.register(new RegisterRequest("alice", "alice@example.com", "pwd", null));

        assertThat(response.getRole()).isEqualTo("ROLE_USER");
        verify(userRepository).save(argThat(u -> "ROLE_USER".equals(u.getRole())));
    }

    @Test
    void register_blankRole_defaultsToRoleUser() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pwd")).thenReturn("encodedPwd");
        User savedUser = new User(1L, "alice", "alice@example.com", "encodedPwd", "ROLE_USER");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken("alice@example.com", "ROLE_USER")).thenReturn("token");

        AuthResponse response = authService.register(new RegisterRequest("alice", "alice@example.com", "pwd", "  "));

        assertThat(response.getRole()).isEqualTo("ROLE_USER");
        verify(userRepository).save(argThat(u -> "ROLE_USER".equals(u.getRole())));
    }

    @Test
    void register_providedRole_storedAsUppercase() {
        when(userRepository.findByEmail("bob@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pwd")).thenReturn("encodedPwd");
        User savedUser = new User(2L, "bob", "bob@example.com", "encodedPwd", "ROLE_ADMIN");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken("bob@example.com", "ROLE_ADMIN")).thenReturn("token");

        authService.register(new RegisterRequest("bob", "bob@example.com", "pwd", "role_admin"));

        verify(userRepository).save(argThat(u -> "ROLE_ADMIN".equals(u.getRole())));
    }

    @Test
    void register_emailAlreadyTaken_throwsConflictException() {
        User existing = new User(1L, "alice", "alice@example.com", "pwd", "ROLE_USER");
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> authService.register(new RegisterRequest("alice", "alice@example.com", "pwd", null)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email already taken");
    }

    // --- getAllUsers ---

    @Test
    void getAllUsers_returnsMappedList() {
        when(userRepository.findAll()).thenReturn(List.of(
                new User(1L, "alice", "alice@example.com", "pwd", "ROLE_USER"),
                new User(2L, "bob", "bob@example.com", "pwd", "ROLE_ADMIN")
        ));

        List<UserResponse> result = authService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsername()).isEqualTo("alice");
        assertThat(result.get(1).getRole()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void getAllUsers_emptyRepository_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        assertThat(authService.getAllUsers()).isEmpty();
    }

    // --- updateUserRole ---

    @Test
    void updateUserRole_success_roleStoredAsUppercase() {
        User user = new User(1L, "alice", "alice@example.com", "pwd", "ROLE_USER");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(new User(1L, "alice", "alice@example.com", "pwd", "ROLE_ADMIN"));

        UserResponse response = authService.updateUserRole(1L, "role_admin");

        assertThat(response.getRole()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void updateUserRole_notFound_throwsEntityNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.updateUserRole(99L, "ROLE_ADMIN"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }
}
