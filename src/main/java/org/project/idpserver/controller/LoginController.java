package org.project.idpserver.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.project.idpserver.annotation.Auditable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import jakarta.servlet.http.Cookie;


@Controller
@RequestMapping("/api")
public class LoginController {

    @GetMapping("/login")
    @CrossOrigin("http://localhost:8081")
    @Auditable(action = "SERVE_LOGIN_PAGE")
    public String login(Model model, @RequestParam(value = "redirect_uri") String redirectUri) {

        model.addAttribute("redirectUri", redirectUri);
        return "login";
    }

    @PostMapping("/logout")
    @Auditable(action = "LOGOUT")
    public ResponseEntity<?> handleLogout(HttpSession session) {

        session.invalidate();
        return ResponseEntity.ok("User logged out successfully");
    }
}
