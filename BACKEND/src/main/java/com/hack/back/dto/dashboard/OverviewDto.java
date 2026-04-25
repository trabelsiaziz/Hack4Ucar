package com.hack.back.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Composite response for GET /api/dashboard/overview.
 * Contains top KPI stat-cards, open alert count, and recommendation count.
 */
@Data
@Builder
public class OverviewDto {

    private List<StatCardDto> kpiCards;
    private long openAlertsCount;
    private long recommendationsCount;
}
