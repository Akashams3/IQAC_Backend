package com.iqac.project.controller;

import com.iqac.project.dto.CoordinatorDTO;
import com.iqac.project.entity.IqacCoordinator;
import com.iqac.project.service.IqacCoordinatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iqac/coordinator")
public class CoordinatorController {

    private final IqacCoordinatorService coordinatorService;

    public CoordinatorController(IqacCoordinatorService coordinatorService) {
        this.coordinatorService = coordinatorService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    public ResponseEntity<IqacCoordinator> getOwnProfile(Authentication auth) {
        return ResponseEntity.ok(coordinatorService.getOwnProfile(auth.getName()));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('IQAC_COORDINATOR')")
    public ResponseEntity<IqacCoordinator> updateOwn(@RequestBody CoordinatorDTO dto, Authentication auth) {
        return ResponseEntity.ok(coordinatorService.updateOwn(auth.getName(), dto));
    }
}
