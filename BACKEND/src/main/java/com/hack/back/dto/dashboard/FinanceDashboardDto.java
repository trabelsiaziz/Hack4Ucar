package com.hack.back.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/** Composite response for GET /api/dashboard/finance */
@Data
@Builder
public class FinanceDashboardDto {

    /** Allocated vs Consumed per institution (stacked-bar) */
    private ChartDto allocatedVsConsumedChart;

    /** Spending by department/org-unit (bar) */
    private ChartDto spendingByDepartmentChart;
}
