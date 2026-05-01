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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    // IQAC: get all HODs
    public List<Hod> getAll() {
        return hodRepository.findAll();
    }

    // IQAC: get HOD by id
    public Hod getById(Long id) {
        return hodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found"));
    }

    // HOD: own profile
    public Hod getOwnProfile(String email) {
        return hodRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    // IQAC: create HOD
    public Hod create(HodDTO dto) {
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

        return hod;
    }

    // HOD: update own profile only
    public Hod updateOwn(String email, HodDTO dto) {
        Hod existing = hodRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        if (hodRepository.existsByEmailAndIdNot(dto.getEmail(), existing.getId()))
            throw new DuplicateResourceException("Email already exists");
        existing.setHodName(dto.getHodName());
        existing.setEmail(dto.getEmail());
        return hodRepository.save(existing);
    }

    // IQAC: delete HOD
    public void delete(Long id) {
        Hod hod = getById(id);
        userRepository.findByEmail(hod.getEmail()).ifPresent(userRepository::delete);
        hodRepository.deleteById(id);
    }
}
