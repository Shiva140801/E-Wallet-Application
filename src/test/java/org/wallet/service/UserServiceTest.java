package org.wallet.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.wallet.configuration.JwtUtil;
import org.wallet.exceptions.BadRequestException;
import org.wallet.exceptions.ConflictException;
import org.wallet.users.model.LoginRequest;
import org.wallet.users.model.SignupRequest;
import org.wallet.users.model.User;
import org.wallet.users.repo.UserRepository;
import org.wallet.users.service.CustomUserDetailsService;
import org.wallet.users.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtUtil jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private User mockUser;
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        signupRequest = new SignupRequest();
        signupRequest.setUsername("newuser");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setEmail("john.doe@example.com");
        signupRequest.setPhoneNumber("1234567890");
        signupRequest.setPassword("password123");

        mockUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .phoneNumber("9876543210")
                .password("encodedPassword")
                .userType("USER")
                .isActive(true)
                .build();

        mockUserDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("encodedPassword")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();


    }


    @Test
    @DisplayName("Login - Should return JWT token when credentials are valid")
    void login_WithValidCredentials_ShouldReturnToken() {
        String expectedToken = "demo";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(customUserDetailsService.loadUserByUsername(loginRequest.getUsername()))
                .thenReturn(mockUserDetails);
        when(jwtTokenProvider.generateToken(mockUserDetails, List.of("ROLE_USER")))
                .thenReturn(expectedToken);

        String actualToken = userService.login(loginRequest);

        assertNotNull(actualToken);
        assertEquals(expectedToken, actualToken);

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(customUserDetailsService, times(1))
                .loadUserByUsername(loginRequest.getUsername());
        verify(jwtTokenProvider, times(1))
                .generateToken(mockUserDetails, List.of("ROLE_USER"));
    }

    @Test
    @DisplayName("Login - Should throw BadRequestException when credentials are invalid")
    void login_WithInvalidCredentials_ShouldThrowBadRequestException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> userService.login(loginRequest)
        );

        assertEquals("Invalid username or password", exception.getMessage());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtTokenProvider, never()).generateToken(any(), any());
    }


    @Test
    @DisplayName("Signup - Should throw ConflictException when email already exists")
    void signup_WithExistingEmail_ShouldThrowConflictException() {
        when(userRepository.findByUsername(signupRequest.getUsername()))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(signupRequest.getEmail()))
                .thenReturn(Optional.of(mockUser));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userService.signup(signupRequest)
        );

        assertEquals("Email already exists", exception.getMessage());

        verify(userRepository, times(1)).findByUsername(signupRequest.getUsername());
        verify(userRepository, times(1)).findByEmail(signupRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Signup - Should generate UUID for new user")
    void signup_ShouldGenerateUUIDForNewUser() {
        when(userRepository.findByUsername(signupRequest.getUsername()))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(signupRequest.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.signup(signupRequest);

        assertNotNull(createdUser.getId());
        assertInstanceOf(UUID.class, createdUser.getId());
    }
}