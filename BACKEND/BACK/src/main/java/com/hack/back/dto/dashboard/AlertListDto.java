package com.hack.back.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Alert-list widget response.
 * Matches the shape: { widgetType, title, items[] }
 */
@Data
@Builder
public class AlertListDto {

    private final String widgetType = "alert-list";

    private String title;
    private List<AlertItemDto> items;

    @Data
    @Builder
    public static class AlertItemDto {
        private UUID alertId;
        /** low | medium | high | critical */
        private String severity;
        private String alertType;
        private String message;
        private String institutionId;
        private String periodId;
        /** open | acknowledged | closed */
        private String status;
        private Double observedValue;
        private Double thresholdValue;
    }
}
