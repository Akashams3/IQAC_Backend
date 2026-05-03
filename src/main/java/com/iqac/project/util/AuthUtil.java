package com.iqac.project.util;

import com.iqac.project.repository.FacultyRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    private final FacultyRepository facultyRepository;

    public AuthUtil(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Long getDeptId(Authentication auth) {
        return (Long) auth.getCredentials();
    }

    public Long getFacultyId(Authentication auth) {
        return facultyRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Faculty not found")).getId();
    }
}
