package com.matheusbarbosase.uptime_monitor.repository;

import com.matheusbarbosase.uptime_monitor.model.HealthCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HealthCheckRepository extends JpaRepository<HealthCheck, Long> {
}