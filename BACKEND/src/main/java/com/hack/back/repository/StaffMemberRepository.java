package com.hack.back.repository;

import com.hack.back.entity.domain.StaffMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffMemberRepository extends JpaRepository<StaffMember, UUID> {

    List<StaffMember> findByInstitution_InstitutionId(UUID institutionId);

    long countByInstitution_InstitutionIdAndEmploymentStatus(UUID institutionId, String employmentStatus);
}
