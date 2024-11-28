package org.project.idpserver.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.project.idpserver.annotation.Auditable;
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
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final IdpUserRepository idpUserRepository;
    private final PasswordEncoder passwordEncoder;


    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, IdpUserRepository idpUserRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.idpUserRepository = idpUserRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/login")
    @CrossOrigin("http://localhost:8081")
    @Auditable(action = "LOGIN")
    public void handleLogin(@RequestParam String username, @RequestParam String password, @RequestParam("redirect_uri") String redirectUri, HttpServletResponse response, Model model, HttpServletRequest request) throws ServletException, IOException {


        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // generate JWT
            String jwt = jwtUtil.generateToken(authentication);

            // Set JWT token in to cookie
            Cookie cookie = new Cookie("jwt", jwt);
            cookie.setHttpOnly(true);
            //cookie.setSecure(true); // only for HTTPS ?
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24); // 1 day
            response.addCookie(cookie);


            response.sendRedirect(redirectUri);

        } catch (Exception e) {

            response.setStatus(HttpServletResponse.SC_FOUND);
            response.setHeader("Location", "/api/login?error=Invalid username or password&redirect_uri=" + redirectUri);
        }
    }

    @GetMapping("/userinfo")
    @CrossOrigin("http://localhost:8081")
    @Auditable(action = "GET_USER_INFO")
    public Map<String, Object> getUserInfo(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        Claims claims = jwtUtil.getClaims(token);

        return Map.of(
                "username", claims.getSubject(),
                "email", claims.get("email"),
                "role", claims.get("role")
        );
    }


    @PostMapping("/register")
    @CrossOrigin("http://localhost:8081")
    @Auditable(action = "REGISTER")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequest request) {

        Optional<IdpUser> existingUser = Optional.ofNullable(idpUserRepository.findByUsername(request.getUsername()));
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("username taken");
        }

        Optional<IdpUser> existingEmail = idpUserRepository.findByEmail(request.getEmail());
        if (existingEmail.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("email taken");
        }

        IdpUser user = new IdpUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        //create user
        idpUserRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
}




