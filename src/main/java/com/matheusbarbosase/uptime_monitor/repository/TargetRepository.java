package com.matheusbarbosase.uptime_monitor.repository;

import com.matheusbarbosase.uptime_monitor.model.Target;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TargetRepository extends JpaRepository<Target, Long> {
}
