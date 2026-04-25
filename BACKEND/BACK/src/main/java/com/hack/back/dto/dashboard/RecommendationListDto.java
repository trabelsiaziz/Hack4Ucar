package com.hack.back.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Recommendation-list widget response.
 * Matches the shape: { widgetType, title, items[] }
 */
@Data
@Builder
public class RecommendationListDto {

    private final String widgetType = "recommendation-list";

    private String title;
    private List<RecommendationItemDto> items;

    @Data
    @Builder
    public static class RecommendationItemDto {
        private UUID recommendationId;
        /** low | medium | high | urgent */
        private String priority;
        private String category;
        private String title;
        private String description;
        /** proposed | accepted | rejected | completed */
        private String status;
    }
}
