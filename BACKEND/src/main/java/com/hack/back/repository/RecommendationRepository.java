package com.hack.back.repository;

import com.hack.back.entity.ai.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {

    List<Recommendation> findByInstitutionIdAndStatusOrderByPriorityAscCreatedAtDesc(
            UUID institutionId, String status);

    List<Recommendation> findByInstitutionIdOrderByCreatedAtDesc(UUID institutionId);

    @Query("SELECT COUNT(r) FROM Recommendation r WHERE r.status = 'proposed'")
    long countProposedRecommendations();
}
