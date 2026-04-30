package com.iqac.project.service;

import com.iqac.project.config.JwtUtil;
import com.iqac.project.dto.ChangePasswordRequest;
import com.iqac.project.dto.LoginRequest;
import com.iqac.project.dto.LoginResponse;
import com.iqac.project.entity.User;
import com.iqac.project.exception.UnauthorizedException;
import com.iqac.project.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()))
            throw new UnauthorizedException("Current password is incorrect");

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new UnauthorizedException("Invalid email or password");

        String userDept = user.getDepartment().getDeptName();
        if (request.getRole() != null && !request.getRole().isBlank()) {
            if (!user.getRole().getRoleName().equalsIgnoreCase(request.getRole()))
                throw new UnauthorizedException("Role does not match");
        }

        String role = user.getRole().getRoleName().toUpperCase();
        Long departmentId = user.getDepartment().getId();
        String token = jwtUtil.generateToken(user.getEmail(), role, departmentId);

        return new LoginResponse(token, user.getEmail(), role, userDept);
    }
}
