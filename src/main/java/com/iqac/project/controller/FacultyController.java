package com.iqac.project.controller;

import com.iqac.project.dto.FacultyDTO;
import com.iqac.project.entity.Faculty;
import com.iqac.project.service.FacultyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iqac/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    // HOD: faculty in same dept | IQAC: all faculty
    @GetMapping
    @PreAuthorize("hasAnyRole('HOD', 'IQAC_COORDINATOR')")
    public ResponseEntity<List<Faculty>> getAll(Authentication auth) {
        if (hasRole(auth, "ROLE_IQAC_COORDINATOR"))
            return ResponseEntity.ok(facultyService.getAllAcrossDepts());
        return ResponseEntity.ok(facultyService.getAll(getDeptId(auth)));
    }

    // FACULTY: own profile
    @GetMapping("/me")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<Faculty> getOwnProfile(Authentication auth) {
        return ResponseEntity.ok(facultyService.getOwnProfile(auth.getName()));
    }

    // HOD: faculty in same dept | IQAC: any
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOD', 'IQAC_COORDINATOR')")
    public ResponseEntity<Faculty> getById(@PathVariable Long id, Authentication auth) {
        if (hasRole(auth, "ROLE_IQAC_COORDINATOR"))
            return ResponseEntity.ok(facultyService.getByIdForIqac(id));
        return ResponseEntity.ok(facultyService.getById(id, getDeptId(auth)));
    }

    // HOD: create faculty in own dept | IQAC: create with dto.departmentId
    @PostMapping
    @PreAuthorize("hasAnyRole('HOD', 'IQAC_COORDINATOR')")
    public ResponseEntity<Faculty> create(@RequestBody FacultyDTO dto, Authentication auth) {
        Long deptId = hasRole(auth, "ROLE_IQAC_COORDINATOR") ? dto.getDepartmentId() : getDeptId(auth);
        return ResponseEntity.ok(facultyService.create(dto, deptId));
    }

    // FACULTY: update own profile only
    @PutMapping("/me")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<Faculty> updateOwn(@RequestBody FacultyDTO dto, Authentication auth) {
        return ResponseEntity.ok(facultyService.updateOwn(auth.getName(), dto));
    }

    // HOD: delete faculty in own dept | IQAC: delete any
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOD', 'IQAC_COORDINATOR')")
    public ResponseEntity<String> delete(@PathVariable Long id, Authentication auth) {
        if (hasRole(auth, "ROLE_IQAC_COORDINATOR"))
            facultyService.deleteForIqac(id);
        else
            facultyService.delete(id, getDeptId(auth));
        return ResponseEntity.ok("Faculty deleted successfully");
    }

    private Long getDeptId(Authentication auth) {
        return (Long) auth.getCredentials();
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }
}
