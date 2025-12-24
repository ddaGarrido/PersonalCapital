package com.percap.dtos.auth;

import com.percap.domain.user.UserRole;

public class LoginResponseDTO {
    private String token;
    private String login;
    private UserRole role;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, String login, UserRole role) {
        this.token = token;
        this.login = login;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
