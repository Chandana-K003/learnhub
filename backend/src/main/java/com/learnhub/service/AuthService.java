package com.learnhub.service;

import com.learnhub.dto.AuthDtos.AuthResponse;
import com.learnhub.dto.AuthDtos.LoginRequest;
import com.learnhub.dto.AuthDtos.RegisterRequest;
import com.learnhub.dto.AuthDtos.UserDto;
import com.learnhub.exception.ApiException;
import com.learnhub.model.Role;
import com.learnhub.model.User;
import com.learnhub.repository.UserRepository;
import com.learnhub.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "An account with this email already exists");
        }
        Role role = request.role() == null ? Role.STUDENT : request.role();
        User user = new User(request.fullName().trim(), email, passwordEncoder.encode(request.password()), role);
        userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(user), UserDto.from(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return new AuthResponse(jwtService.generateToken(user), UserDto.from(user));
    }
}
