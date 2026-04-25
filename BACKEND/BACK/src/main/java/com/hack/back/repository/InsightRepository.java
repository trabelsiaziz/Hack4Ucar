package com.hack.back.repository;

import com.hack.back.entity.ai.Insight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InsightRepository extends JpaRepository<Insight, UUID> {

    List<Insight> findByInstitutionIdAndPeriodIdOrderByCreatedAtDesc(UUID institutionId, UUID periodId);

    Optional<Insight> findFirstByInstitutionIdAndPeriodIdAndInsightTypeOrderByCreatedAtDesc(
            UUID institutionId, UUID periodId, String insightType);
}
