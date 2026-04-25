package com.hack.back.service.kpi;

import com.hack.back.dto.dashboard.*;
import com.hack.back.entity.kpi.Alert;
import com.hack.back.entity.ai.Insight;
import com.hack.back.entity.ai.Recommendation;
import com.hack.back.repository.AlertRepository;
import com.hack.back.repository.InsightRepository;
import com.hack.back.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Assembles all dashboard widget responses.
 * Delegates KPI computation to domain-specific services and
 * maps persistence entities to frontend-ready DTOs.
 *
 * Endpoints served:
 *   GET /api/dashboard/overview
 *   GET /api/dashboard/trends
 *   GET /api/dashboard/comparison
 *   GET /api/dashboard/finance
 *   GET /api/dashboard/hr
 *   GET /api/dashboard/alerts
 *   GET /api/dashboard/insights
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final AcademicKpiService   academicKpi;
    private final FinanceKpiService    financeKpi;
    private final HrKpiService         hrKpi;
    private final AlertRepository      alertRepo;
    private final InsightRepository    insightRepo;
    private final RecommendationRepository recommendationRepo;

    // ═══════════════════════════════════════════════════════════════════════
    // 1. OVERVIEW
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Top KPI stat-cards + open alert count + recommendation count.
     *
     * @param institutionId   the institution in scope
     * @param institutionCode human-readable code for display
     * @param periodId        the reporting period
     * @param periodLabel     human-readable period label (e.g. "2025-S1")
     */
    public OverviewDto buildOverview(UUID institutionId, String institutionCode,
                                     UUID periodId, String periodLabel) {

        List<StatCardDto> cards = new ArrayList<>();

        cards.add(academicKpi.successRateCard(institutionId, institutionCode, periodId, periodLabel));
        cards.add(financeKpi.budgetExecutionRateCard(institutionId, institutionCode, periodId, periodLabel));
        cards.add(hrKpi.absenteeismRateCard(institutionId, institutionCode, periodId, periodLabel));
        cards.add(financeKpi.costPerStudentCard(institutionId, institutionCode, periodId, periodLabel));

        long openAlerts       = alertRepo.countOpenAlerts();
        long recommendations  = recommendationRepo.countProposedRecommendations();

        return OverviewDto.builder()
                .kpiCards(cards)
                .openAlertsCount(openAlerts)
                .recommendationsCount(recommendations)
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. TRENDS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Returns a list of line-chart DTOs — one per supported KPI —
     * showing values over multiple periods for a single institution.
     *
     * @param kpiKey       one of: successRate | attendanceRate |
     *                     budgetExecutionRate | absenteeismRate
     * @param institutionId target institution
     * @param periodIds    ordered period UUIDs (oldest → newest)
     * @param periodLabels ordered period labels (same order)
     */
    public ChartDto buildTrend(String kpiKey,
                                UUID institutionId,
                                List<UUID> periodIds,
                                List<String> periodLabels) {
        return switch (kpiKey) {
            case "successRate"       -> academicKpi.successRateTrendChart(institutionId, periodIds, periodLabels);
            case "attendanceRate"    -> academicKpi.attendanceRateTrendChart(institutionId, periodIds, periodLabels);
            case "budgetExecutionRate" -> financeKpi.budgetExecutionTrendChart(institutionId, periodIds, periodLabels);
            case "absenteeismRate"   -> hrKpi.absenteeismTrendChart(institutionId, periodIds, periodLabels);
            default -> throw new IllegalArgumentException("Unknown KPI key for trend: " + kpiKey);
        };
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. COMPARISON
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Returns a bar-chart comparing a KPI across institutions for one period.
     *
     * @param kpiKey           one of: successRate | attendanceRate |
     *                         budgetExecutionRate | absenteeismRate |
     *                         costPerStudent
     * @param institutionIds   ordered list of institution UUIDs
     * @param institutionCodes ordered list of human-readable codes (same order)
     * @param periodId         the reporting period
     */
    public ChartDto buildComparison(String kpiKey,
                                     List<UUID> institutionIds,
                                     List<String> institutionCodes,
                                     UUID periodId) {
        return switch (kpiKey) {
            case "successRate"         -> academicKpi.successRateComparisonChart(institutionIds, institutionCodes, periodId);
            case "attendanceRate"      -> academicKpi.attendanceRateComparisonChart(institutionIds, institutionCodes, periodId);
            case "budgetExecutionRate" -> financeKpi.budgetExecutionComparisonChart(institutionIds, institutionCodes, periodId);
            case "absenteeismRate"     -> hrKpi.absenteeismRateComparisonChart(institutionIds, institutionCodes, periodId);
            case "costPerStudent"      -> buildCostPerStudentComparisonChart(institutionIds, institutionCodes, periodId);
            default -> throw new IllegalArgumentException("Unknown KPI key for comparison: " + kpiKey);
        };
    }

    private ChartDto buildCostPerStudentComparisonChart(List<UUID> institutionIds,
                                                        List<String> institutionCodes,
                                                        UUID periodId) {
        List<Double> data = new ArrayList<>();
        for (UUID id : institutionIds) {
            Double v = financeKpi.computeCostPerStudent(id, periodId);
            data.add(v != null ? v : 0.0);
        }
        return ChartDto.builder()
                .chartType("bar")
                .title("Cost per Student by Institution")
                .categories(institutionCodes)
                .series(List.of(ChartDto.SeriesDto.builder()
                        .key("costPerStudent").label("Cost per Student")
                        .data(data).unit("TND").build()))
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. FINANCE
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Finance dashboard: allocated vs consumed chart + cost per student comparison.
     *
     * @param institutionIds   all institutions in scope
     * @param institutionCodes human-readable codes (same order)
     * @param periodId         reporting period
     */
    public FinanceDashboardDto buildFinanceDashboard(List<UUID> institutionIds,
                                                      List<String> institutionCodes,
                                                      UUID periodId) {
        ChartDto allocVsConsumed = financeKpi.allocatedVsConsumedChart(institutionIds, institutionCodes, periodId);
        ChartDto costPerStudent  = buildCostPerStudentComparisonChart(institutionIds, institutionCodes, periodId);

        return FinanceDashboardDto.builder()
                .allocatedVsConsumedChart(allocVsConsumed)
                .spendingByDepartmentChart(costPerStudent)
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 5. HR
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * HR dashboard: headcount + teaching load + absenteeism charts.
     */
    public HrDashboardDto buildHrDashboard(List<UUID> institutionIds,
                                            List<String> institutionCodes,
                                            UUID periodId) {
        return HrDashboardDto.builder()
                .headcountChart(hrKpi.headcountComparisonChart(institutionIds, institutionCodes))
                .teachingLoadChart(hrKpi.teachingLoadChart(institutionIds, institutionCodes, periodId))
                .absenteeismChart(hrKpi.absenteeismRateComparisonChart(institutionIds, institutionCodes, periodId))
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 6. ALERTS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Alert list (open by default) + severity summary.
     *
     * @param institutionId optional — filter by institution; null = all institutions
     */
    public AlertListDto buildAlertList(UUID institutionId) {
        List<Alert> alerts = institutionId != null
                ? alertRepo.findByInstitutionIdAndStatusOrderByCreatedAtDesc(institutionId, "open")
                : alertRepo.findByStatusOrderByCreatedAtDesc("open");

        List<AlertListDto.AlertItemDto> items = alerts.stream()
                .map(a -> AlertListDto.AlertItemDto.builder()
                        .alertId(a.getAlertId())
                        .severity(a.getSeverity())
                        .alertType(a.getAlertType())
                        .message(a.getMessage())
                        .institutionId(a.getInstitutionId() != null ? a.getInstitutionId().toString() : null)
                        .periodId(a.getPeriodId() != null ? a.getPeriodId().toString() : null)
                        .status(a.getStatus())
                        .observedValue(a.getObservedValue())
                        .thresholdValue(a.getThresholdValue())
                        .build())
                .collect(Collectors.toList());

        return AlertListDto.builder()
                .title("Open Alerts")
                .items(items)
                .build();
    }

    public AlertSummaryDto buildAlertSummary() {
        long totalOpen = alertRepo.countOpenAlerts();

        List<Object[]> rows = alertRepo.countOpenAlertsBySeverity();
        Map<String, Long> bySeverity = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String severity = (String) row[0];
            Long   count    = (Long)   row[1];
            bySeverity.put(severity, count);
        }

        return AlertSummaryDto.builder()
                .totalOpen(totalOpen)
                .bySeverity(bySeverity)
                .build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 7. INSIGHTS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Executive summary insight card + top proposed recommendations.
     *
     * @param institutionId target institution
     * @param periodId      reporting period
     */
    public InsightsDashboardDto buildInsightsDashboard(UUID institutionId, UUID periodId) {

        // ── Insight card ─────────────────────────────────────────────────
        InsightCardDto insightCard = insightRepo
                .findFirstByInstitutionIdAndPeriodIdAndInsightTypeOrderByCreatedAtDesc(
                        institutionId, periodId, "executive_summary")
                .map(i -> InsightCardDto.builder()
                        .title(i.getTitle())
                        .summaryText(i.getSummaryText())
                        .confidenceScore(i.getConfidenceScore())
                        .insightType(i.getInsightType())
                        .build())
                .orElse(InsightCardDto.builder()
                        .title("Executive Summary")
                        .summaryText("No AI insight available yet for this period.")
                        .confidenceScore(null)
                        .insightType("executive_summary")
                        .build());

        // ── Recommendations ───────────────────────────────────────────────
        List<Recommendation> recs = recommendationRepo
                .findByInstitutionIdAndStatusOrderByPriorityAscCreatedAtDesc(institutionId, "proposed");

        List<RecommendationListDto.RecommendationItemDto> recItems = recs.stream()
                .limit(5)
                .map(r -> RecommendationListDto.RecommendationItemDto.builder()
                        .recommendationId(r.getRecommendationId())
                        .priority(r.getPriority())
                        .category(r.getCategory())
                        .title(r.getTitle())
                        .description(r.getDescription())
                        .status(r.getStatus())
                        .build())
                .collect(Collectors.toList());

        RecommendationListDto recList = RecommendationListDto.builder()
                .title("Top Recommendations")
                .items(recItems)
                .build();

        return InsightsDashboardDto.builder()
                .executiveSummary(insightCard)
                .topRecommendations(recList)
                .build();
    }
}
