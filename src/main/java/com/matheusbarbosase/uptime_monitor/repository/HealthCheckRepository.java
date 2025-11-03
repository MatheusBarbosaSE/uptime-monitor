package com.matheusbarbosase.uptime_monitor.repository;

import com.matheusbarbosase.uptime_monitor.model.HealthCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;


@Repository
public interface HealthCheckRepository extends JpaRepository<HealthCheck, Long> {

    Page<HealthCheck> findByTargetIdAndCheckedAtBetween(
            Long targetId,
            Instant startDate,
            Instant endDate,
            Pageable pageable
    );
    void deleteAllByTargetId(Long targetId);
}