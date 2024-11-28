package org.project.idpserver.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class LoginControllerTest {

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    private LoginController loginController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginController = new LoginController();
    }

    @Test
    void login() {
        String redirectUri = "http://localhost:8081/home";

        String result = loginController.login(model, redirectUri, "");

        verify(model).addAttribute("redirectUri", redirectUri);
        assertEquals("login", result);
    }

    @Test
    void handleLogout() {
        ResponseEntity<?> response = loginController.handleLogout(session);

        verify(session).invalidate();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User logged out successfully", response.getBody());
    }

}