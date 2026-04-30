package com.iqac.project.controller;

import com.iqac.project.dto.PlanningDTO;
import com.iqac.project.entity.Planning;
import com.iqac.project.service.PlanningService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iqac/planning")
public class PlanningController {

    private final PlanningService planningService;
    private final MessageSource messageSource;

    public PlanningController(PlanningService planningService, MessageSource messageSource) {
        this.planningService = planningService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public ResponseEntity<List<Planning>> getAll(Authentication auth) {
        return ResponseEntity.ok(planningService.getAll(getDeptId(auth)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Planning> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(planningService.getById(id, getDeptId(auth)));
    }

    @PostMapping
    public ResponseEntity<Planning> create(@RequestBody PlanningDTO dto, Authentication auth) {
        return ResponseEntity.ok(planningService.create(dto, getDeptId(auth)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Planning> update(@PathVariable Long id, @RequestBody PlanningDTO dto, Authentication auth) {
        return ResponseEntity.ok(planningService.update(id, dto, getDeptId(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id, Authentication auth) {
        planningService.delete(id, getDeptId(auth));
        return ResponseEntity.ok(messageSource.getMessage("planning.deleted", null, LocaleContextHolder.getLocale()));
    }

    private Long getDeptId(Authentication auth) {
        return (Long) auth.getCredentials();
    }
}
