package com.iqac.project.service;

import com.iqac.project.dto.FacultyDTO;
import com.iqac.project.entity.Department;
import com.iqac.project.entity.Faculty;
import com.iqac.project.entity.Role;
import com.iqac.project.entity.User;
import com.iqac.project.exception.DuplicateResourceException;
import com.iqac.project.exception.ResourceNotFoundException;
import com.iqac.project.repository.DepartmentRepository;
import com.iqac.project.repository.FacultyRepository;
import com.iqac.project.repository.RoleRepository;
import com.iqac.project.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public FacultyService(FacultyRepository facultyRepository, DepartmentRepository departmentRepository,
                          UserRepository userRepository, RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder) {
        this.facultyRepository = facultyRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // HOD: faculty in same dept | IQAC: all faculty
    public List<Faculty> getAll(Long departmentId) {
        return facultyRepository.findByDepartmentId(departmentId);
    }

    public List<Faculty> getAllAcrossDepts() {
        return facultyRepository.findAll();
    }

    // HOD: faculty in same dept by id | IQAC: any by id
    public Faculty getById(Long id, Long departmentId) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));
        if (!faculty.getDepartment().getId().equals(departmentId))
            throw new ResourceNotFoundException("Faculty not found in your department");
        return faculty;
    }

    public Faculty getByIdForIqac(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));
    }

    // FACULTY: own profile
    public Faculty getOwnProfile(String email) {
        return facultyRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    // HOD: create faculty in own dept | IQAC: create faculty with explicit deptId
    public Faculty create(FacultyDTO dto, Long departmentId) {
        if (facultyRepository.existsByEmail(dto.getEmail()) || userRepository.existsByEmail(dto.getEmail()))
            throw new DuplicateResourceException("Email already exists");

        Department dept = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));

        Faculty faculty = facultyRepository.save(Faculty.builder()
                .facultyName(dto.getFacultyName())
                .email(dto.getEmail())
                .designation(dto.getDesignation())
                .department(dept)
                .build());

        Role facultyRole = roleRepository.findByRoleName("FACULTY")
                .orElseThrow(() -> new ResourceNotFoundException("FACULTY role not found"));

        userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode("Faculty@1234"))
                .role(facultyRole)
                .department(dept)
                .build());

        return faculty;
    }

    // FACULTY: update own profile only
    public Faculty updateOwn(String email, FacultyDTO dto) {
        Faculty existing = facultyRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        if (facultyRepository.existsByEmailAndIdNot(dto.getEmail(), existing.getId()))
            throw new DuplicateResourceException("Email already exists");
        existing.setFacultyName(dto.getFacultyName());
        existing.setEmail(dto.getEmail());
        existing.setDesignation(dto.getDesignation());
        return facultyRepository.save(existing);
    }

    // HOD: delete faculty in own dept | IQAC: delete any
    public void delete(Long id, Long departmentId) {
        Faculty faculty = getById(id, departmentId);
        userRepository.findByEmail(faculty.getEmail()).ifPresent(userRepository::delete);
        facultyRepository.deleteById(id);
    }

    public void deleteForIqac(Long id) {
        Faculty faculty = getByIdForIqac(id);
        userRepository.findByEmail(faculty.getEmail()).ifPresent(userRepository::delete);
        facultyRepository.deleteById(id);
    }
}
