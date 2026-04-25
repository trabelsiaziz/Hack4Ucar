package com.hack.back.application;

import com.hack.back.dto.dashboard.*;
import com.hack.back.service.kpi.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller exposing KPI dashboard endpoints (V1).
 *
 * All endpoints are chart-ready / widget-ready — no KPI logic in the frontend.
 *
 * Base path: /api/dashboard
 *
 * ┌──────────────────────────────────────────────────────────────────────────────┐
 * │  GET /api/dashboard/overview    — top stat-cards, alert count, rec count     │
 * │  GET /api/dashboard/trends      — line-chart for one KPI over periods        │
 * │  GET /api/dashboard/comparison  — bar-chart comparing institutions           │
 * │  GET /api/dashboard/finance     — finance charts (alloc vs consumed, etc.)   │
 * │  GET /api/dashboard/hr          — HR charts (headcount, load, absenteeism)   │
 * │  GET /api/dashboard/alerts      — alert list + severity summary              │
 * │  GET /api/dashboard/insights    — executive insight + top recommendations    │
 * └──────────────────────────────────────────────────────────────────────────────┘
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // ── 1. GET /api/dashboard/overview ────────────────────────────────────
    /**
     * Returns the top KPI stat-cards, open alert count, and proposed recommendation count.
     *
     * @param institutionId   UUID of the institution in scope
     * @param institutionCode human-readable institution code (for display labels)
     * @param periodId        UUID of the reporting period
     * @param periodLabel     human-readable period label (e.g. "2025-S1")
     *
     * @return {@link OverviewDto}
     */
    @GetMapping("/overview")
    public ResponseEntity<OverviewDto> getOverview(
            @RequestParam UUID institutionId,
            @RequestParam String institutionCode,
            @RequestParam UUID periodId,
            @RequestParam String periodLabel) {

        return ResponseEntity.ok(
                dashboardService.buildOverview(institutionId, institutionCode, periodId, periodLabel));
    }

    // ── 2. GET /api/dashboard/trends ──────────────────────────────────────
    /**
     * Returns a line-chart for one KPI tracked over multiple periods.
     *
     * @param kpi             KPI key — one of:
     *                        successRate | attendanceRate |
     *                        budgetExecutionRate | absenteeismRate
     * @param institutionId   institution UUID in scope
     * @param periodIds       comma-separated ordered period UUIDs (oldest → newest)
     * @param periodLabels    comma-separated period labels (same order as periodIds)
     *
     * @return {@link ChartDto} with chartType=line
     */
    @GetMapping("/trends")
    public ResponseEntity<ChartDto> getTrend(
            @RequestParam String kpi,
            @RequestParam UUID institutionId,
            @RequestParam String periodIds,
            @RequestParam String periodLabels) {

        List<UUID>   ids    = parseUuids(periodIds);
        List<String> labels = parseLabels(periodLabels);

        return ResponseEntity.ok(
                dashboardService.buildTrend(kpi, institutionId, ids, labels));
    }

    // ── 3. GET /api/dashboard/comparison ─────────────────────────────────
    /**
     * Returns a bar-chart comparing one KPI across multiple institutions.
     *
     * @param kpi              KPI key — one of:
     *                         successRate | attendanceRate |
     *                         budgetExecutionRate | absenteeismRate | costPerStudent
     * @param institutionIds   comma-separated institution UUIDs (same order as codes)
     * @param institutionCodes comma-separated institution codes for display
     * @param periodId         UUID of the reporting period
     *
     * @return {@link ChartDto} with chartType=bar
     */
    @GetMapping("/comparison")
    public ResponseEntity<ChartDto> getComparison(
            @RequestParam String kpi,
            @RequestParam String institutionIds,
            @RequestParam String institutionCodes,
            @RequestParam UUID periodId) {

        List<UUID>   ids   = parseUuids(institutionIds);
        List<String> codes = parseLabels(institutionCodes);

        return ResponseEntity.ok(
                dashboardService.buildComparison(kpi, ids, codes, periodId));
    }

    // ── 4. GET /api/dashboard/finance ─────────────────────────────────────
    /**
     * Returns finance dashboard widgets:
     * - Allocated vs Consumed Budget (stacked-bar per institution)
     * - Cost per Student (bar per institution, reused as spending proxy)
     *
     * @param institutionIds   comma-separated institution UUIDs
     * @param institutionCodes comma-separated institution codes
     * @param periodId         reporting period UUID
     *
     * @return {@link FinanceDashboardDto}
     */
    @GetMapping("/finance")
    public ResponseEntity<FinanceDashboardDto> getFinance(
            @RequestParam String institutionIds,
            @RequestParam String institutionCodes,
            @RequestParam UUID periodId) {

        List<UUID>   ids   = parseUuids(institutionIds);
        List<String> codes = parseLabels(institutionCodes);

        return ResponseEntity.ok(
                dashboardService.buildFinanceDashboard(ids, codes, periodId));
    }

    // ── 5. GET /api/dashboard/hr ──────────────────────────────────────────
    /**
     * Returns HR dashboard widgets:
     * - Teaching vs Administrative Headcount (bar)
     * - Teaching Load (bar)
     * - Absenteeism Rate (bar)
     *
     * @param institutionIds   comma-separated institution UUIDs
     * @param institutionCodes comma-separated institution codes
     * @param periodId         reporting period UUID (for load + absenteeism)
     *
     * @return {@link HrDashboardDto}
     */
    @GetMapping("/hr")
    public ResponseEntity<HrDashboardDto> getHr(
            @RequestParam String institutionIds,
            @RequestParam String institutionCodes,
            @RequestParam UUID periodId) {

        List<UUID>   ids   = parseUuids(institutionIds);
        List<String> codes = parseLabels(institutionCodes);

        return ResponseEntity.ok(
                dashboardService.buildHrDashboard(ids, codes, periodId));
    }

    // ── 6. GET /api/dashboard/alerts ──────────────────────────────────────
    /**
     * Returns:
     * - Alert list (open alerts, optionally filtered by institution)
     * - Alert summary grouped by severity
     *
     * @param institutionId optional institution UUID filter
     *
     * @return {@link AlertsDashboardResponse}
     */
    @GetMapping("/alerts")
    public ResponseEntity<AlertsDashboardResponse> getAlerts(
            @RequestParam(required = false) UUID institutionId) {

        AlertListDto    alertList = dashboardService.buildAlertList(institutionId);
        AlertSummaryDto summary   = dashboardService.buildAlertSummary();

        return ResponseEntity.ok(new AlertsDashboardResponse(alertList, summary));
    }

    // ── 7. GET /api/dashboard/insights ────────────────────────────────────
    /**
     * Returns:
     * - Executive summary insight card
     * - Top proposed recommendations (max 5)
     *
     * @param institutionId institution UUID in scope
     * @param periodId      reporting period UUID
     *
     * @return {@link InsightsDashboardDto}
     */
    @GetMapping("/insights")
    public ResponseEntity<InsightsDashboardDto> getInsights(
            @RequestParam UUID institutionId,
            @RequestParam UUID periodId) {

        return ResponseEntity.ok(
                dashboardService.buildInsightsDashboard(institutionId, periodId));
    }

    // ── Inline response wrapper for /alerts ───────────────────────────────

    /**
     * Combines alert list and severity summary into one response body.
     */
    public record AlertsDashboardResponse(AlertListDto alertList, AlertSummaryDto summary) {}

    // ── Helpers ───────────────────────────────────────────────────────────

    /** Parse "uuid1,uuid2,uuid3" → List<UUID> */
    private List<UUID> parseUuids(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }

    /** Parse "ENIT,FSEG,IHEC" or "2025-S1,2025-S2" → List<String> */
    private List<String> parseLabels(String csv) {
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
