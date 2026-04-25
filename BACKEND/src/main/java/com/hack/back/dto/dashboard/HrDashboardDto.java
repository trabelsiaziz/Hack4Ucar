package com.hack.back.dto.dashboard;

import lombok.Builder;
import lombok.Data;

/** Composite response for GET /api/dashboard/hr */
@Data
@Builder
public class HrDashboardDto {

    /** Teaching vs Administrative headcount (bar) */
    private ChartDto headcountChart;

    /** Teaching load by institution or org unit (bar) */
    private ChartDto teachingLoadChart;

    /** Absenteeism rate by institution (bar) */
    private ChartDto absenteeismChart;
}
