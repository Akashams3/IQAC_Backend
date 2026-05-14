package com.iqac.project.service;

import com.iqac.project.dto.CoordinatorDTO;
import com.iqac.project.entity.IqacCoordinator;
import com.iqac.project.exception.DuplicateResourceException;
import com.iqac.project.exception.ResourceNotFoundException;
import com.iqac.project.repository.IqacCoordinatorRepository;
import com.iqac.project.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IqacCoordinatorService {

    private final IqacCoordinatorRepository coordinatorRepository;
    private final UserRepository userRepository;

    public IqacCoordinatorService(IqacCoordinatorRepository coordinatorRepository, UserRepository userRepository) {
        this.coordinatorRepository = coordinatorRepository;
        this.userRepository = userRepository;
    }

    public IqacCoordinator getOwnProfile(String email) {
        log.info("Fetching coordinator profile for email={}", email);
        return coordinatorRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    public IqacCoordinator updateOwn(String email, CoordinatorDTO dto) {
        log.info("Updating coordinator profile for email={}", email);
        IqacCoordinator existing = coordinatorRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        if (coordinatorRepository.existsByEmailAndIdNot(dto.getEmail(), existing.getId()))
            throw new DuplicateResourceException("Email already exists");
        existing.setCoordinatorName(dto.getCoordinatorName());
        existing.setEmail(dto.getEmail());
        userRepository.findByEmail(email).ifPresent(u -> {
            u.setEmail(dto.getEmail());
            userRepository.save(u);
        });
        log.info("Coordinator profile updated for email={}", dto.getEmail());
        return coordinatorRepository.save(existing);
    }
}
