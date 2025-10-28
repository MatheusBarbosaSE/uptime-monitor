package com.matheusbarbosase.uptime_monitor.repository;

import com.matheusbarbosase.uptime_monitor.model.Target;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TargetRepository extends JpaRepository<Target, Long> {

    List<Target> findAllByUserId(Long userId);
    Optional<Target> findByIdAndUserId(Long id, Long userId);
}