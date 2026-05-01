package com.iqac.project.controller;

import com.iqac.project.dto.HodDTO;
import com.iqac.project.entity.Hod;
import com.iqac.project.service.HodService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iqac/hod")
public class HodController {

    private final HodService hodService;

    public HodController(HodService hodService) {
        this.hodService = hodService;
    }

    // IQAC: get all HODs
    @GetMapping
    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    public ResponseEntity<List<Hod>> getAll() {
        return ResponseEntity.ok(hodService.getAll());
    }

    // HOD: own profile
    @GetMapping("/me")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<Hod> getOwnProfile(Authentication auth) {
        return ResponseEntity.ok(hodService.getOwnProfile(auth.getName()));
    }

    // IQAC: get HOD by id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    public ResponseEntity<Hod> getById(@PathVariable Long id) {
        return ResponseEntity.ok(hodService.getById(id));
    }

    // IQAC: create HOD
    @PostMapping
    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    public ResponseEntity<Hod> create(@RequestBody HodDTO dto) {
        return ResponseEntity.ok(hodService.create(dto));
    }

    // HOD: update own profile
    @PutMapping("/me")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<Hod> updateOwn(@RequestBody HodDTO dto, Authentication auth) {
        return ResponseEntity.ok(hodService.updateOwn(auth.getName(), dto));
    }

    // IQAC: delete HOD
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        hodService.delete(id);
        return ResponseEntity.ok("HOD deleted successfully");
    }
}
