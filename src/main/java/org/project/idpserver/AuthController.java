package org.project.idpserver;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/login")
    @CrossOrigin("http://localhost:8081")
    public void handleLogin(@RequestParam String username, @RequestParam String password, @RequestParam("redirect_uri") String redirectUri,  HttpServletResponse response) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // generate JWT
        String jwt = jwtUtil.generateToken(authentication);

        // Set JWT as a secure cookie
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        //cookie.setSecure(true); // only for HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24); // 1 day
        response.addCookie(cookie);

        // Redirect to the client
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", redirectUri);
    }

    @GetMapping("/userinfo")
    @CrossOrigin("http://localhost:8081")
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
}




