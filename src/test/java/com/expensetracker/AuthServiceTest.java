package com.expensetracker;

import com.expensetracker.dto.request.AuthRequest;
import com.expensetracker.dto.response.AuthResponse;
import com.expensetracker.entity.User;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_ShouldCreateUserAndReturnToken() {
        AuthRequest.Register request = new AuthRequest.Register();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("Test User");
    }

    @Test
    void register_ShouldThrow_WhenEmailAlreadyExists() {
        AuthRequest.Register request = new AuthRequest.Register();
        request.setName("Test User");
        request.setEmail("duplicate@example.com");
        request.setPassword("password123");

        authService.register(request);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {
        // Register first
        AuthRequest.Register reg = new AuthRequest.Register();
        reg.setName("Login User");
        reg.setEmail("login@example.com");
        reg.setPassword("mypassword");
        authService.register(reg);

        // Now login
        AuthRequest.Login login = new AuthRequest.Login();
        login.setEmail("login@example.com");
        login.setPassword("mypassword");

        AuthResponse response = authService.login(login);

        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getEmail()).isEqualTo("login@example.com");
    }

    @Test
    void register_ShouldHashPassword() {
        AuthRequest.Register request = new AuthRequest.Register();
        request.setName("Hashed User");
        request.setEmail("hashed@example.com");
        request.setPassword("plaintext");
        authService.register(request);

        User stored = userRepository.findByEmail("hashed@example.com").orElseThrow();
        assertThat(stored.getPassword()).isNotEqualTo("plaintext");
        assertThat(stored.getPassword()).startsWith("$2a$");
    }
}
