package org.project.idpserver.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.idpserver.dto.RegistrationRequest;
import org.project.idpserver.entity.IdpUser;
import org.project.idpserver.repository.IdpUserRepository;
import org.project.idpserver.utility.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    private AuthController authController;

    @Mock
    private IdpUserRepository idpUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Model model;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authController = new AuthController(authenticationManager, jwtUtil, idpUserRepository, passwordEncoder);
    }

    @Test
    void handleLoginSuccessful() throws ServletException, IOException {

        String username = "testuser";
        String password = "password";
        String redirectUri = "http://localhost:8081/secured";
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtil.generateToken(authentication)).thenReturn("mock");



        authController.handleLogin(username, password, redirectUri, response, model, request);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCaptor.capture());
        verify(response).sendRedirect(redirectUri);


        Cookie jwtCookie = cookieCaptor.getValue();
        assertEquals("jwt", jwtCookie.getName());
        assertEquals("mock", jwtCookie.getValue());
        assertTrue(jwtCookie.isHttpOnly());
        assertEquals("/", jwtCookie.getPath());
        assertEquals(60 * 60 * 24, jwtCookie.getMaxAge());
    }

    @Test
    void getUserInfo_ValidToken() {
        String token = "mock";
        String authHeader = "Bearer " + token;
        when(request.getHeader("Authorization")).thenReturn(authHeader);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("testuser");
        when(claims.get("email")).thenReturn("testuser@example.com");
        when(claims.get("role")).thenReturn("USER");
        when(jwtUtil.getClaims(token)).thenReturn(claims);

        Map<String, Object> userInfo = authController.getUserInfo(request);

        assertNotNull(userInfo);
        assertEquals("testuser", userInfo.get("username"));
        assertEquals("testuser@example.com", userInfo.get("email"));
        assertEquals("USER", userInfo.get("role"));
    }

    @Test
    void registerUserSuccessful() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("testuser");
        request.setEmail("testuser@example.com");
        request.setPassword("password");

        when(idpUserRepository.findByUsername("testuser")).thenReturn(null);
        when(idpUserRepository.findByEmail("testuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        ResponseEntity<String> response = authController.registerUser(request);

        ArgumentCaptor<IdpUser> captor = ArgumentCaptor.forClass(IdpUser.class);
        verify(idpUserRepository).save(captor.capture());

        IdpUser savedUser = captor.getValue();
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("testuser@example.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User registered successfully", response.getBody());
    }

}
