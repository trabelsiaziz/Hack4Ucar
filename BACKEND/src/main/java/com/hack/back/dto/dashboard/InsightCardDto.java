package com.hack.back.dto.dashboard;

import lombok.Builder;
import lombok.Data;

/**
 * Insight-card widget response.
 * Matches the shape: { widgetType, title, summaryText, confidenceScore }
 */
@Data
@Builder
public class InsightCardDto {

    private final String widgetType = "insight-card";

    private String title;
    private String summaryText;
    private Double confidenceScore;
    private String insightType;
}
