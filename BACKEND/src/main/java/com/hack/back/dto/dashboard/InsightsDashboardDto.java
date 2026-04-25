package com.hack.back.dto.dashboard;

import lombok.Builder;
import lombok.Data;

/** Composite response for GET /api/dashboard/insights */
@Data
@Builder
public class InsightsDashboardDto {

    private InsightCardDto executiveSummary;
    private RecommendationListDto topRecommendations;
}
