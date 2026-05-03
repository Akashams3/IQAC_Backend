package com.iqac.project.service;

import com.iqac.project.config.JwtUtil;
import com.iqac.project.dto.ChangePasswordRequest;
import com.iqac.project.dto.LoginRequest;
import com.iqac.project.dto.LoginResponse;
import com.iqac.project.entity.User;
import com.iqac.project.exception.UnauthorizedException;
import com.iqac.project.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        log.info("Password change request for email={}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new UnauthorizedException("Current password is incorrect");
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed successfully for email={}", email);
    }

    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email={}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new UnauthorizedException("Invalid email or password");

        if (request.getRole() != null && !request.getRole().isBlank()) {
            if (!user.getRole().getRoleName().equalsIgnoreCase(request.getRole()))
                throw new UnauthorizedException("Role does not match");
        }

        String role = user.getRole().getRoleName().toUpperCase(Locale.ROOT);
        Long departmentId = user.getDepartment().getId();
        String token = jwtUtil.generateToken(user.getEmail(), role, departmentId);

        log.info("Login successful for email={}, role={}", user.getEmail(), role);
        return new LoginResponse(token, user.getEmail(), role, user.getDepartment().getDeptName());
    }
}
