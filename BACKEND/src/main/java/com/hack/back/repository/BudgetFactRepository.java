package com.hack.back.repository;

import com.hack.back.entity.fact.BudgetFact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetFactRepository extends JpaRepository<BudgetFact, UUID> {

    /** Sum of amounts by institution, period, and budget_type */
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM BudgetFact b " +
           "WHERE b.institution.institutionId = :institutionId " +
           "AND b.period.periodId = :periodId " +
           "AND b.budgetType = :budgetType")
    BigDecimal sumByInstitutionAndPeriodAndType(@Param("institutionId") UUID institutionId,
                                                @Param("periodId") UUID periodId,
                                                @Param("budgetType") String budgetType);

    /** Sum by org unit, period, and budget_type (for department breakdown) */
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM BudgetFact b " +
           "WHERE b.orgUnit.orgUnitId = :orgUnitId " +
           "AND b.period.periodId = :periodId " +
           "AND b.budgetType = :budgetType")
    BigDecimal sumByOrgUnitAndPeriodAndType(@Param("orgUnitId") UUID orgUnitId,
                                            @Param("periodId") UUID periodId,
                                            @Param("budgetType") String budgetType);

    /** All budget facts for an institution in a period */
    List<BudgetFact> findByInstitution_InstitutionIdAndPeriod_PeriodId(UUID institutionId, UUID periodId);
}
