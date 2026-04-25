package com.hack.back.service.kpi;

import com.hack.back.dto.dashboard.ChartDto;
import com.hack.back.dto.dashboard.StatCardDto;
import com.hack.back.repository.HrFactRepository;
import com.hack.back.repository.StaffMemberRepository;
import com.hack.back.repository.TeacherRepository;
import com.hack.back.repository.TeachingAssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Computes HR KPIs:
 *   TEACHING_HEADCOUNT, ADMINISTRATIVE_HEADCOUNT, ABSENTEEISM_RATE, TEACHING_LOAD
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HrKpiService {

    private final TeacherRepository           teacherRepo;
    private final StaffMemberRepository       staffMemberRepo;
    private final HrFactRepository            hrFactRepo;
    private final TeachingAssignmentRepository teachingAssignmentRepo;

    // ── TEACHING_HEADCOUNT ───────────────────────────────────────────────

    /**
     * TEACHING_HEADCOUNT = count(teachers where employment_status = 'active'
     *                            and institution = :institutionId)
     */
    public long computeTeachingHeadcount(UUID institutionId) {
        return teacherRepo.countByInstitution_InstitutionIdAndEmploymentStatus(institutionId, "active");
    }

    public StatCardDto teachingHeadcountCard(UUID institutionId, String institutionCode,
                                             String periodLabel) {
        long value = computeTeachingHeadcount(institutionId);
        return StatCardDto.builder()
                .title("Teaching Headcount")
                .value((double) value)
                .unit("count")
                .trend("stable")
                .delta(null)
                .periodId(periodLabel)
                .scope(StatCardDto.ScopeDto.builder()
                        .scopeType("institution")
                        .scopeId(institutionCode)
                        .build())
                .build();
    }

    // ── ADMINISTRATIVE_HEADCOUNT ─────────────────────────────────────────

    public long computeAdministrativeHeadcount(UUID institutionId) {
        return staffMemberRepo.countByInstitution_InstitutionIdAndEmploymentStatus(institutionId, "active");
    }

    public StatCardDto adminHeadcountCard(UUID institutionId, String institutionCode,
                                          String periodLabel) {
        long value = computeAdministrativeHeadcount(institutionId);
        return StatCardDto.builder()
                .title("Administrative Headcount")
                .value((double) value)
                .unit("count")
                .trend("stable")
                .delta(null)
                .periodId(periodLabel)
                .scope(StatCardDto.ScopeDto.builder()
                        .scopeType("institution")
                        .scopeId(institutionCode)
                        .build())
                .build();
    }

    // ── ABSENTEEISM_RATE ─────────────────────────────────────────────────

    /**
     * ABSENTEEISM_RATE = AVG(hr_fact.value where hr_metric = 'absenteeism_rate')
     */
    public Double computeAbsenteeismRate(UUID institutionId, UUID periodId) {
        return hrFactRepo.avgValueByInstitutionPeriodAndMetric(institutionId, periodId, "absenteeism_rate");
    }

    public StatCardDto absenteeismRateCard(UUID institutionId, String institutionCode,
                                           UUID periodId, String periodLabel) {
        Double value = computeAbsenteeismRate(institutionId, periodId);
        return StatCardDto.builder()
                .title("Absenteeism Rate")
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

    // ── TEACHING_LOAD ─────────────────────────────────────────────────────

    /**
     * TEACHING_LOAD = SUM(hours_assigned) for institution in period.
     */
    public Double computeTeachingLoad(UUID institutionId, UUID periodId) {
        return teachingAssignmentRepo.sumHoursByInstitutionAndPeriod(institutionId, periodId);
    }

    // ── Comparison charts ────────────────────────────────────────────────

    /** Bar: Teaching vs Administrative headcount per institution. */
    public ChartDto headcountComparisonChart(List<UUID> institutionIds,
                                             List<String> institutionCodes) {
        List<Double> teachingData = new ArrayList<>();
        List<Double> adminData    = new ArrayList<>();
        for (UUID id : institutionIds) {
            teachingData.add((double) computeTeachingHeadcount(id));
            adminData.add((double) computeAdministrativeHeadcount(id));
        }
        return ChartDto.builder()
                .chartType("bar")
                .title("Teaching vs Administrative Headcount")
                .categories(institutionCodes)
                .series(List.of(
                        ChartDto.SeriesDto.builder()
                                .key("teachingHeadcount").label("Teaching")
                                .data(teachingData).unit("count").build(),
                        ChartDto.SeriesDto.builder()
                                .key("adminHeadcount").label("Administrative")
                                .data(adminData).unit("count").build()
                ))
                .build();
    }

    /** Bar: Teaching load per institution in a period. */
    public ChartDto teachingLoadChart(List<UUID> institutionIds,
                                      List<String> institutionCodes,
                                      UUID periodId) {
        List<Double> data = new ArrayList<>();
        for (UUID id : institutionIds) {
            Double v = computeTeachingLoad(id, periodId);
            data.add(v != null ? v : 0.0);
        }
        return ChartDto.builder()
                .chartType("bar")
                .title("Teaching Load by Institution")
                .categories(institutionCodes)
                .series(List.of(ChartDto.SeriesDto.builder()
                        .key("teachingLoad").label("Teaching Load")
                        .data(data).unit("hours").build()))
                .build();
    }

    /** Bar: Absenteeism rate per institution in a period. */
    public ChartDto absenteeismRateComparisonChart(List<UUID> institutionIds,
                                                   List<String> institutionCodes,
                                                   UUID periodId) {
        List<Double> data = new ArrayList<>();
        for (UUID id : institutionIds) {
            Double v = computeAbsenteeismRate(id, periodId);
            data.add(v != null ? v : 0.0);
        }
        return ChartDto.builder()
                .chartType("bar")
                .title("Absenteeism Rate by Institution")
                .categories(institutionCodes)
                .series(List.of(ChartDto.SeriesDto.builder()
                        .key("absenteeismRate").label("Absenteeism Rate")
                        .data(data).unit("%").build()))
                .build();
    }

    /** Line: Absenteeism rate trend for one institution. */
    public ChartDto absenteeismTrendChart(UUID institutionId,
                                          List<UUID> periodIds,
                                          List<String> periodLabels) {
        List<Double> data = new ArrayList<>();
        for (UUID pid : periodIds) {
            Double v = computeAbsenteeismRate(institutionId, pid);
            data.add(v != null ? v : 0.0);
        }
        return ChartDto.builder()
                .chartType("line")
                .title("Absenteeism Rate Trend")
                .xAxis(periodLabels)
                .series(List.of(ChartDto.SeriesDto.builder()
                        .key("absenteeismRate").label("Absenteeism Rate")
                        .data(data).unit("%").build()))
                .build();
    }
}
