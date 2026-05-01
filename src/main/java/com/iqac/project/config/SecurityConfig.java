package com.iqac.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/iqac/auth/login").permitAll()
                .requestMatchers("/iqac/auth/change-password").hasAnyRole("HOD", "FACULTY", "IQAC_COORDINATOR")
                // coordinator
                .requestMatchers("/iqac/coordinator/me").hasRole("IQAC_COORDINATOR")
                .requestMatchers(HttpMethod.PUT, "/iqac/coordinator/me").hasRole("IQAC_COORDINATOR")
                // hod
                .requestMatchers("/iqac/hod/me").hasRole("HOD")
                .requestMatchers(HttpMethod.PUT, "/iqac/hod/me").hasRole("HOD")
                .requestMatchers("/iqac/hod/**").hasRole("IQAC_COORDINATOR")
                // faculty
                .requestMatchers("/iqac/faculty/me").hasRole("FACULTY")
                .requestMatchers(HttpMethod.PUT, "/iqac/faculty/me").hasRole("FACULTY")
                // department
                .requestMatchers(HttpMethod.POST, "/iqac/department/**").hasRole("IQAC_COORDINATOR")
                .requestMatchers(HttpMethod.PUT, "/iqac/department/**").hasRole("IQAC_COORDINATOR")
                .requestMatchers(HttpMethod.DELETE, "/iqac/department/**").hasRole("IQAC_COORDINATOR")
                // academics
                .requestMatchers("/iqac/academics/**").hasRole("HOD")
                // generic
                .requestMatchers(HttpMethod.GET, "/iqac/**").hasAnyRole("HOD", "FACULTY", "IQAC_COORDINATOR")
                .requestMatchers(HttpMethod.POST, "/iqac/**").hasAnyRole("HOD", "IQAC_COORDINATOR")
                .requestMatchers(HttpMethod.PUT, "/iqac/**").hasAnyRole("HOD", "FACULTY", "IQAC_COORDINATOR")
                .requestMatchers(HttpMethod.DELETE, "/iqac/**").hasAnyRole("HOD", "IQAC_COORDINATOR")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> null;
    }
}
