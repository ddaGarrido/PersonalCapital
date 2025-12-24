package com.percap.dtos.auth;

import com.percap.domain.user.UserRole;
import jakarta.validation.constraints.NotBlank;

public class RegisterDTO {
    @NotBlank(message = "Login is required")
    private String login;

    @NotBlank(message = "Password is required")
    private String password;

    private UserRole role = UserRole.USER;

    public RegisterDTO() {
    }

    public RegisterDTO(String login, String password, UserRole role) {
        this.login = login;
        this.password = password;
        this.role = role != null ? role : UserRole.USER;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role != null ? role : UserRole.USER;
    }
}
