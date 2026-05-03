package com.iqac.project.service;

import com.iqac.project.dto.HodDTO;
import com.iqac.project.entity.Department;
import com.iqac.project.entity.Hod;
import com.iqac.project.entity.Role;
import com.iqac.project.entity.User;
import com.iqac.project.exception.DuplicateResourceException;
import com.iqac.project.exception.ResourceNotFoundException;
import com.iqac.project.repository.DepartmentRepository;
import com.iqac.project.repository.HodRepository;
import com.iqac.project.repository.RoleRepository;
import com.iqac.project.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HodService {

    private final HodRepository hodRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public HodService(HodRepository hodRepository, DepartmentRepository departmentRepository,
                      UserRepository userRepository, RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder) {
        this.hodRepository = hodRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Hod> getAll() {
        log.info("Fetching all HODs");
        return hodRepository.findAll();
    }

    public Hod getById(Long id) {
        log.info("Fetching HOD id={}", id);
        return hodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found"));
    }

    public Hod getOwnProfile(String email) {
        log.info("Fetching HOD profile for email={}", email);
        return hodRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    public Hod create(HodDTO dto) {
        log.info("Creating HOD email={}, dept={}", dto.getEmail(), dto.getDepartmentId());
        if (hodRepository.existsByEmail(dto.getEmail()) || userRepository.existsByEmail(dto.getEmail()))
            throw new DuplicateResourceException("Email already exists");

        Department dept = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Hod hod = hodRepository.save(Hod.builder()
                .hodName(dto.getHodName())
                .email(dto.getEmail())
                .department(dept)
                .build());

        Role hodRole = roleRepository.findByRoleName("HOD")
                .orElseThrow(() -> new ResourceNotFoundException("HOD role not found"));

        userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode("Hod@1234"))
                .role(hodRole)
                .department(dept)
                .build());

        log.info("HOD created id={}, email={}", hod.getId(), hod.getEmail());
        return hod;
    }

    public Hod updateOwn(String email, HodDTO dto) {
        log.info("Updating HOD profile for email={}", email);
        Hod existing = hodRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        if (hodRepository.existsByEmailAndIdNot(dto.getEmail(), existing.getId()))
            throw new DuplicateResourceException("Email already exists");
        existing.setHodName(dto.getHodName());
        existing.setEmail(dto.getEmail());
        return hodRepository.save(existing);
    }

    public void delete(Long id) {
        log.info("Deleting HOD id={}", id);
        Hod hod = getById(id);
        userRepository.findByEmail(hod.getEmail()).ifPresent(userRepository::delete);
        hodRepository.deleteById(id);
        log.info("HOD id={} deleted", id);
    }
}
