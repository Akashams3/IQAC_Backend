package com.iqac.project.repository;

import com.iqac.project.entity.IqacCoordinator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IqacCoordinatorRepository extends JpaRepository<IqacCoordinator, Long> {
    Optional<IqacCoordinator> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
}
