package com.hack.back.service.kpi;

import com.hack.back.dto.dashboard.ChartDto;
import com.hack.back.dto.dashboard.StatCardDto;
import com.hack.back.repository.AssessmentResultRepository;
import com.hack.back.repository.AttendanceRecordRepository;
import com.hack.back.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Computes Academic KPIs:
 *   SUCCESS_RATE, ATTENDANCE_RATE, DROPOUT_RATE, EXAM_RESULTS_AVG
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AcademicKpiService {

    private final AssessmentResultRepository assessmentResultRepo;
    private final AttendanceRecordRepository attendanceRecordRepo;
    private final EnrollmentRepository enrollmentRepo;

    // ── SUCCESS_RATE ─────────────────────────────────────────────────────

    /**
     * SUCCESS_RATE = (passed / assessed) * 100
     */
    public Double computeSuccessRate(UUID institutionId, UUID periodId) {
        long total  = assessmentResultRepo.countByInstitutionAndPeriod(institutionId, periodId);
        long passed = assessmentResultRepo.countPassedByInstitutionAndPeriod(institutionId, periodId);
        if (total == 0) return null;
        return (double) passed / total * 100.0;
    }

    public StatCardDto successRateCard(UUID institutionId, String institutionCode,
                                       UUID periodId, String periodLabel) {
        Double value = computeSuccessRate(institutionId, periodId);
        return StatCardDto.builder()
                .title("Success Rate")
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

    // ── ATTENDANCE_RATE ──────────────────────────────────────────────────

    /**
     * ATTENDANCE_RATE = AVG(attendance_rate) for STUDENT records
     */
    public Double computeAttendanceRate(UUID institutionId, UUID periodId) {
        return attendanceRecordRepo.avgAttendanceRateByInstitutionAndPeriodAndType(
                institutionId, periodId, "STUDENT");
    }

    public StatCardDto attendanceRateCard(UUID institutionId, String institutionCode,
                                          UUID periodId, String periodLabel) {
        Double value = computeAttendanceRate(institutionId, periodId);
        return StatCardDto.builder()
                .title("Attendance Rate")
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

    // ── DROPOUT_RATE ─────────────────────────────────────────────────────

    /**
     * DROPOUT_RATE = (dropped / enrolled) * 100
     */
    public Double computeDropoutRate(UUID institutionId, UUID periodId) {
        long total   = enrollmentRepo.countByInstitutionAndPeriod(institutionId, periodId);
        long dropped = enrollmentRepo.countDroppedByInstitutionAndPeriod(institutionId, periodId);
        if (total == 0) return null;
        return (double) dropped / total * 100.0;
    }

    public StatCardDto dropoutRateCard(UUID institutionId, String institutionCode,
                                       UUID periodId, String periodLabel) {
        Double value = computeDropoutRate(institutionId, periodId);
        return StatCardDto.builder()
                .title("Dropout Rate")
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

    // ── EXAM_RESULTS_AVG ─────────────────────────────────────────────────

    /**
     * EXAM_RESULTS_AVG = AVG(score)
     */
    public Double computeExamResultsAvg(UUID institutionId, UUID periodId) {
        return assessmentResultRepo.avgScoreByInstitutionAndPeriod(institutionId, periodId);
    }

    public StatCardDto examResultsAvgCard(UUID institutionId, String institutionCode,
                                          UUID periodId, String periodLabel) {
        Double value = computeExamResultsAvg(institutionId, periodId);
        return StatCardDto.builder()
                .title("Exam Results Avg")
                .value(value)
                .unit("score")
                .trend("stable")
                .delta(null)
                .periodId(periodLabel)
                .scope(StatCardDto.ScopeDto.builder()
                        .scopeType("institution")
                        .scopeId(institutionCode)
                        .build())
                .build();
    }

    // ── Comparison chart: multiple institutions, one period ──────────────

    /**
     * Build a bar-chart comparing successRate across institutions.
     *
     * @param institutionIds   list of institution UUIDs
     * @param institutionCodes list of human-readable codes (same order)
     * @param periodId         target period
     */
    public ChartDto successRateComparisonChart(List<UUID> institutionIds,
                                               List<String> institutionCodes,
                                               UUID periodId) {
        List<Double> data = new ArrayList<>();
        for (UUID id : institutionIds) {
            Double v = computeSuccessRate(id, periodId);
            data.add(v != null ? v : 0.0);
        }
        return ChartDto.builder()
                .chartType("bar")
                .title("Success Rate by Institution")
                .categories(institutionCodes)
                .series(List.of(ChartDto.SeriesDto.builder()
                        .key("successRate")
                        .label("Success Rate")
                        .data(data)
                        .unit("%")
                        .build()))
                .build();
    }

    /** Bar-chart comparing attendanceRate across institutions. */
    public ChartDto attendanceRateComparisonChart(List<UUID> institutionIds,
                                                  List<String> institutionCodes,
                                                  UUID periodId) {
        List<Double> data = new ArrayList<>();
        for (UUID id : institutionIds) {
            Double v = computeAttendanceRate(id, periodId);
            data.add(v != null ? v : 0.0);
        }
        return ChartDto.builder()
                .chartType("bar")
                .title("Attendance Rate by Institution")
                .categories(institutionCodes)
                .series(List.of(ChartDto.SeriesDto.builder()
                        .key("attendanceRate")
                        .label("Attendance Rate")
                        .data(data)
                        .unit("%")
                        .build()))
                .build();
    }

    // ── Trend chart: one institution, multiple periods ───────────────────

    /**
     * Build a line-chart showing successRate trend for one institution
     * over a list of periods.
     *
     * @param institutionId  target institution
     * @param periodIds      ordered list of period UUIDs
     * @param periodLabels   ordered list of period labels (same order)
     */
    public ChartDto successRateTrendChart(UUID institutionId,
                                          List<UUID> periodIds,
                                          List<String> periodLabels) {
        List<Double> data = new ArrayList<>();
        for (UUID pid : periodIds) {
            Double v = computeSuccessRate(institutionId, pid);
            data.add(v != null ? v : 0.0);
        }
        return ChartDto.builder()
                .chartType("line")
                .title("Success Rate Trend")
                .xAxis(periodLabels)
                .series(List.of(ChartDto.SeriesDto.builder()
                        .key("successRate")
                        .label("Success Rate")
                        .data(data)
                        .unit("%")
                        .build()))
                .build();
    }

    /** Line-chart: attendanceRate trend for one institution over multiple periods. */
    public ChartDto attendanceRateTrendChart(UUID institutionId,
                                             List<UUID> periodIds,
                                             List<String> periodLabels) {
        List<Double> data = new ArrayList<>();
        for (UUID pid : periodIds) {
            Double v = computeAttendanceRate(institutionId, pid);
            data.add(v != null ? v : 0.0);
        }
        return ChartDto.builder()
                .chartType("line")
                .title("Attendance Rate Trend")
                .xAxis(periodLabels)
                .series(List.of(ChartDto.SeriesDto.builder()
                        .key("attendanceRate")
                        .label("Attendance Rate")
                        .data(data)
                        .unit("%")
                        .build()))
                .build();
    }
}
