package com.hack.back;

import com.hack.back.entity.ai.Insight;
import com.hack.back.entity.ai.Recommendation;
import com.hack.back.entity.kpi.Alert;
import com.hack.back.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@SpringBootApplication
public class BackApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackApplication.class, args);
    }

    /**
     * Seeds realistic mock data for all KPI dashboard endpoints so the dev team
     * can test the frontend without a real database.
     *
     * Fixed UUIDs are used everywhere so that the same IDs can be copy-pasted
     * directly into the Swagger / Postman collection.
     *
     * Covered entities:
     *   - Alert        → GET /api/dashboard/alerts
     *   - Insight      → GET /api/dashboard/insights (executiveSummary)
     *   - Recommendation → GET /api/dashboard/insights (topRecommendations)
     *
     * NOTE: Academic, Finance and HR KPI values are computed on-the-fly by
     *   AcademicKpiService / FinanceKpiService / HrKpiService from fact tables.
     *   Seed those tables (AssessmentResult, BudgetFact, HrFact, etc.) via your
     *   Flyway / Liquibase migration scripts for full KPI coverage.
     */
    @Bean
    @Transactional
    CommandLineRunner dataInitializer(
            AlertRepository          alertRepo,
            InsightRepository        insightRepo,
            RecommendationRepository recRepo
    ) {
        return args -> {

            if (alertRepo.count() > 0) {
                log.info("Mock data already present — skipping seed.");
                return;
            }

            log.info("=== Seeding KPI dashboard mock data ===");

            // ─────────────────────────────────────────────────────────────
            // Stable UUIDs — copy these into your Postman/Swagger calls
            // ─────────────────────────────────────────────────────────────
            final UUID TENANT_ID  = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

            // Institution IDs
            final UUID ENIT_ID    = UUID.fromString("11111111-0000-0000-0000-000000000001");
            final UUID FSEG_ID    = UUID.fromString("11111111-0000-0000-0000-000000000002");
            final UUID IHEC_ID    = UUID.fromString("11111111-0000-0000-0000-000000000003");

            // Period IDs
            final UUID P_2024_S1  = UUID.fromString("22222222-0000-0000-0000-000000000001");
            final UUID P_2024_S2  = UUID.fromString("22222222-0000-0000-0000-000000000002");
            final UUID P_2025_S1  = UUID.fromString("22222222-0000-0000-0000-000000000003");

            // KPI Definition IDs (placeholder — match your kpi_definitions table)
            final UUID KPI_SUCCESS_RATE   = UUID.fromString("33333333-0000-0000-0000-000000000001");
            final UUID KPI_ATTENDANCE     = UUID.fromString("33333333-0000-0000-0000-000000000002");
            final UUID KPI_BUDGET_EXEC    = UUID.fromString("33333333-0000-0000-0000-000000000003");
            final UUID KPI_ABSENTEEISM    = UUID.fromString("33333333-0000-0000-0000-000000000004");
            final UUID KPI_COST_PER_STU   = UUID.fromString("33333333-0000-0000-0000-000000000005");

            // ─────────────────────────────────────────────────────────────
            // ALERTS  (12 open = 1 critical + 3 high + 5 medium + 3 low)
            // ─────────────────────────────────────────────────────────────

            // 1 — CRITICAL
            alertRepo.save(Alert.builder()
                    .kpiId(KPI_SUCCESS_RATE).tenantId(TENANT_ID)
                    .institutionId(FSEG_ID).periodId(P_2025_S1)
                    .alertType("ACADEMIC").severity("critical")
                    .observedValue(58.2).thresholdValue(70.0)
                    .message("FSEG student success rate (58.2%) dropped far below the 70% threshold — emergency intervention required.")
                    .status("open").build());

            // 2-4 — HIGH
            alertRepo.save(Alert.builder()
                    .kpiId(KPI_BUDGET_EXEC).tenantId(TENANT_ID)
                    .institutionId(ENIT_ID).periodId(P_2025_S1)
                    .alertType("FINANCE").severity("high")
                    .observedValue(95.1).thresholdValue(90.0)
                    .message("ENIT budget consumption at 95.1% — overrun risk before end of semester.")
                    .status("open").build());

            alertRepo.save(Alert.builder()
                    .kpiId(KPI_ABSENTEEISM).tenantId(TENANT_ID)
                    .institutionId(IHEC_ID).periodId(P_2025_S1)
                    .alertType("HR").severity("high")
                    .observedValue(12.4).thresholdValue(10.0)
                    .message("IHEC staff absenteeism rate (12.4%) exceeds the 10% threshold — staffing risk detected.")
                    .status("open").build());

            alertRepo.save(Alert.builder()
                    .kpiId(KPI_ATTENDANCE).tenantId(TENANT_ID)
                    .institutionId(ENIT_ID).periodId(P_2025_S1)
                    .alertType("ACADEMIC").severity("high")
                    .observedValue(78.3).thresholdValue(80.0)
                    .message("ENIT student attendance rate (78.3%) fell below the 80% minimum threshold.")
                    .status("open").build());

            // 5-9 — MEDIUM
            alertRepo.save(Alert.builder()
                    .kpiId(KPI_COST_PER_STU).tenantId(TENANT_ID)
                    .institutionId(FSEG_ID).periodId(P_2025_S1)
                    .alertType("FINANCE").severity("medium")
                    .observedValue(4050.0).thresholdValue(3800.0)
                    .message("FSEG cost-per-student (4050 TND) rose 6.6% above the target ceiling of 3800 TND.")
                    .status("open").build());

            alertRepo.save(Alert.builder()
                    .kpiId(KPI_ABSENTEEISM).tenantId(TENANT_ID)
                    .institutionId(ENIT_ID).periodId(P_2025_S1)
                    .alertType("HR").severity("medium")
                    .observedValue(9.1).thresholdValue(8.0)
                    .message("ENIT administrative staff absenteeism (9.1%) exceeds the 8% internal benchmark.")
                    .status("open").build());

            alertRepo.save(Alert.builder()
                    .kpiId(KPI_SUCCESS_RATE).tenantId(TENANT_ID)
                    .institutionId(IHEC_ID).periodId(P_2025_S1)
                    .alertType("ACADEMIC").severity("medium")
                    .observedValue(61.5).thresholdValue(70.0)
                    .message("IHEC success rate trending downward for 3 consecutive periods — trend analysis required.")
                    .status("open").build());

            alertRepo.save(Alert.builder()
                    .kpiId(KPI_BUDGET_EXEC).tenantId(TENANT_ID)
                    .institutionId(FSEG_ID).periodId(P_2025_S1)
                    .alertType("FINANCE").severity("medium")
                    .observedValue(47.3).thresholdValue(60.0)
                    .message("FSEG budget execution at 47.3% at mid-semester — severe underspend risk.")
                    .status("open").build());

            alertRepo.save(Alert.builder()
                    .kpiId(KPI_ATTENDANCE).tenantId(TENANT_ID)
                    .institutionId(FSEG_ID).periodId(P_2024_S2)
                    .alertType("ACADEMIC").severity("medium")
                    .observedValue(81.0).thresholdValue(85.0)
                    .message("FSEG attendance rate (81.0%) missed the 85% excellence threshold in 2024-S2.")
                    .status("acknowledged").build());

            // 10-12 — LOW
            alertRepo.save(Alert.builder()
                    .kpiId(KPI_ATTENDANCE).tenantId(TENANT_ID)
                    .institutionId(IHEC_ID).periodId(P_2025_S1)
                    .alertType("ACADEMIC").severity("low")
                    .observedValue(0.0).thresholdValue(0.0)
                    .message("IHEC attendance data missing for 2 course sections in 2025-S1 — data quality issue.")
                    .status("open").build());

            alertRepo.save(Alert.builder()
                    .kpiId(KPI_BUDGET_EXEC).tenantId(TENANT_ID)
                    .institutionId(ENIT_ID).periodId(P_2025_S1)
                    .alertType("FINANCE").severity("low")
                    .observedValue(15.2).thresholdValue(10.0)
                    .message("ENIT uncommitted operational budget exceeds 15% — recommend proactive reallocation.")
                    .status("open").build());

            alertRepo.save(Alert.builder()
                    .kpiId(KPI_ABSENTEEISM).tenantId(TENANT_ID)
                    .institutionId(FSEG_ID).periodId(P_2025_S1)
                    .alertType("HR").severity("low")
                    .observedValue(0.0).thresholdValue(0.0)
                    .message("FSEG HR fact records incomplete for 2025-S1 — 2 org-units have no submissions.")
                    .status("open").build());

            // ─────────────────────────────────────────────────────────────
            // INSIGHTS  (executive_summary — one per institution per period)
            // ─────────────────────────────────────────────────────────────

            insightRepo.save(Insight.builder()
                    .tenantId(TENANT_ID).institutionId(ENIT_ID).periodId(P_2025_S1)
                    .insightType("executive_summary")
                    .title("Executive Summary — ENIT 2025-S1")
                    .summaryText("ENIT shows strong improvement in academic performance with a success rate of 78.4% (+2.1% vs 2024-S2). " +
                            "Budget execution is healthy at 85.7% and on track to close without overrun. " +
                            "HR absenteeism decreased to 6.3%, signaling improved workforce stability. " +
                            "Teaching load average is 18.5 hrs/week, within the recommended range.")
                    .generatedBy("ai-engine").confidenceScore(0.87).build());

            insightRepo.save(Insight.builder()
                    .tenantId(TENANT_ID).institutionId(FSEG_ID).periodId(P_2025_S1)
                    .insightType("executive_summary")
                    .title("Executive Summary — FSEG 2025-S1")
                    .summaryText("FSEG academic results remain below target: success rate of 65.2% is 4.8 pts below the 70% floor. " +
                            "Budget execution stands at 47.3% at mid-semester — a significant underspend requiring proactive reallocation. " +
                            "Absenteeism rose to 8.1%, crossing the internal HR threshold for the first time in 4 periods.")
                    .generatedBy("ai-engine").confidenceScore(0.79).build());

            insightRepo.save(Insight.builder()
                    .tenantId(TENANT_ID).institutionId(IHEC_ID).periodId(P_2025_S1)
                    .insightType("executive_summary")
                    .title("Executive Summary — IHEC 2025-S1")
                    .summaryText("IHEC leads all three institutions in academic performance with an 83.1% success rate (+1.2% YoY). " +
                            "Budget execution is on track at 88.4%. However, staff absenteeism at 10.2% is elevated and " +
                            "requires a structured HR intervention before semester-end to avoid operational disruption.")
                    .generatedBy("ai-engine").confidenceScore(0.91).build());

            insightRepo.save(Insight.builder()
                    .tenantId(TENANT_ID).institutionId(ENIT_ID).periodId(P_2024_S2)
                    .insightType("executive_summary")
                    .title("Executive Summary — ENIT 2024-S2")
                    .summaryText("ENIT recorded a success rate of 75.4% in 2024-S2, a 3.3 pt improvement over 2024-S1. " +
                            "Budget consumed at 82.1%. Absenteeism was stable at 7.5%.")
                    .generatedBy("ai-engine").confidenceScore(0.83).build());

            insightRepo.save(Insight.builder()
                    .tenantId(TENANT_ID).institutionId(ENIT_ID).periodId(P_2024_S1)
                    .insightType("executive_summary")
                    .title("Executive Summary — ENIT 2024-S1")
                    .summaryText("ENIT baseline period: success rate 72.1%, budget execution 79.3%, absenteeism 8.1%.")
                    .generatedBy("ai-engine").confidenceScore(0.75).build());

            // ─────────────────────────────────────────────────────────────
            // RECOMMENDATIONS  (5 proposed — mixed institutions)
            // ─────────────────────────────────────────────────────────────

            recRepo.save(Recommendation.builder()
                    .tenantId(TENANT_ID).institutionId(ENIT_ID).periodId(P_2025_S1)
                    .category("ACADEMIC").priority("high")
                    .title("Reinforce tutoring in engineering modules")
                    .description("Modules with success rates below 60% require targeted tutoring programs before end of semester. " +
                            "Focus on Mathematics, Physics and Programming fundamentals.")
                    .justification("Success rate dropped 4.2 pts in CS and Math modules compared to 2024-S2.")
                    .generatedBy("hybrid-engine").status("proposed").build());

            recRepo.save(Recommendation.builder()
                    .tenantId(TENANT_ID).institutionId(ENIT_ID).periodId(P_2025_S1)
                    .category("FINANCE").priority("medium")
                    .title("Reallocate uncommitted budget to lab equipment")
                    .description("15% of operational budget remains uncommitted mid-semester. " +
                            "Redirect funds to equipment procurement and deferred maintenance to avoid year-end budget lapse.")
                    .justification("Lab utilisation rate is 92% but maintenance backlog grew 23% this semester.")
                    .generatedBy("rules-engine").status("proposed").build());

            recRepo.save(Recommendation.builder()
                    .tenantId(TENANT_ID).institutionId(FSEG_ID).periodId(P_2025_S1)
                    .category("ACADEMIC").priority("urgent")
                    .title("Launch emergency academic support for FSEG underperforming cohorts")
                    .description("Success rate at 65.2% is 4.8 pts below institutional target. " +
                            "Activate peer-mentoring programme and schedule weekly remedial sessions for at-risk students.")
                    .justification("3 consecutive periods of decline detected by trend engine.")
                    .generatedBy("hybrid-engine").status("proposed").build());

            recRepo.save(Recommendation.builder()
                    .tenantId(TENANT_ID).institutionId(FSEG_ID).periodId(P_2025_S1)
                    .category("FINANCE").priority("high")
                    .title("Accelerate FSEG budget execution before mid-semester deadline")
                    .description("Only 47.3% of the budget has been executed. " +
                            "Finance office must expedite pending purchase orders and supplier invoices by end of week 8.")
                    .justification("Historical pattern shows <50% mid-semester execution leads to year-end overruns.")
                    .generatedBy("rules-engine").status("proposed").build());

            recRepo.save(Recommendation.builder()
                    .tenantId(TENANT_ID).institutionId(IHEC_ID).periodId(P_2025_S1)
                    .category("HR").priority("medium")
                    .title("Reduce IHEC staff absenteeism through flexible scheduling")
                    .description("Introduce flexible work arrangements for administrative staff to reduce absenteeism from 10.2% below the 8% target. " +
                            "Pilot a 4-day compressed work week for non-teaching roles.")
                    .justification("Exit surveys indicate commute time is the primary driver of staff absences.")
                    .generatedBy("ai-engine").status("proposed").build());

            log.info("=== Seed complete: {} alerts | {} insights | {} recommendations ===",
                    alertRepo.count(), insightRepo.count(), recRepo.count());

            log.info("""
                    ╔══════════════════════════════════════════════════════════╗
                    ║  DEV QUICK-REFERENCE — copy into Swagger/Postman         ║
                    ╠══════════════════════════════════════════════════════════╣
                    ║  institutionId (ENIT) : 11111111-0000-0000-0000-000000000001 ║
                    ║  institutionId (FSEG) : 11111111-0000-0000-0000-000000000002 ║
                    ║  institutionId (IHEC) : 11111111-0000-0000-0000-000000000003 ║
                    ║  periodId (2024-S1)   : 22222222-0000-0000-0000-000000000001 ║
                    ║  periodId (2024-S2)   : 22222222-0000-0000-0000-000000000002 ║
                    ║  periodId (2025-S1)   : 22222222-0000-0000-0000-000000000003 ║
                    ╚══════════════════════════════════════════════════════════╝
                    """);
        };
    }
}
