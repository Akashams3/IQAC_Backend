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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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

    public List<Faculty> getAll(Long departmentId) {
        log.info("Fetching all faculty for dept={}", departmentId);
        return facultyRepository.findByDepartmentId(departmentId);
    }

    public List<Faculty> getAllAcrossDepts() {
        log.info("Fetching all faculty across departments");
        return facultyRepository.findAll();
    }

    public Faculty getById(Long id, Long departmentId) {
        log.info("Fetching faculty id={} for dept={}", id, departmentId);
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));
        if (!faculty.getDepartment().getId().equals(departmentId))
            throw new ResourceNotFoundException("Faculty not found in your department");
        return faculty;
    }

    public Faculty getByIdForIqac(Long id) {
        log.info("Fetching faculty id={} for IQAC", id);
        return facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));
    }

    public Faculty getOwnProfile(String email) {
        log.info("Fetching own profile for email={}", email);
        return facultyRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    public Faculty create(FacultyDTO dto, Long departmentId) {
        log.info("Creating faculty email={}, dept={}", dto.getEmail(), departmentId);
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

        log.info("Faculty created id={}, email={}", faculty.getId(), faculty.getEmail());
        return faculty;
    }

    public Faculty updateOwn(String email, FacultyDTO dto) {
        log.info("Updating own profile for email={}", email);
        Faculty existing = facultyRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        if (facultyRepository.existsByEmailAndIdNot(dto.getEmail(), existing.getId()))
            throw new DuplicateResourceException("Email already exists");
        existing.setFacultyName(dto.getFacultyName());
        existing.setEmail(dto.getEmail());
        existing.setDesignation(dto.getDesignation());
        return facultyRepository.save(existing);
    }

    public void delete(Long id, Long departmentId) {
        log.info("Deleting faculty id={} from dept={}", id, departmentId);
        Faculty faculty = getById(id, departmentId);
        userRepository.findByEmail(faculty.getEmail()).ifPresent(userRepository::delete);
        facultyRepository.deleteById(id);
    }

    public void deleteForIqac(Long id) {
        log.info("Deleting faculty id={} by IQAC", id);
        Faculty faculty = getByIdForIqac(id);
        userRepository.findByEmail(faculty.getEmail()).ifPresent(userRepository::delete);
        facultyRepository.deleteById(id);
    }
}
