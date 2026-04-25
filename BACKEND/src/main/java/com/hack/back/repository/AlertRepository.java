package com.hack.back.repository;

import com.hack.back.entity.kpi.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {

    List<Alert> findByStatusOrderByCreatedAtDesc(String status);

    List<Alert> findByInstitutionIdAndStatusOrderByCreatedAtDesc(UUID institutionId, String status);

    @Query("SELECT COUNT(a) FROM Alert a WHERE a.status = 'open'")
    long countOpenAlerts();

    @Query("SELECT a.severity, COUNT(a) FROM Alert a WHERE a.status = 'open' GROUP BY a.severity")
    List<Object[]> countOpenAlertsBySeverity();
}
