package com.iqac.project.service;

import com.iqac.project.dto.CoordinatorDTO;
import com.iqac.project.entity.IqacCoordinator;
import com.iqac.project.exception.DuplicateResourceException;
import com.iqac.project.exception.ResourceNotFoundException;
import com.iqac.project.repository.IqacCoordinatorRepository;
import com.iqac.project.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class IqacCoordinatorService {

    private final IqacCoordinatorRepository coordinatorRepository;
    private final UserRepository userRepository;

    public IqacCoordinatorService(IqacCoordinatorRepository coordinatorRepository, UserRepository userRepository) {
        this.coordinatorRepository = coordinatorRepository;
        this.userRepository = userRepository;
    }

    public IqacCoordinator getOwnProfile(String email) {
        return coordinatorRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    public IqacCoordinator updateOwn(String email, CoordinatorDTO dto) {
        IqacCoordinator existing = coordinatorRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        if (coordinatorRepository.existsByEmailAndIdNot(dto.getEmail(), existing.getId()))
            throw new DuplicateResourceException("Email already exists");
        existing.setCoordinatorName(dto.getCoordinatorName());
        existing.setEmail(dto.getEmail());
        // sync email in users table
        userRepository.findByEmail(email).ifPresent(u -> {
            u.setEmail(dto.getEmail());
            userRepository.save(u);
        });
        return coordinatorRepository.save(existing);
    }
}
