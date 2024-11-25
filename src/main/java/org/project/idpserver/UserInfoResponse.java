package org.project.idpserver;

public class UserInfoResponse {
    private String username;
    private String email;
    private String role;

    public UserInfoResponse(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}