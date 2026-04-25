package com.hack.back.service.kpi;

import com.hack.back.dto.dashboard.ChartDto;
import com.hack.back.dto.dashboard.StatCardDto;
import com.hack.back.repository.BudgetFactRepository;
import com.hack.back.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Computes Finance KPIs:
 *   ALLOCATED_BUDGET, CONSUMED_BUDGET, BUDGET_EXECUTION_RATE, COST_PER_STUDENT
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinanceKpiService {

    private final BudgetFactRepository budgetFactRepo;
    private final EnrollmentRepository enrollmentRepo;

    // ── ALLOCATED_BUDGET ─────────────────────────────────────────────────

    public BigDecimal computeAllocatedBudget(UUID institutionId, UUID periodId) {
        return budgetFactRepo.sumByInstitutionAndPeriodAndType(institutionId, periodId, "allocated");
    }

    // ── CONSUMED_BUDGET ──────────────────────────────────────────────────

    public BigDecimal computeConsumedBudget(UUID institutionId, UUID periodId) {
        return budgetFactRepo.sumByInstitutionAndPeriodAndType(institutionId, periodId, "consumed");
    }

    // ── BUDGET_EXECUTION_RATE ─────────────────────────────────────────────

    /**
     * BUDGET_EXECUTION_RATE = (consumed / allocated) * 100
     * Returns null if allocated == 0.
     */
    public Double computeBudgetExecutionRate(UUID institutionId, UUID periodId) {
        BigDecimal allocated = computeAllocatedBudget(institutionId, periodId);
        BigDecimal consumed  = computeConsumedBudget(institutionId, periodId);
        if (allocated == null || allocated.compareTo(BigDecimal.ZERO) == 0) return null;
        return consumed.divide(allocated, 6, RoundingMode.HALF_UP)
                       .multiply(BigDecimal.valueOf(100))
                       .doubleValue();
    }

    public StatCardDto budgetExecutionRateCard(UUID institutionId, String institutionCode,
                                               UUID periodId, String periodLabel) {
        Double value = computeBudgetExecutionRate(institutionId, periodId);
        return StatCardDto.builder()
                .title("Budget Execution Rate")
                .value(value)
                .unit("%")
                .trend("stable")
                .delta(null)
                .periodId(periodLabel)
                .scope(StatCardDto.ScopeDto.builder()
                        .scopeType("institution")
                        .scopeId(institutionCode)
                        .build())
                .build();
    }

    // ── COST_PER_STUDENT ─────────────────────────────────────────────────

    /**
     * COST_PER_STUDENT = consumed / total_enrolled_students
     * Returns null if enrollment count == 0.
     */
    public Double computeCostPerStudent(UUID institutionId, UUID periodId) {
        BigDecimal consumed = computeConsumedBudget(institutionId, periodId);
        long enrolled       = enrollmentRepo.countByInstitutionAndPeriod(institutionId, periodId);
        if (enrolled == 0 || consumed == null) return null;
        return consumed.divide(BigDecimal.valueOf(enrolled), 4, RoundingMode.HALF_UP).doubleValue();
    }

    public StatCardDto costPerStudentCard(UUID institutionId, String institutionCode,
                                          UUID periodId, String periodLabel) {
        Double value = computeCostPerStudent(institutionId, periodId);
        return StatCardDto.builder()
                .title("Cost per Student")
                .value(value)
                .unit("TND")
                .trend("stable")
                .delta(null)
                .periodId(periodLabel)
                .scope(StatCardDto.ScopeDto.builder()
                        .scopeType("institution")
                        .scopeId(institutionCode)
                        .build())
                .build();
    }

    // ── Comparison chart: budget execution rate across institutions ───────

    public ChartDto budgetExecutionComparisonChart(List<UUID> institutionIds,
                                                   List<String> institutionCodes,
                                                   UUID periodId) {
        List<Double> data = new ArrayList<>();
        for (UUID id : institutionIds) {
            Double v = computeBudgetExecutionRate(id, periodId);
            data.add(v != null ? v : 0.0);
        }
        return ChartDto.builder()
                .chartType("bar")
                .title("Budget Execution Rate by Institution")
                .categories(institutionCodes)
                .series(List.of(ChartDto.SeriesDto.builder()
                        .key("budgetExecutionRate")
                        .label("Budget Execution Rate")
                        .data(data)
                        .unit("%")
                        .build()))
                .build();
    }

    /** Stacked-bar: Allocated vs Consumed per institution. */
    public ChartDto allocatedVsConsumedChart(List<UUID> institutionIds,
                                             List<String> institutionCodes,
                                             UUID periodId) {
        List<Double> allocatedData = new ArrayList<>();
        List<Double> consumedData  = new ArrayList<>();

        for (UUID id : institutionIds) {
            BigDecimal alloc = computeAllocatedBudget(id, periodId);
            BigDecimal cons  = computeConsumedBudget(id, periodId);
            allocatedData.add(alloc != null ? alloc.doubleValue() : 0.0);
            consumedData.add(cons  != null ? cons.doubleValue()  : 0.0);
        }

        return ChartDto.builder()
                .chartType("stacked-bar")
                .title("Allocated vs Consumed Budget")
                .categories(institutionCodes)
                .series(List.of(
                        ChartDto.SeriesDto.builder()
                                .key("allocatedBudget").label("Allocated")
                                .data(allocatedData).unit("TND").build(),
                        ChartDto.SeriesDto.builder()
                                .key("consumedBudget").label("Consumed")
                                .data(consumedData).unit("TND").build()
                ))
                .build();
    }

    /** Bar: Budget execution rate trend over periods for one institution. */
    public ChartDto budgetExecutionTrendChart(UUID institutionId,
                                              List<UUID> periodIds,
                                              List<String> periodLabels) {
        List<Double> data = new ArrayList<>();
        for (UUID pid : periodIds) {
            Double v = computeBudgetExecutionRate(institutionId, pid);
            data.add(v != null ? v : 0.0);
        }
        return ChartDto.builder()
                .chartType("line")
                .title("Budget Execution Trend")
                .xAxis(periodLabels)
                .series(List.of(ChartDto.SeriesDto.builder()
                        .key("budgetExecutionRate")
                        .label("Budget Execution Rate")
                        .data(data)
                        .unit("%")
                        .build()))
                .build();
    }
}
