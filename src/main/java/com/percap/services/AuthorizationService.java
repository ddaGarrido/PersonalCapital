package com.percap.services;

import com.percap.domain.user.User;
import com.percap.domain.user.UserRole;
import com.percap.dtos.auth.LoginResponseDTO;
import com.percap.dtos.auth.RegisterDTO;
import com.percap.infra.security.TokenService;
import com.percap.repositories.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorizationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthorizationService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public LoginResponseDTO authenticate(String login, String password) {
        Optional<User> userOptional = userRepository.findByLogin(login);
        
        if (userOptional.isEmpty()) {
            throw new BadCredentialsException("Invalid login or password");
        }

        User user = userOptional.get();
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid login or password");
        }

        String token = tokenService.generateToken(user);
        return new LoginResponseDTO(token, user.getLogin(), user.getRole());
    }

    public LoginResponseDTO register(RegisterDTO registerDTO) {
        if (userRepository.findByLogin(registerDTO.getLogin()).isPresent()) {
            throw new IllegalArgumentException("User with login " + registerDTO.getLogin() + " already exists");
        }

        User user = new User();
        user.setLogin(registerDTO.getLogin());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole(registerDTO.getRole() != null ? registerDTO.getRole() : UserRole.USER);

        User savedUser = userRepository.save(user);
        String token = tokenService.generateToken(savedUser);
        
        return new LoginResponseDTO(token, savedUser.getLogin(), savedUser.getRole());
    }
}
